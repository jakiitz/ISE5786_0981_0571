package geometries.impl;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;
import geometries.api.Intersectable;
import java.util.List;
import java.util.ArrayList;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Cylinder represents a finite cylinder.
 */
public final class Cylinder extends Tube {
    private final double _height;

    /**
     * Constructor for Cylinder
     * @param radius Cylinder radius
     * @param axis Cylinder axis ray
     * @param height Cylinder finite height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public Vector getNormal(primitives.Point point) {
        Vector axisDir = _axis.direction();
        primitives.Point axisOrigin = _axis.origin();

        // הגנה: אם הנקודה היא בדיוק ראשית הציר, הנורמל הוא פשוט כיוון הציר (בסיס תחתון)
        if (point.equals(axisOrigin)) {
            return axisDir.normalize();
        }

        Vector p0ToP = point.subtract(axisOrigin);
        double t = axisDir.dotProduct(p0ToP);

        // אם הנקודה על הבסיס התחתון
        if (isZero(t)) {
            return axisDir.normalize();
        }
        // אם הנקודה על הבסיס העליון
        if (isZero(t - _height)) {
            return axisDir.normalize();
        }

        // הנקודה על המעטפת הצילינדרית
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

        // ─── חיתוך עם מעטפת הצילינדר ───
        double a = D.lengthSquared() - D.dotProduct(V) * D.dotProduct(V);
        double b = 0;
        double c = 0;

        // הגנה מפני וקטור אפס: אם ראשית הקרן מתלכדת עם ראשית הצילינדר
        if (O.equals(P0)) {
            c = -_radius * _radius;
        } else {
            Vector U = O.subtract(P0);
            b = 2 * (D.dotProduct(U) - D.dotProduct(V) * U.dotProduct(V));
            c = U.lengthSquared() - U.dotProduct(V) * U.dotProduct(V) - _radius * _radius;
        }

        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant >= 0) {
            double sqrtDisc = Math.sqrt(discriminant);
            double t1 = alignZero((-b - sqrtDisc) / (2 * a));
            double t2 = alignZero((-b + sqrtDisc) / (2 * a));

            if (t1 > 0) {
                Point p1 = ray.getPoint(t1);
                // הגנה מפני וקטור אפס בחיסור
                double param1 = p1.equals(P0) ? 0 : alignZero((p1.subtract(P0)).dotProduct(V));
                if (param1 > 0 && param1 < _height) { // שינוי ל-מיושר קטן/גדול ממש כדי למנוע נקודות קצה על הבסיס
                    intersections.add(new Intersectable.Intersection(this, p1));
                }
            }
            if (t2 > 0 && alignZero(t1 - t2) != 0) {
                Point p2 = ray.getPoint(t2);
                // הגנה מפני וקטור אפס בחיסור
                double param2 = p2.equals(P0) ? 0 : alignZero((p2.subtract(P0)).dotProduct(V));
                if (param2 > 0 && param2 < _height) {
                    intersections.add(new Intersectable.Intersection(this, p2));
                }
            }
        }

        // ─── חיתוך עם בסיסי הצילינדר (מישורים חסומים) ───
        double denom = alignZero(D.dotProduct(V));
        if (denom != 0) {
            // בסיס תחתון (במיקום P0)
            double tBottom;
            if (O.equals(P0)) {
                tBottom = 0;
            } else {
                tBottom = alignZero((P0.subtract(O)).dotProduct(V) / denom);
            }

            if (tBottom > 0) {
                Point pBottom = ray.getPoint(tBottom);
                if (alignZero(pBottom.distanceSquared(P0) - _radius * _radius) <= 0) {
                    intersections.add(new Intersectable.Intersection(this, pBottom));
                }
            }

            // בסיס עליון (במיקום P_top)
            Point P_top = P0.add(V.scale(_height));
            double tTop;
            if (O.equals(P_top)) {
                tTop = 0;
            } else {
                tTop = alignZero((P_top.subtract(O)).dotProduct(V) / denom);
            }

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