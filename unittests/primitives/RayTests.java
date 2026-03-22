package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Ray}.
 * The tests verify:
 * <ul>
 * <li>Ray constructor validity</li>
 * <li>Ray properties: origin, direction</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class RayTests {
    /** Default constructor to satisfy JavaDoc generator */
    RayTests() { /* to satisfy JavaDoc generator */ }

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 0.000001;

    /** Test point (1,2,3) */
    private static final Point P1 = new Point(1, 2, 3);
    /** Test vector (1,2,3) */
    private static final Vector V1 = new Vector(1, 2, 3);

    /**
     * Test Ray constructor.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal ray
        assertDoesNotThrow(() -> new Ray(P1, V1), "Failed constructing a normal ray");
    }

    /**
     * Test get origin.
     */
    @Test
    void testOrigin() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal origin
        Ray ray = new Ray(P1, V1);
        assertEquals(P1, ray.origin(), "Origin getter failed");
    }

    /**
     * Test get direction.
     */
    @Test
    void testDirection() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal direction
        Ray ray = new Ray(P1, V1);
        Vector expected = V1.normalize();
        assertEquals(expected, ray.direction(), "Direction getter failed");
        assertEquals(1, ray.direction().length(), DELTA, "Direction should be normalized");
    }
}
