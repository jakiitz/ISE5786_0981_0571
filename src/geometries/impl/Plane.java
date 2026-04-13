package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a plane in 3D space.
 */
public class Plane extends Geometry {

    /** A point on the plane */
    private final Point _p0;
    /** The normal vector to the plane */
    private final Vector _normal;




     /**
     * Constructor for plane from three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @throws IllegalArgumentException if the points are collinear
     */
    public Plane(Point p1, Point p2, Point p3) {
        _p0 = p1;
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        _normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructor to initialize Plane with a point and its normal vector
     * @param q0     point on the plane
     * @param normal normal vector (will be normalized)
     */
    public Plane(Point q0, Vector normal) {
        _p0 = q0;
        _normal = normal.normalize(); // חשוב לנרמל את הוקטור כבר כאן
    }

    /**
     * Gets the normal vector to the plane.
     * @param point a point on the plane (not used for plane)
     * @return the normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /**
     * intersections of plane with ray
     * @param ray the ray to intersect with
     * @return list of intersection points (null if no intersections)
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        double nv = _normal.dotProduct(ray.direction());

        if (isZero(nv)) {
            return null;
        }

        if (_p0.equals(ray.origin())) {
            return null;
        }

        Vector p0_q0 = _p0.subtract(ray.origin());
        double nP0MinusP0 = alignZero(_normal.dotProduct(p0_q0));

        double t = alignZero(nP0MinusP0 / nv);

        return t <= 0 ? null : List.of(ray.getPoint(t));
    }
}
