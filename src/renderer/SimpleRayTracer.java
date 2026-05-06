package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import geometries.api.Intersectable;
import scene.Scene;

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
     * @return the color at the intersection
     */
    private Color calcColor(Intersectable.Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(intersection.geometry.getEmission());
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
        return closestIntersection == null ? _scene.background : calcColor(closestIntersection);
    }
}
