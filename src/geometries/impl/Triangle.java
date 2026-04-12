package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Triangle represents a triangle in 3D space.
 */
public final class Triangle extends Polygon {
    /**
     * Constructor for Triangle
     *
     * @param p1 vertex 1
     * @param p2 vertex 2
     * @param p3 vertex 3
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    public Vector getNormal(Point point) {
        // הנורמל של משולש הוא אותו נורמל של המישור המכיל אותו
        return super.getNormal(point);
    }

    /**
     * find the intersection with a ray
     *
     * @param ray the ray to intersect with the triangle
     * @return list of intersection points (null if no intersections)
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        // שלב 1: מציאת חיתוך עם המישור המכיל את המשולש
        // משתמשים בנוסחה: t = (n * (p0 - head)) / (n * dir)
        Vector v = ray.direction();
        Point head = ray.origin();
        Vector n = getNormal(null); // הנורמל של המשולש

        double nv = n.dotProduct(v);

        // אם הקרן מקבילה למישור, אין חיתוך
        if (isZero(nv)) return null;

        // חישוב המרחק t לנקודת החיתוך
        Vector p0MinusHead;
        try {
            p0MinusHead = _vertices.get(0).subtract(head);
        } catch (IllegalArgumentException e) {
            // אם ראש הקרן הוא בדיוק קודקוד המשולש, אין חיתוך לפי ההנחיות
            return null;
        }

        double nP0MinusHead = n.dotProduct(p0MinusHead);
        double t = alignZero(nP0MinusHead / nv);

        // חיתוך קיים רק בכיוון הקרן (t > 0)
        if (t <= 0) return null;

        // שלב 2: בדיקה האם נקודת החיתוך נמצאת בתוך המשולש
        // מחשבים וקטורים מהראש לכל קודקוד ובודקים את הסימן של המכפלות הסקלריות
        Vector v1 = _vertices.get(0).subtract(head);
        Vector v2 = _vertices.get(1).subtract(head);
        Vector v3 = _vertices.get(2).subtract(head);

        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // אם אחד הסקלרים הוא אפס, הנקודה על צלע או קודקוד - נחזיר null
        if (isZero(s1) || isZero(s2) || isZero(s3)) return null;

        // הנקודה בפנים אם לכולם אותו סימן
        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            // יצירת הרשימה רק כשבטוחים שיש חיתוך (ביצועים)
            return List.of(ray.getPoint(t));
        }

        return null;
    }
}