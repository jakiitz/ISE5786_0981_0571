package geometries.api;

import primitives.Vector;
import primitives.Point;

/**
 * Abstract base class for geometric objects in 3D space.
 * Provides common functionality for all geometries.
 */
public abstract class Geometry extends Intersectable {

    /**
     * Returns the normal vector to the geometry at the specified point.
     * @param point the point on the geometry
     * @return the normal vector
     */
    public abstract Vector getNormal(Point point);
}
