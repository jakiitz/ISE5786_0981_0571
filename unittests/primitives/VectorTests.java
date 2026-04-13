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
}