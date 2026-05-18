package renderer;

import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link SpotLight}.
 * <p>
 * Tests follow the methodology of Equivalence Partitions and Boundary Values.
 * </p>
 */
class SpotLightTest {
    /** Default constructor to satisfy documentation tools. */
    SpotLightTest() { /* Default constructor to satisfy documentation tools */ }

    /** Test light intensity. */
    private static final Color INTENSITY = new Color(100, 150, 200);

    /** Position of the spotlight. */
    private static final Point POSITION = Point.ZERO;

    /** Spotlight direction. */
    private static final Vector DIRECTION = new Vector(0, 0, 1);

    /**
     * Test method for {@link SpotLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is different from light position
        Point point = new Point(0, 0, 5);
        assertEquals(new Vector(0, 0, 1), light.getL(point),
                "SpotLight getL() should return normalized vector from light position to point");

        // =============== Boundary Values Tests ==================

        // BV01: Point coincides with light position - zero vector cannot be created
        assertThrows(IllegalArgumentException.class, () -> light.getL(POSITION),
                "SpotLight getL() should fail when point coincides with light position");
    }

    /**
     * Test method for {@link SpotLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is in front of a spotlight direction
        assertEquals(INTENSITY, light.getIntensity(new Point(0, 0, 5)),
                "SpotLight getIntensity() is incorrect for point in front of spotlight");

        // EP02: Point is behind the spotlight direction
        assertEquals(Color.BLACK, light.getIntensity(new Point(0, 0, -5)),
                "SpotLight getIntensity() should be black for point behind spotlight");

        // =============== Boundary Values Tests ==================

        // BV01: Point coincides with light position - getL cannot create zero vector
        assertThrows(IllegalArgumentException.class, () -> light.getIntensity(POSITION),
                "SpotLight getIntensity() should fail when point coincides with light position");

        // BV02: Point is at 90 degrees to a spotlight direction
        assertEquals(Color.BLACK, light.getIntensity(new Point(5, 0, 0)),
                "SpotLight getIntensity() should be black for point at 90 degrees to spotlight direction");
    }
}