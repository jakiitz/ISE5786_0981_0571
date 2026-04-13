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
    public Vector getNormal(primitives.Point point) {
        //direction of the cylinder's axis
        Vector axisDir = _axis.direction();
        //origin of the cylinder's axis
        primitives.Point axisOrigin = _axis.origin();
        Vector p0ToP = point.subtract(axisOrigin);
        //projection of p0ToP on the axis direction
        double t = axisDir.dotProduct(p0ToP);
        //if the point is on the base
        if (Math.abs(t) < 1e-10) {
            return axisDir.normalize();
        }
        //if the point is on the top
        if (Math.abs(t - _height) < 1e-10) {
            return axisDir.normalize();
        }
        //the point is on the curved surface
        primitives.Point o = axisOrigin.add(axisDir.scale(t));
        return point.subtract(o).normalize();
    }
}