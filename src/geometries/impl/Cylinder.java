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
        // ציר הגליל
        Vector axisDir = _axis.direction();
        primitives.Point axisOrigin = _axis.origin();
        // וקטור מהבסיס התחתון לנקודה
        Vector p0ToP = point.subtract(axisOrigin);
        // פרויקט הנקודה על הציר (t)
        double t = axisDir.dotProduct(p0ToP);
        // אם הנקודה על הבסיס התחתון
        if (Math.abs(t) < 1e-10) {
            return axisDir.normalize();
        }
        // אם הנקודה על הבסיס העליון
        if (Math.abs(t - _height) < 1e-10) {
            return axisDir.normalize();
        }
        // אחרת, הנקודה על מעטפת הצד
        primitives.Point o = axisOrigin.add(axisDir.scale(t));
        return point.subtract(o).normalize();
    }
}