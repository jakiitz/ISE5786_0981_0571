package geometries.api;
import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * Interface Intersectable represents geometric objects that can be intersected by rays.
 * It defines a method to find the intersection points of a ray with the geometry.
 */
public abstract class Intersectable {
    public abstract List<Point> findIntersections(Ray ray);
}