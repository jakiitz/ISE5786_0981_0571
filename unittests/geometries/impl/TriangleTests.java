package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

class TriangleTests {
    @Test
    void testTriangleNormal() {
        Triangle triangle = new Triangle(
            new Point(0,0,0), new Point(1,0,0), new Point(0,1,0));
        assertDoesNotThrow(() -> triangle.getNormal(new Point(0,0,0)), "getNormal should not throw");
        // In Stage 1, getNormal returns null, so we check for null
        assertNull(triangle.getNormal(new Point(0,0,0)), "getNormal should return null in Stage 1");
    }
}
