package geometries.impl;


import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.impl.Sphere class
 */
class SphereTests {

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Test normal calculation for a point on the sphere
        Sphere sph = new Sphere(new Point(0, 0, 0), 1.0);

        // Point (0,0,1) is on the sphere. The normal should be the vector (0,0,1)
        Vector expectedNormal = new Vector(0, 0, 1);
        Vector actualNormal = sph.getNormal(new Point(0, 0, 1));

        // Ensure the normal is correct
        assertEquals(expectedNormal, actualNormal, "Sphere getNormal() gives wrong result");

        // Ensure the normal is a unit vector (length of 1)
        assertEquals(1, actualNormal.length(), 0.00000001, "Sphere normal is not a unit vector");
    }

    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(Ray)}
     */
    @Test
    void testFindIntersections() {
        Sphere sph = new Sphere(new Point(0, 0, 0), 1.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line outside the sphere (0 points)
        Ray ray1 = new Ray(new Point(0, 0, 2), new Vector(0, 1, 0));
        assertNull(sph.findIntersections(ray1), "Ray's line out of sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        Ray ray2 = new Ray(new Point(0, 0, -2), new Vector(0, 0, 1));
        var result2 = sph.findIntersections(ray2);
        assertNotNull(result2, "Ray should intersect sphere");
        assertEquals(2, result2.size(), "Should be 2 points");
        // וודא שהסדר נכון (הקרוב לראשית הקרן ראשון)
        assertEquals(List.of(new Point(0, 0, -1), new Point(0, 0, 1)), result2, "Incorrect intersection points");

        // TC03: Ray starts inside the sphere (1 point)
        Ray ray3 = new Ray(new Point(0, 0, 0.5), new Vector(0, 0, 1));
        var result3 = sph.findIntersections(ray3);
        assertEquals(1, result3.size(), "Should be 1 point");
        assertEquals(new Point(0, 0, 1), result3.get(0), "Incorrect point");

        // TC04: Ray starts after the sphere (0 points)
        Ray ray4 = new Ray(new Point(0, 0, 2), new Vector(0, 0, 1));
        assertNull(sph.findIntersections(ray4), "Ray starts after sphere");


        // ============ Boundary Values Tests ==============

        // ---- Group: Ray's line crosses the sphere center ----

        // BV01: Ray starts at sphere and goes inside (1 point)
        Ray ray5 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        var result5 = sph.findIntersections(ray5);
        assertEquals(1, result5.size(), "Should be 1 point (exit point)");
        assertEquals(new Point(0, 0, 1), result5.get(0), "Wrong point");

        // BV02: Ray starts at sphere and goes outside (0 points)
        Ray ray6 = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        assertNull(sph.findIntersections(ray6), "Ray starts at sphere and goes out");

        // BV03: Ray starts at the center (1 point)
        Ray ray7 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        var result7 = sph.findIntersections(ray7);
        assertEquals(1, result7.size(), "Should be 1 point from center");
        assertEquals(new Point(0, 0, 1), result7.get(0), "Wrong point");


        // ---- Group: Ray's line is tangent to the sphere ----

        // BV04: Ray starts before tangent point (0 points - as per instruction: no tangent points)
        Ray ray8 = new Ray(new Point(1, -1, 0), new Vector(0, 1, 0));
        assertNull(sph.findIntersections(ray8), "Tangent points should not be included");

        // BV05: Ray starts at tangent point (0 points)
        Ray ray9 = new Ray(new Point(1, 0, 0), new Vector(0, 1, 0));
        assertNull(sph.findIntersections(ray9), "Tangent point at origin should be excluded");

        // BV06: Ray starts after tangent point (0 points)
        Ray ray10 = new Ray(new Point(1, 1, 0), new Vector(0, 1, 0));
        assertNull(sph.findIntersections(ray10), "Ray after tangent point");


        // ---- Group: Special cases (Orthogonal) ----

        // BV07: Ray is orthogonal to [P0,O] and starts outside (0 points)
        Ray ray11 = new Ray(new Point(0, 2, 0), new Vector(1, 0, 0));
        assertNull(sph.findIntersections(ray11), "Orthogonal outside");

        // BV08: Ray is orthogonal to [P0,O] and starts at surface (0 points - tangent)
        Ray ray12 = new Ray(new Point(0, 1, 0), new Vector(1, 0, 0));
        assertNull(sph.findIntersections(ray12), "Orthogonal at surface is tangent");
        // BV09: Ray starts inside the sphere (not at center) and points towards the center (1 point)
        Ray ray13 = new Ray(new Point(0, 0, 0.5), new Vector(0, 0, 1));
        var res13 = sph.findIntersections(ray13);
        assertEquals(1, res13.size(), "BV09: Should have 1 point (exit)");

        // BV10: Ray starts outside the sphere and its line passes through center but points away (0 points)
        Ray ray14 = new Ray(new Point(0, 0, 2), new Vector(0, 0, 1));
        assertNull(sph.findIntersections(ray14), "BV10: Starts outside and points away from center");

        // BV11: Ray starts outside and its line is tangent but points away (0 points)
        Ray ray15 = new Ray(new Point(1, 1, 0), new Vector(0, 1, 0));
        assertNull(sph.findIntersections(ray15), "BV11: Tangent line, points away from tangent point");

        // BV12: Ray starts at the sphere and its line crosses twice, but it points away (0 points)
        // (Starting at P0 on surface, but vector points "out" and not through the sphere)
        Ray ray16 = new Ray(new Point(0, 0, 1), new Vector(0, 1, 1)); // וקטור אלכסוני החוצה
        assertNull(sph.findIntersections(ray16), "BV12: Starts on surface but points outside the sphere's volume");

        // BV13: Ray starts at the sphere and its line crosses twice, pointing inside (1 point)
        // (Similar to BV01 but with a general vector that doesn't pass through the center)
        Ray ray17 = new Ray(new Point(1, 0, 0), new Vector(-1, 1, 0));
        var res17 = sph.findIntersections(ray17);
        assertEquals(1, res17.size(), "BV13: Starts on surface, points inside, should find the other exit point");
    }
}