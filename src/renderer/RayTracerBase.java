package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracers. It defines the structure for tracing rays through a scene to determine the color of pixels in the rendered image.
 */
abstract class RayTracerBase {
    /** The scene to be rendered, containing geometries and lighting information. */
    protected final Scene _scene;
    /** Abstract method to trace a ray through the scene and determine the resulting color.
     * @param ray the ray to be traced
     * @return the color resulting from tracing the ray through the scene
     */
    abstract Color traceRay(Ray ray);
    /** Constructor for RayTracerBase
     * @param scene the scene to be rendered
     */
    RayTracerBase(Scene scene) {
        _scene = scene;
    }
}
