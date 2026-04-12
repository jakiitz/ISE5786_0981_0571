package geometries.impl;

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
     * find the intersection points of the sphere with a ray
     *
     * @param ray the ray to intersect with the sphere
     * @return list of intersection points (null if no intersections)
     */
//    @Override
//    public List<Point> findIntersections(Ray ray)
//    {
//        // find the point of intersection of the ray with the sphere
//        // using the geometric solution of the quadratic equation
//        Vector u = _center.subtract(ray.origin());
//        double tm = ray.direction().dotProduct(u);
//        double d2 = u.lengthSquared() - tm * tm;
//        double radius2 = _radius * _radius;
//        if (d2 > radius2) {
//            return null; // no intersections
//        }
//        double th = Math.sqrt(radius2 - d2);
//        double t1 = tm - th;
//        double t2 = tm + th;
//        if (t1 <= 0 && t2 <= 0) {
//            return null; // both intersections are behind the ray
//        }
//        List<Point> intersections = new java.util.ArrayList<>();
//        if (t1 > 0) {
//            intersections.add(ray.origin().add(ray.direction().scale(t1)));
//        }
//        if (t2 > 0) {
//            intersections.add(ray.origin().add(ray.direction().scale(t2)));
//        }
//        return intersections;
//    }
//    @Override
//    public List<Point> findIntersections(Ray ray) {
//        Point p0 = ray.origin();
//        Vector v = ray.direction();
//
//        // טיפול במקרה שראשית הקרן היא מרכז הכדור כדי למנוע Vector(0,0,0)
//        if (p0.equals(_center)) {
//            return List.of(ray.origin().add(ray.direction().scale(_radius)));
//        }
//
//        Vector u = _center.subtract(p0);
//        double tm = alignZero(v.dotProduct(u));
//        double d2 = alignZero(u.lengthSquared() - tm * tm);
//        double radius2 = _radius * _radius;
//
//        // אם המרחק d גדול מהרדיוס - אין חיתוך
//        // אם d שווה לרדיוס - זו השקה, ולפי ההוראות אין לכלול נקודות השקה
//        if (alignZero(d2 - radius2) >= 0) {
//            return null;
//        }
//
//        double th = Math.sqrt(radius2 - d2);
//        double t1 = alignZero(tm - th);
//        double t2 = alignZero(tm + th);
//
//        // אנחנו צריכים רק t > 0 (חיתוך בכיוון הקרן בלבד, ללא ראשית הקרן)
//        boolean t1Positive = t1 > 0;
//        boolean t2Positive = t2 > 0;
//
//        // שימוש באופרטור טרנרי לייעול היצירה של הרשימה (KISS)
//        if (t1Positive && t2Positive) {
//            // שתי נקודות חיתוך - נחזיר אותן לפי סדר המרחק מראשית הקרן (t1 תמיד קטן מ-t2)
//            return List.of(ray.origin().add(ray.direction().scale(t1)), ray.origin().add(ray.direction().scale(t2)));
//        }
//
//        if (t1Positive) {
//            return List.of(ray.origin().add(ray.direction().scale(t1)));
//        }
//
//        if (t2Positive) {
//            return List.of(ray.origin().add(ray.direction().scale(t2)));
//        }
//
//        return null;
//    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // טיפול במקרה שראשית הקרן היא מרכז הכדור (למניעת וקטור האפס)
        if (p0.equals(_center)) {
            return List.of(ray.getPoint(_radius));
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
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }

        // שימוש בטרנרי למקרה של נקודה אחת או null (לפי הדוגמה במסמך)
        return t1Positive ? List.of(ray.getPoint(t1)) :
                t2Positive ? List.of(ray.getPoint(t2)) : null;
    }
}