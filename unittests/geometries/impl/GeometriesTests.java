package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class GeometriesTests contains unit tests for the Geometries class.
 */
public class GeometriesTests {
    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // יצירת אוסף גופים לבדיקות
        Geometries geometries = new Geometries(
                new Sphere(new Point(1, 0, 0), 1d),
                new Plane(new Point(0, 0, -1), new Vector(0, 0, 1)),
                new Triangle(new Point(1, 1, 1), new Point(2, 1, 1), new Point(1, 2, 1))
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: חלק מהגופים נחתכים (EP)
        // הקרן חותכת את המישור ואת הכדור, אך לא את המשולש
        var result1 = geometries.findIntersections(new Ray(new Point(0.5, 0, -2), new Vector(0, 0, 1)));
        assertNotNull(result1, "TC01: Should have intersections");
        assertEquals(3, result1.size(), "TC01: Should intersect Sphere (2) and Plane (1)");


        // ============ Boundary Values Tests ==============

        // BV01: אוסף ריק
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(1, 2, 3), new Vector(0, 1, 0))),
                "BV01: Empty collection should return null");

        // BV02: אף גוף לא נחתך
        // הקרן פונה לכיוון ההפוך מכל הגופים
        assertNull(geometries.findIntersections(new Ray(new Point(5, 5, 5), new Vector(0, 0, 1))),
                "BV02: No body is intersected");

        // BV03: רק גוף אחד נחתך
        // חיתוך רק עם המישור (בנקודה רחוקה משאר הגופים)
        var result3 = geometries.findIntersections(new Ray(new Point(-5, -5, -2), new Vector(0, 0, 1)));
        assertNotNull(result3, "BV03: Should have intersections");
        assertEquals(1, result3.size(), "BV03: Only the plane should be intersected");

        // BV04: כל הגופים נחתכים
        // נגדיר גופים שכולם נמצאים על ציר ה-Z מעל הקרן
        Geometries allGeometries = new Geometries(
                new Sphere(new Point(0, 0, 2), 1d), // z בין 1 ל-3
                new Plane(new Point(0, 0, 4), new Vector(0, 0, 1)), // z = 4
                new Triangle(new Point(-1, -1, 5), new Point(1, -1, 5), new Point(0, 1, 5)) // z = 5
        );

        // קרן שיוצאת מהראשית ועולה למעלה על ציר ה-Z
        var result4 = allGeometries.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        assertNotNull(result4, "BV04: Should have intersections");
        // כדור (2) + מישור (1) + משולש (1) = 4
        assertEquals(4, result4.size(), "BV04: All geometries should be intersected");
    }
}
