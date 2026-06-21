package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.*;
import scene.Scene;
import java.util.List;
import static primitives.Util.alignZero;

class SimpleRayTracer extends RayTracerBase {
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final Double3 INITIAL_K = Double3.ONE;
    private static final Double3 MIN_CALC_COLOR_K = new Double3(0.001);

    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) return _scene.background;
        var closest = ray.findClosestIntersection(intersections);
        return closest == null ? _scene.background : calcColor(closest, ray);
    }

    private Color calcColor(Intersectable.Intersection intersection, Ray ray) {
        Color ambient = _scene.ambientLight.getIntensity().scale(intersection.material.kA);
        IntersectionCtx ctx = preprocessIntersection(intersection, ray);
        return ctx == null ? ambient : ambient.add(calcColor(intersection, ctx, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    private Color calcColor(Intersectable.Intersection intersection, IntersectionCtx ctx, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, ctx, k);
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, ctx, level, k));
    }

    private Color calcGlobalEffects(Intersectable.Intersection intersection, IntersectionCtx ctx, int level, Double3 k) {
        Material m = intersection.material;
        return calcGlobalEffect(constructReflectionRay(intersection, ctx), level, k, m.kR)
                .add(calcGlobalEffect(constructTransparencyRay(intersection, ctx), level, k, m.kT));
    }

    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) return _scene.background.scale(kx);
        var closest = ray.findClosestIntersection(intersections);
        if (closest == null) return _scene.background.scale(kx);
        IntersectionCtx ctx = preprocessIntersection(closest, ray);
        return ctx == null ? Color.BLACK : calcColor(closest, ctx, level - 1, kkx).scale(kx);
    }

    private Ray constructReflectionRay(Intersectable.Intersection intersection, IntersectionCtx ctx) {
        Vector r = ctx.v().add(ctx.normal().scale(-2 * ctx.v().dotProduct(ctx.normal()))).normalize();
        return new Ray(intersection.point, r, ctx.normal());
    }

    private Ray constructTransparencyRay(Intersectable.Intersection intersection, IntersectionCtx ctx) {
        return new Ray(intersection.point, ctx.v(), ctx.normal());
    }

    private Color calcLocalEffects(Intersectable.Intersection intersection, IntersectionCtx ctx, Double3 k) {
        Color color = intersection.geometry.getEmission();
        for (LightSource ls : _scene.lights) {
            LightCtx lCtx = preprocessLightSource(intersection, ls, ctx);
            if (lCtx == null) continue;
            Double3 ktr = transparency(intersection, ctx, lCtx);
            if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                color = color.add(lCtx.intensity().scale(ktr).scale(
                        calcDiffusive(intersection, lCtx).add(calcSpecular(intersection, ctx, lCtx))));
            }
        }
        return color;
    }

    private Double3 transparency(Intersectable.Intersection intersection, IntersectionCtx ctx, LightCtx lCtx) {
        LightSource lightSource = lCtx.source();
        List<Point> targetArea = lightSource.getTargetAreaPoints(intersection.point);
        if (targetArea == null) {
            Double3 ktr = Double3.ONE;
            Ray shadowRay = new Ray(intersection.point, lCtx.l().scale(-1), ctx.normal());
            double lightDist = lightSource.getDistance(intersection.point);
            var shadowIntersections = _scene.geometries.calcIntersections(shadowRay);
            if (shadowIntersections == null || shadowIntersections.isEmpty()) return ktr;
            for (Intersectable.Intersection si : shadowIntersections) {
                if (si.point.distance(shadowRay.origin()) < lightDist) {
                    ktr = ktr.product(si.geometry.getMaterial().kT);
                    if (ktr.isLowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
                }
            }
            return ktr;
        }
        Double3 totalKtr = Double3.ZERO;
        for (Point lightPoint : targetArea) {
            Vector lightDirection = lightPoint.subtract(intersection.point).normalize();
            Ray shadowRay = new Ray(intersection.point, lightDirection, ctx.normal());
            double lightDist = lightPoint.distance(intersection.point);
            var shadowIntersections = _scene.geometries.calcIntersections(shadowRay);
            Double3 ktr = Double3.ONE;
            if (shadowIntersections != null && !shadowIntersections.isEmpty()) {
                for (Intersectable.Intersection si : shadowIntersections) {
                    if (si.point.distance(shadowRay.origin()) < lightDist) {
                        ktr = ktr.product(si.geometry.getMaterial().kT);
                        if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                            ktr = Double3.ZERO;
                            break;
                        }
                    }
                }
            }
            totalKtr = totalKtr.add(ktr);
        }
        return totalKtr.product(new Double3(1.0 / targetArea.size()));
    }

    private Double3 calcDiffusive(Intersectable.Intersection intersection, LightCtx lCtx) {
        return intersection.material.kD.scale(Math.abs(lCtx.nl()));
    }

    private Double3 calcSpecular(Intersectable.Intersection intersection, IntersectionCtx ctx, LightCtx lCtx) {
        Material m = intersection.material;
        Vector r = lCtx.l().add(ctx.normal().scale(-2 * lCtx.nl())).normalize();
        double minusVR = alignZero(-ctx.v().dotProduct(r));
        return minusVR <= 0 ? Double3.ZERO : m.kS.scale(Math.pow(minusVR, m.nShininess));
    }
}