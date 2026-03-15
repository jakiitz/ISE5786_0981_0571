package geometries.impl;

import primitives.Ray;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder.
 */
public final class Cylinder extends Tube {
    private final double _height;

    /**
     * Constructor for Cylinder
     * @param radius
     * @param axis
     * @param height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public Vector getNormal(primitives.Point point) { return null; }
}