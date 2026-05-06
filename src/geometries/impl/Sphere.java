package geometries.impl;

import geometries.api.Intersectable;
import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;


/**
 * Class Sphere represents a 3D ball.
 */
public final class Sphere extends RadialGeometry {
    private final Point _center;

    /**
     * constructor of sphere
     *
     * @param center
     * @param radius
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        // הנורמל של כדור הוא הווקטור מהמרכז לנקודה, מנורמל
        return point.subtract(_center).normalize();
    }

    /**
     * find the intersection points of the sphere with a ray (NVI helper)
     *
     * @param ray the ray to intersect with the sphere
     * @return list of intersection objects (null if no intersections)
     */
    @Override
    protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray)
    {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // טיפול במקרה שראשית הקרן היא מרכז הכדור (למניעת וקטור האפס)
        if (p0.equals(_center)) {
            return List.of(new Intersectable.Intersection(this, ray.getPoint(_radius)));
        }

        Vector u = _center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double radius2 = _radius * _radius;

        // בדיקת חיתוך/השקה - אם d2 >= radius2 אין חיתוך (כולל השקה)
        if (alignZero(d2 - radius2) >= 0) {
            return null;
        }

        double th = Math.sqrt(radius2 - d2);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // בדיקה אילו נקודות נמצאות בכיוון הקרן (t > 0)
        boolean t1Positive = t1 > 0;
        boolean t2Positive = t2 > 0;

        // החזרה באמצעות אופרטור טרנרי/KISS כפי שנדרש
        if (t1Positive && t2Positive) {
            return List.of(new Intersectable.Intersection(this, ray.getPoint(t1)), 
                           new Intersectable.Intersection(this, ray.getPoint(t2)));
        }

        // שימוש בטרנרי למקרה של נקודה אחת או null (לפי הדוגמה במסמך)
        return t1Positive ? List.of(new Intersectable.Intersection(this, ray.getPoint(t1))) :
                t2Positive ? List.of(new Intersectable.Intersection(this, ray.getPoint(t2))) : null;
    }
}