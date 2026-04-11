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

    /**
     * Gets the normal vector to the tube at a given point.
     * @param point a point on the surface of the tube
     * @return the normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // P0 = axis head, v = axis direction
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // t = v * (P - P0)
        Vector delta = point.subtract(p0);
        double t = v.dotProduct(delta);

        // Check if the projection is exactly on the head of the ray (t=0)
        // In this case, O = P0
        if (Util.isZero(t)) {
            return delta.normalize();
        }

        // O = P0 + t * v
        Point o = p0.add(v.scale(t));

        // n = normalize(P - O)
        return point.subtract(o).normalize();
    }
}