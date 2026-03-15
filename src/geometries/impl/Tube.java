package geometries.impl;

import primitives.*;

/**
 * Class Tube represents an infinite cylinder.
 */
public class Tube extends RadialGeometry {
    protected final Ray _axis;

    /**
     * constructor of tube
     * @param radius
     * @param axis
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) { return null; }
}