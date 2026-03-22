package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Point}.
 * The tests verify:
 * <ul>
 * <li>Point constructor validity</li>
 * <li>Point operations: subtract, add, distance</li>
 * <li>Point properties: equals, toString</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PointTests {
    /** Default constructor to satisfy JavaDoc generator */
    PointTests() { /* to satisfy JavaDoc generator */ }

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 0.000001;

    /** Test point (1,2,3) */
    private static final Point P1 = new Point(1, 2, 3);
    /** Test point (2,4,6) */
    private static final Point P2 = new Point(2, 4, 6);
    /** Test point (0,0,0) */
    private static final Point ZERO = Point.ZERO;

    /**
     * Test Point constructor with double values.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal point
        assertDoesNotThrow(() -> new Point(1, 2, 3), "Failed constructing a normal point");
    }

    /**
     * Test Point constructor with Double3.
     */
    @Test
    void testConstructorDouble3() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal point
        Double3 d = new Double3(1, 2, 3);
        assertDoesNotThrow(() -> new Point(d), "Failed constructing a point with Double3");
    }

    /**
     * Test subtract.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal subtract
        Vector result = P1.subtract(P2);
        assertEquals(new Vector(-1, -2, -3), result, "Subtract failed");
    }

    /**
     * Test add vector.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal add
        Vector v = new Vector(1, 2, 3);
        Point result = P1.add(v);
        assertEquals(new Point(2, 4, 6), result, "Add vector failed");
    }

    /**
     * Test distance squared.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal distance squared
        assertEquals(14, P1.distanceSquared(P2), DELTA, "Distance squared failed");
    }

    /**
     * Test distance.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal distance
        assertEquals(Math.sqrt(14), P1.distance(P2), DELTA, "Distance failed");
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Same point
        assertEquals(P1, new Point(1, 2, 3), "Equals failed for same point");

        // TC02: Different point
        assertNotEquals(P1, P2, "Equals failed for different points");
    }

    /**
     * Test ZERO constant.
     */
    @Test
    void testZeroConstant() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: ZERO point
        assertEquals(new Point(0, 0, 0), ZERO, "ZERO constant failed");
    }

    /**
     * Test toString.
     */
    @Test
    void testToString() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal toString
        assertEquals("(1.0,2.0,3.0)", P1.toString(), "toString failed");
    }
}
