package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

/**
 * Abstract base class for ray tracers. It defines the structure for tracing rays through a scene to determine the color of pixels in the rendered image.
 */
abstract class RayTracerBase {
    /** The scene to be rendered, containing geometries and lighting information. */
    protected final Scene _scene;

    /** Normal vector at the intersection point. */
    protected Vector normal;

    /** Vector from the camera/ray origin direction. */
    protected Vector v;

    /** Dot product of normal and view vector. */
    protected double nv;

    /** Direction vector from light source to the point. */
    protected Vector l;

    /** Dot product of normal and light vector. */
    protected double nl;

    /** Light intensity at the intersection point. */
    protected Color lightIntensity;

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

    /**
     * Preprocesses data related to an intersection.
     * @param intersection the intersection to preprocess
     * @param ray the ray that created the intersection
     * @return true if the intersection can be shaded
     */
    protected boolean preprocessIntersection(Intersectable.Intersection intersection, Ray ray) {
        normal = intersection.geometry.getNormal(intersection.point);
        v = ray.direction();
        nv = alignZero(normal.dotProduct(v));

        return nv != 0;
    }

    /**
     * Preprocesses data related to a light source.
     * @param intersection the intersection to shade
     * @param lightSource the light source
     * @return true if the light source affects the intersection
     */
    protected boolean preprocessLightSource(Intersectable.Intersection intersection, LightSource lightSource) {
        l = lightSource.getL(intersection.point);
        nl = alignZero(normal.dotProduct(l));

        if (nl * nv <= 0) {
            return false;
        }

        lightIntensity = lightSource.getIntensity(intersection.point);
        return true;
    }
}