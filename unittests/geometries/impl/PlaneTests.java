package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for geometries.impl.Plane class.
 * The tests verify constructors and getNormal method.
 */
class PlaneTests {
    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-10;
    /** Error message for wrong normal */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";
    /** Error message for constructor exception */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Constructor failed to throw/not throw correctly";

    /**
     * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Plane pl = new Plane(p0, new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple test with a point in the plane (not reference point)
        assertEquals(new Vector(0, 0, 1), pl.getNormal(new Point(10, 5, 1)), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // BV01: Test with the reference point of the plane
        assertEquals(new Vector(0, 0, 1), pl.getNormal(p0), ERROR_NORMAL);
    }

    /**
     * Test method for constructors:
     * {@link geometries.impl.Plane#Plane(Point, Vector)}
     * {@link geometries.impl.Plane#Plane(Point, Point, Point)}
     */
    @Test
    void testConstructors() {
        // --- Test Plane(Point, Vector) ---
        // ============ Equivalence Partitions Tests =============
        // EP01: Test that normal is normalized in constructor
        Plane p = new Plane(new Point(0,0,1), new Vector(0,0,5));
        assertEquals(1, p.getNormal(null).length(), DELTA, "Normal must be normalized");

        // =============== Boundary Values Tests ==================
        // BV01: Normal vector is zero
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(0, 0, 1), new Vector(0, 0, 0)), ERROR_CONSTRUCTOR);

        // --- Test Plane(Point, Point, Point) ---
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);

        // ============ Equivalence Partitions Tests =============
        // EP02: Correct construction with 3 non-collinear points
        assertDoesNotThrow(() -> new Plane(p1, p2, p3), ERROR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================
        // BV02: Two points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p3), ERROR_CONSTRUCTOR);
        // BV03: All three points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p1), ERROR_CONSTRUCTOR);
        // BV04: Three points are collinear
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(0,0,1), new Point(0,0,2), new Point(0,0,3)), ERROR_CONSTRUCTOR);
    }

    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Plane pl = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests =============
        // EP01: Ray intersects the plane (1 point)
        Ray ray1 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        var result1 = pl.findIntersections(ray1);
        assertNotNull(result1, "Ray should intersect the plane");
        assertEquals(1, result1.size(), "Should be 1 intersection point");
        assertEquals(new Point(0, 0, 1), result1.get(0), "Incorrect intersection point");
        // EP02: Ray does not intersect (0 points)
        Ray ray2 = new Ray(new Point(0, 0, 0), new Vector(0, 1, 0));
        assertNull(pl.findIntersections(ray2), "Ray should not intersect the plane");

        //=================== Boundary Values Tests ==================
        // BV01: Ray is parallel to the plane and lies in the plane (infinite points)
        Ray ray3 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        assertNull(pl.findIntersections(ray3), "Ray lies in the plane, should return null (no single intersection point)");
        // BV02: Ray is parallel to the plane and does not lie in the plane (0 points)
        Ray ray4 = new Ray(new Point(0, 0, 2), new Vector(1, 0, 0));
        assertNull(pl.findIntersections(ray4), "Ray is parallel and outside the plane, should return null");
        // BV03: Ray is orthogonal to the plane and starts before the plane (1 point)
        Ray ray5 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        var result5 = pl.findIntersections(ray5);
        assertNotNull(result5, "Ray should intersect the plane");
        assertEquals(1, result5.size(), "Should be 1 intersection point");
        assertEquals(new Point(0, 0, 1), result5.get(0), "Incorrect intersection point");
        // BV04: Ray is orthogonal to the plane and starts on the plane (1 point)
        Ray ray6 = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        var result6 = pl.findIntersections(ray6);
        assertNull(result6, "Ray starts on the plane and goes away, should return null (no single intersection point)");
         // BV05: Ray is orthogonal to the plane and starts after the plane (0 points)
        Ray ray7 = new Ray(new Point(0, 0, 2), new Vector(0, 0, 1));
        assertNull(pl.findIntersections(ray7), "Ray starts after the plane, should return null");

        // BV06:  Ray is neither orthogonal nor parallel to the plane and starts on the plane (1 point)
        Ray ray8 = new Ray(new Point(0, 0, 1), new Vector(1, 1, 1));
        var result8 = pl.findIntersections(ray8);
        assertNull(result8, "Ray starts on the plane and goes away, should return null (no single intersection point)");

        // BV07:  Ray is neither orthogonal nor parallel to the plane and begins at the reference point of the plane (0 point)
        Ray ray9 = new Ray(new Point(0, 0, 1), new Vector(1, 1, 1));
        var result9 = pl.findIntersections(ray9);
        assertNull(result9, "Ray starts at the reference point of the plane and goes away, should return null (no single intersection point)");


    }
}