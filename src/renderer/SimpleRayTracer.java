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
import static java.lang.Math.max;
import static primitives.Util.alignZero;

/**
 * SimpleRayTracer is a basic implementation of a ray tracer that extends the RayTracerBase class.
 * It provides a simple traceRay method that currently returns null, which can be implemented to perform actual ray tracing.
 */
class SimpleRayTracer extends RayTracerBase {

    /** Constructor for SimpleRayTracer that takes a Scene object and passes it to the superclass constructor. */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Calculates the color at an intersection point.
     * @param intersection the intersection object
     * @param ray the ray that created the intersection
     * @return the color at the intersection
     */
    private Color calcColor(Intersectable.Intersection intersection, Ray ray) {
        Color color = _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(intersection.geometry.getEmission());

        return preprocessIntersection(intersection, ray)
                ? color.add(calcColorLocalEffects(intersection))
                : color;
    }

    /**
     * Calculates the local lighting effects at an intersection point.
     * @param intersection the intersection object
     * @return the local lighting color contribution
     */
    private Color calcColorLocalEffects(Intersectable.Intersection intersection) {
        Color color = Color.BLACK;

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                color = color.add(
                        lightIntensity.scale(
                                calcDiffusive(intersection).add(calcSpecular(intersection))
                        )
                );
            }
        }

        return color;
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