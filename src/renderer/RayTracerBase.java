package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

/**
 * Abstract base class for ray tracers.
 * All precomputed intersection data is returned as immutable records so that
 * each thread in a multi-threaded render has its own local copy — no shared
 * mutable state.
 */
abstract class RayTracerBase {

    /** The scene to be rendered. */
    protected final Scene _scene;

    /** Precomputed values for a single intersection (one per ray, thread-local). */
    protected record IntersectionCtx(Vector normal, Vector v, double nv) {}

    /** Precomputed values for a single light-source contribution (one per light, thread-local). */
    protected record LightCtx(LightSource source, Vector l, double nl, Color intensity) {}

    RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /** Traces a ray through the scene and returns the resulting pixel colour. */
    abstract Color traceRay(Ray ray);

    /**
     * Computes the intersection context for the given hit.
     * @return a context record, or {@code null} if the ray grazes the surface (nv == 0)
     */
    protected IntersectionCtx preprocessIntersection(Intersectable.Intersection intersection, Ray ray) {
        Vector normal = intersection.geometry.getNormal(intersection.point);
        Vector v      = ray.direction();
        double nv     = alignZero(normal.dotProduct(v));
        return nv != 0 ? new IntersectionCtx(normal, v, nv) : null;
    }

    /**
     * Computes the light-source context for the given light at the given hit.
     * @return a context record, or {@code null} if the light is on the wrong side
     */
    protected LightCtx preprocessLightSource(Intersectable.Intersection intersection,
                                              LightSource lightSource,
                                              IntersectionCtx ctx) {
        Vector l  = lightSource.getL(intersection.point);
        double nl = alignZero(ctx.normal().dotProduct(l));
        if (nl * ctx.nv() <= 0) return null;
        return new LightCtx(lightSource, l, nl, lightSource.getIntensity(intersection.point));
    }
}
