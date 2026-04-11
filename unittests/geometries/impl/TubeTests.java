package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for geometries.impl.Tube class.
 */
class TubeTests {
    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 1), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests =============

        // EP01: Point is in front of the ray head (t > 0)
        Vector n1 = tube.getNormal(new Point(1, 0, 2));
        assertEquals(new Vector(1, 0, 0), n1, "Bad normal for point in front of ray head");
        assertEquals(1, n1.length(), DELTA, "Normal is not normalized");

        // EP02: Point is behind the ray head (t < 0)
        Vector n2 = tube.getNormal(new Point(1, 0, 0));
        assertEquals(new Vector(1, 0, 0), n2, "Bad normal for point behind ray head");
        assertEquals(1, n2.length(), DELTA, "Normal is not normalized");

        // =============== Boundary Values Tests ==================

        // BV01: Point is exactly opposite the ray head (t = 0)
        // In this case, (P - P0) is already orthogonal to the direction vector.
        Vector n3 = tube.getNormal(new Point(1, 0, 1));
        assertEquals(new Vector(1, 0, 0), n3, "Bad normal for point opposite ray head (BV)");
        assertEquals(1, n3.length(), DELTA, "Normal is not normalized");
    }
}
