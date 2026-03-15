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
        // In Stage 1 we can return null as per instructions [cite: 165]
        return null;
    }
}