package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static java.lang.Math.abs;
import static primitives.Util.alignZero;

/**
 * SimpleRayTracer is a basic implementation of a ray tracer that extends the RayTracerBase class.
 * It provides a simple traceRay method that currently returns null, which can be implemented to perform actual ray tracing.
 */
class SimpleRayTracer extends RayTracerBase {

    /** Maximum recursion depth for global effects. */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /** Initial attenuation factor for the recursion tree. */
    private static final Double3 INITIAL_K = Double3.ONE;

    /** Minimal attenuation factor below which color contribution is ignored. */
    private static final Double3 MIN_CALC_COLOR_K = new Double3(0.001);

    /** Current light source being processed for local effects. */
    private LightSource lightSource;

    /** Constructor for SimpleRayTracer that takes a Scene object and passes it to the superclass constructor. */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Calculates the color at an intersection point. (Wrapper method)
     * @param intersection the intersection object
     * @param ray the ray that created the intersection
     * @return the color at the intersection
     */
    private Color calcColor(Intersectable.Intersection intersection, Ray ray) {
        // מוסיפים רק את התאורה הסביבתית כבסיס
        Color color = _scene.ambientLight.getIntensity().scale(intersection.material.kA);

        return preprocessIntersection(intersection, ray)
                ? color.add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K))
                : color;
    }

    /**
     * Recursive method to calculate both local and global effects.
     * @param intersection the intersection object
     * @param level the current recursion level
     * @param k the accumulated attenuation factor
     * @return the total color contribution
     */
    private Color calcColor(Intersectable.Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates the global effects (Reflection and Refraction/Transparency) by casting secondary rays.
     * @param intersection the intersection object
     * @param level the current recursion level
     * @param k the accumulated attenuation factor
     * @return the global lighting color contribution
     */
    private Color calcGlobalEffects(Intersectable.Intersection intersection, int level, Double3 k) {
        Color color = Color.BLACK;
        Material material = intersection.material;

        Ray krRay = constructReflectionRay(intersection);
        color = color.add(calcGlobalEffect(krRay, level, k, material.kR));

        Ray ktRay = constructTransparencyRay(intersection);
        color = color.add(calcGlobalEffect(ktRay, level, k, material.kT));

        return color;
    }

    /**
     * Calculates the color of a specific secondary ray (either reflection or transparency).
     * @param ray the secondary ray to trace
     * @param level the current recursion level
     * @param k the accumulated attenuation factor
     * @param kx the material property (kT or kR)
     * @return the color contribution of the ray
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background.scale(kx);
        }

        var closestIntersection = ray.findClosestIntersection(intersections);
        if (closestIntersection == null) {
            return _scene.background.scale(kx);
        }

        return preprocessIntersection(closestIntersection, ray)
                ? calcColor(closestIntersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Calculates the local lighting effects at an intersection point.
     * @param intersection the intersection object
     * @param k the accumulated attenuation factor
     * @return the local lighting color contribution
     */
    private Color calcLocalEffects(Intersectable.Intersection intersection, Double3 k) {
        // מתחילים מחישוב צבע הפליטה של הגוף הנחתך, ולא משחור!
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            this.lightSource = lightSource;
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection);

                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    color = color.add(
                            lightSource.getIntensity(intersection.point).scale(ktr).scale(
                                    calcDiffusive(intersection).add(calcSpecular(intersection))
                            )
                    );
                }
            }
        }

        return color;
    }

    /**
     * Calculates the aggregated transparency attenuation factor for partial shadows.
     * @param intersection the intersection point being shaded
     * @return the accumulated transparency factor between the point and the current light source
     */
    private Double3 transparency(Intersectable.Intersection intersection) {
        Double3 ktr = Double3.ONE;
        Vector lightDirection = l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, lightDirection, normal);
        double lightDistance = lightSource.getDistance(intersection.point);

        var shadowIntersections = _scene.geometries.calcIntersections(shadowRay);

        if (shadowIntersections == null || shadowIntersections.isEmpty()) {
            return ktr;
        }

        for (Intersectable.Intersection shadowIntersection : shadowIntersections) {
            if (shadowIntersection.point.distance(shadowRay.origin()) < lightDistance) {
                ktr = ktr.product(shadowIntersection.geometry.getMaterial().kT);

                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }

        return ktr;
    }

    /**
     * Constructs a reflection ray using the formula: r = v - 2 * (v * n) * n
     */
    private Ray constructReflectionRay(Intersectable.Intersection intersection) {
        Vector r = v.add(normal.scale(-2 * v.dotProduct(normal))).normalize();
        return new Ray(intersection.point, r, normal);
    }

    /**
     * Constructs a transparency ray (continues in the same direction as the original ray).
     */
    private Ray constructTransparencyRay(Intersectable.Intersection intersection) {
        return new Ray(intersection.point, v, normal);
    }

    /**
     * Calculates the diffusive light component.
     * @param intersection the intersection object
     * @return diffusive coefficient
     */
    private Double3 calcDiffusive(Intersectable.Intersection intersection) {
        return intersection.material.kD.scale(abs(nl));
    }

    /**
     * Calculates the specular light component.
     * @param intersection the intersection object
     * @return specular coefficient
     */
    private Double3 calcSpecular(Intersectable.Intersection intersection) {
        Material material = intersection.material;

        Vector r = l.add(normal.scale(-2 * nl)).normalize();
        double minusVR = alignZero(-v.dotProduct(r));

        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        return material.kS.scale(Math.pow(minusVR, material.nShininess));
    }

    /**
     * Traces a ray through the scene and calculates the color at the intersection point.
     * @param ray the ray to be traced
     * @return the color resulting from tracing the ray
     */
    @Override
    Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background;
        }
        var closestIntersection = ray.findClosestIntersection(intersections);
        return closestIntersection == null ? _scene.background : calcColor(closestIntersection, ray);
    }
}