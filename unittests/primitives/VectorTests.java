package primitives;

import org.junit.jupiter.api.Test;
import primitives.Point;
import static org.junit.jupiter.api.Assertions.*;
import primitives.Vector;

class VectorTests {
    @Test
    void testAddZero() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2), "add() for reverse vectors does not throw exception");
    }

    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        assertEquals(0, v1.dotProduct(v2), 0.00001, "dotProduct() wrong value");
    }

    @Test
    void testCrossProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);
        assertEquals(0, vr.dotProduct(v1), 0.00001, "crossProduct() result is not orthogonal to its operands");
        assertEquals(0, vr.dotProduct(v2), 0.00001, "crossProduct() result is not orthogonal to its operands");
    }

    @Test
    void testLength() {
        assertEquals(1, new Vector(1, 0, 0).length(), 0.00001, "length() wrong value");
    }

    @Test
    void testNormalize() {
        Vector v = new Vector(1, 2, 3);
        Vector u = v.normalize();
        assertEquals(1, u.length(), 0.00001, "normalize() result is not a unit vector");
    }

    @Test
    void testScale() {
        Vector v = new Vector(1, 2, 3);
        Vector u = v.scale(2);
        assertEquals(new Vector(2, 4, 6), u, "scale() wrong result");
    }

    @Test
    void testGetX() {
        Vector v = new Vector(1, 2, 3);
        assertEquals(1, v.getX(), "getX() wrong value");
    }

    @Test
    void testGetY() {
        Vector v = new Vector(1, 2, 3);
        assertEquals(2, v.getY(), "getY() wrong value");
    }

    @Test
    void testGetZ() {
        Vector v = new Vector(1, 2, 3);
        assertEquals(3, v.getZ(), "getZ() wrong value");
    }
}