package geometries.impl;

import geometries.api.Geometry;
import primitives.Vector;
import primitives.Point;

/**
 * Abstract base class for radial geometries (geometries with a radius).
 */
public abstract class RadialGeometry extends Geometry {

    /** The radius of the geometry */
    protected final double _radius;

    /**
     * Constructor for radial geometry.
     * @param radius the radius
     * @throws IllegalArgumentException if radius is not positive
     */
    public RadialGeometry(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        _radius = radius;
    }

    /**
     * Gets the radius.
     * @return the radius
     */
    public double getRadius() {
        return _radius;
    }
}
