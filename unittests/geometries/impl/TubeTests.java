package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class TubeTests {
    @Test
    void testTubeNormal() {
        Tube tube = new Tube(1.0, new Ray(new Point(0,0,0), new Vector(0,0,1)));
        assertDoesNotThrow(() -> tube.getNormal(new Point(1,0,0)), "getNormal should not throw");
        // In Stage 1, getNormal returns null, so we check for null
        assertNull(tube.getNormal(new Point(1,0,0)), "getNormal should return null in Stage 1");
    }
}
