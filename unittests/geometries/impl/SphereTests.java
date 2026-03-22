package geometries.impl;


import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
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
}