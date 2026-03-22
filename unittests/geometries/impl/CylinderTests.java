package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

class CylinderTests {
    @Test
    void testGetNormal() {
        // Arrange: Cylinder along Z axis, radius 1, height 2
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1.0, axis, 2.0);
        // Act: Point on the side surface at (1,0,1)
        Point p = new Point(1, 0, 1);
        Vector normal = cylinder.getNormal(p);
        // Assert: Normal should be (1,0,0)
        assertEquals(new Vector(1, 0, 0), normal, "Cylinder getNormal() gives wrong result");
        assertEquals(1, normal.length(), 1e-6, "Cylinder normal is not a unit vector");
    }
}
