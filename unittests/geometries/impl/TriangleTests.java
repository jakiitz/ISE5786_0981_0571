package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Test the class geometries.impl.Triangle
 */
class TriangleTests {
    @Test
    void testTriangleNormal() {
        //=========== Equivalence Partitions Tests ==============
        // TC01: Test normal calculation for a point on the triangle
        Triangle triangle = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        Vector expectedNormal = new Vector(0, 0, 1);
        Vector actualNormal = triangle.getNormal(new Point(0.25, 0.25, 0));
        assertEquals(expectedNormal, actualNormal, "Triangle getNormal() gives wrong result");
        assertEquals(1, actualNormal.length(), 0.00000001, "Triangle normal is not a unit vector");
    }

    /**
     * Test Method findIntersections().
     */
    @Test
    public void testFindIntersections() {
        Triangle triangle = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray intersects the triangle
        assertEquals(1, triangle.findIntersections(new Ray(new Point(0.25, 0.25, -1), new Vector(0, 0, 1))).size(),
                "Ray should intersect the triangle");

        // TC02: Ray misses the triangle (outside edge)
        assertNull(triangle.findIntersections(new Ray(new Point(-1, -1, -1), new Vector(0, 0, 1))),
                "Ray should miss the triangle");

        // TC03: Ray misses the triangle (outside vertex)
        assertNull(triangle.findIntersections(new Ray(new Point(2, 2, -1), new Vector(0, 0, 1))),
                "Ray should miss the triangle");

        // =============== Boundary Values Tests ==================

        // TC11: Ray on edge (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1))),
                "Ray on edge should not count as intersection");

        // TC12: Ray on vertex (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))),
                "Ray on vertex should not count as intersection");

        // TC13: Ray on edge's continuation (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(2, 0, -1), new Vector(0, 0, 1))),
                "Ray on edge continuation should not count as intersection");
    }
}
