package geometries.impl;

import primitives.*;

/**
 * Class Sphere represents a 3D ball.
 */
public final class Sphere extends RadialGeometry {
    private final Point _center;

    /**
     * constructor of sphere
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
}