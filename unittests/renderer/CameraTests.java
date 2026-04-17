package renderer;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTests {

    @Test
    void testConstructRay() {
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(1, 1)
                .setVpDistance(1)
                .setResolution(1, 1)
                .build();

        Ray ray = camera.constructRay(0, 0);
        assertNotNull(ray);
        assertEquals(new Point(0, 0, 0), ray.origin());
        // Direction should be to the center of the view plane
        assertEquals(new Vector(0, 0, -1), ray.direction());
    }

    @Test
    void testBuilder() {
        // Test successful build
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(1, 1)
                .setVpDistance(1)
                .setResolution(1, 1)
                .build();

        assertNotNull(camera);
    }

    @Test
    void testBuilderInvalidResolution() {
        // Test invalid resolution
        assertThrows(IllegalArgumentException.class, () -> {
            Camera.getBuilder()
                    .setLocation(new Point(0, 0, 0))
                    .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                    .setVpSize(1, 1)
                    .setVpDistance(1)
                    .setResolution(0, 1)
                    .build();
        });
    }

    @Test
    void testBuilderMissingLocation() {
        // Test missing location
        assertThrows(Exception.class, () -> {
            Camera.getBuilder()
                    .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                    .setVpSize(1, 1)
                    .setVpDistance(1)
                    .setResolution(1, 1)
                    .build();
        });
    }
}
