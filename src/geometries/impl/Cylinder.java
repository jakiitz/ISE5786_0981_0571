package geometries.impl;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;
import geometries.api.Intersectable;
import java.util.List;
import java.util.ArrayList;

import static primitives.Util.alignZero;

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

    @Override
    protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersectable.Intersection> intersections = new ArrayList<>();

        Point P0 = _axis.origin();
        Vector V = _axis.direction();
        Point O = ray.origin();
        Vector D = ray.direction();

        // Lateral surface intersections
        Vector U = O.subtract(P0);
        double a = D.lengthSquared() - D.dotProduct(V) * D.dotProduct(V);
        double b = 2 * (D.dotProduct(U) - D.dotProduct(V) * U.dotProduct(V));
        double c = U.lengthSquared() - U.dotProduct(V) * U.dotProduct(V) - _radius * _radius;

        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant < 0) {
            // No intersections with lateral surface
        } else {
            double sqrtDisc = Math.sqrt(discriminant);
            double t1 = alignZero((-b - sqrtDisc) / (2 * a));
            double t2 = alignZero((-b + sqrtDisc) / (2 * a));

            if (t1 > 0) {
                Point p1 = ray.getPoint(t1);
                double param1 = alignZero((p1.subtract(P0)).dotProduct(V));
                if (param1 >= 0 && param1 <= _height) {
                    intersections.add(new Intersectable.Intersection(this, p1));
                }
            }
            if (t2 > 0 && alignZero(t1 - t2) != 0) {
                Point p2 = ray.getPoint(t2);
                double param2 = alignZero((p2.subtract(P0)).dotProduct(V));
                if (param2 >= 0 && param2 <= _height) {
                    intersections.add(new Intersectable.Intersection(this, p2));
                }
            }
        }

        // Base intersections
        // Bottom base: plane at P0 with normal V
        double denom = alignZero(D.dotProduct(V));
        if (alignZero(denom) != 0) {
            double tBottom = alignZero((P0.subtract(O)).dotProduct(V) / denom);
            if (tBottom > 0) {
                Point pBottom = ray.getPoint(tBottom);
                if (alignZero(pBottom.distanceSquared(P0) - _radius * _radius) <= 0) {
                    intersections.add(new Intersectable.Intersection(this, pBottom));
                }
            }
        }

        // Top base: plane at P_top with normal V
        Point P_top = P0.add(V.scale(_height));
        if (alignZero(denom) != 0) {
            double tTop = alignZero((P_top.subtract(O)).dotProduct(V) / denom);
            if (tTop > 0) {
                Point pTop = ray.getPoint(tTop);
                if (alignZero(pTop.distanceSquared(P_top) - _radius * _radius) <= 0) {
                    intersections.add(new Intersectable.Intersection(this, pTop));
                }
            }
        }

        return intersections.isEmpty() ? null : intersections;
    }
}