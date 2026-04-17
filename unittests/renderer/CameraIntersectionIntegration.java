package renderer;

import geometries.impl.*;
import geometries.api.Intersectable;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.*;

public class CameraIntersectionIntegration {

    // Cameras for testing
    private final Camera camera1 = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(3, 3)
            .setVpDistance(1)
            .setResolution(3, 3)
            .build();

    private final Camera camera2 = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(3, 3)
            .setVpDistance(1)
            .setResolution(3, 3)
            .build();

    // Add more cameras if needed

    @Test
    void testCameraRaySphereIntegration() {
        // Test cases for sphere
        Sphere sphere1 = new Sphere(new Point(0, 0, -3), 1);
        assertIntersectionsCount(camera1, sphere1, 2, "Sphere 2 points");

        Sphere sphere2 = new Sphere(new Point(0, 0, -2.5), 2.5);
        assertIntersectionsCount(camera1, sphere2, 18, "Sphere 18 points");

        Sphere sphere3 = new Sphere(new Point(0, 0, -2), 2);
        assertIntersectionsCount(camera1, sphere3, 10, "Sphere 10 points");

        Sphere sphere4 = new Sphere(new Point(0, 0, -1), 4);
        assertIntersectionsCount(camera1, sphere4, 9, "Sphere 9 points");

        Sphere sphere5 = new Sphere(new Point(0, 0, 1), 0.5);
        assertIntersectionsCount(camera1, sphere5, 0, "Sphere 0 points");
    }

    @Test
    void testCameraRayPlaneIntegration() {
        // Test cases for plane
        Plane plane1 = new Plane(new Point(0, 0, -3), new Vector(0, 0, 1));
        assertIntersectionsCount(camera1, plane1, 9, "Plane 9 points");

        Plane plane2 = new Plane(new Point(0, 0, -3), new Vector(0, -1, 2));
        assertIntersectionsCount(camera1, plane2, 9, "Plane 9 points");

        Plane plane3 = new Plane(new Point(0, 0, -3), new Vector(0, -1, 1));
        assertIntersectionsCount(camera1, plane3, 6, "Plane 6 points");
    }

    @Test
    void testCameraRayTriangleIntegration() {
        // Test cases for triangle
        Triangle triangle1 = new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertIntersectionsCount(camera1, triangle1, 1, "Triangle 1 point");

        Triangle triangle2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertIntersectionsCount(camera1, triangle2, 2, "Triangle 2 points");
    }

    private void assertIntersectionsCount(Camera camera, Intersectable geometry, int expected, String testName) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                var intersections = geometry.findIntersections(camera.constructRay(i, j));
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        assertEquals(expected, count, testName);
    }
}
