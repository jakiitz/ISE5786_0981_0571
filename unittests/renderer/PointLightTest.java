package renderer;

import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link PointLight}.
 * <p>
 * Tests follow the methodology of Equivalence Partitions and Boundary Values.
 * </p>
 */
class PointLightTest {
    /** Default constructor to satisfy documentation tools. */
    PointLightTest() { /* Default constructor to satisfy documentation tools */ }

    /** Test light intensity. */
    private static final Color INTENSITY = new Color(100, 150, 200);

    /** Position of the point light. */
    private static final Point POSITION = new Point(1, 2, 3);

    /**
     * Test method for {@link PointLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        PointLight light = new PointLight(INTENSITY, POSITION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is different from light position
        Point point = new Point(2, 4, 6);
        Vector expected = point.subtract(POSITION).normalize();
        assertEquals(expected, light.getL(point),
                "PointLight getL() should return normalized vector from light position to point");

        // =============== Boundary Values Tests ==================

        // BV01: Point coincides with light position - zero vector cannot be created
        assertThrows(IllegalArgumentException.class, () -> light.getL(POSITION),
                "PointLight getL() should fail when point coincides with light position");
    }

    /**
     * Test method for {@link PointLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        PointLight light = new PointLight(INTENSITY, POSITION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is different from the light position
        assertEquals(INTENSITY, light.getIntensity(new Point(2, 4, 6)),
                "PointLight getIntensity() is incorrect for a regular point");

        // =============== Boundary Values Tests ==================

        // BV01: Point coincides with light position
        assertEquals(INTENSITY, light.getIntensity(POSITION),
                "PointLight getIntensity() is incorrect when point coincides with light position");
    }
}