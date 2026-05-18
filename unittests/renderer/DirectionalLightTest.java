package renderer;

import lighting.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link DirectionalLight}.
 * <p>
 * Tests follow the methodology of Equivalence Partitions.
 * </p>
 */
class DirectionalLightTest {
    /** Default constructor to satisfy documentation tools. */
    DirectionalLightTest() { /* Default constructor to satisfy documentation tools */ }

    /** Test light intensity. */
    private static final Color INTENSITY = new Color(100, 150, 200);

    /** Test direction before normalization. */
    private static final Vector DIRECTION = new Vector(1, 2, 2);

    /** A representative point in space. */
    private static final Point POINT = new Point(3, 4, 5);

    /**
     * Test method for {@link DirectionalLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: getL returns the normalized direction, independent of the point
        assertEquals(DIRECTION.normalize(), light.getL(POINT),
                "DirectionalLight getL() should return the normalized light direction");
    }

    /**
     * Test method for {@link DirectionalLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Directional light intensity is constant everywhere
        assertEquals(INTENSITY, light.getIntensity(POINT),
                "DirectionalLight getIntensity() should return constant intensity");
    }
}