package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class Triangle represents a triangle in 3D space.
 */
public final class Triangle extends Polygon {
    /**
     * Constructor for Triangle
     * @param p1 vertex 1
     * @param p2 vertex 2
     * @param p3 vertex 3
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }
    public Vector getNormal(Point point) {
        return null;
     }

}