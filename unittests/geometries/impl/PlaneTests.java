package geometries;
import primitives.Vector;
import primitives.Point;

import org.junit.jupiter.api.Test;
import primitives.*;
import geometries.impl.Plane;
import static org.junit.jupiter.api.Assertions.*;

class PlaneTests {

    /** Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)} */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests =============
        // TC01: Simple test
        Plane pl = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        assertEquals(new Vector(0, 0, 1), pl.getNormal(new Point(10, 5, 1)), "Bad normal for plane");
    }
}