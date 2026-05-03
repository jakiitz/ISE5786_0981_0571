package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
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
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }

    /**
     * Traces a ray through the scene and calculates the color at the intersection point.
     * @param ray the ray to be traced
     * @return
     */
    @Override
    Color traceRay(Ray ray) {
        var intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null) {
            return Color.BLACK; // No intersections, return black
        }
        // For simplicity, we take the first intersection point to calculate the color
        Point closestIntersection = intersections.get(0);
        return calcColor(closestIntersection);
    }
}
