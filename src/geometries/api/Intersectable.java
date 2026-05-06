package geometries.api;
import primitives.Point;
import primitives.Ray;
import primitives.Material;
import java.util.List;
import java.util.Objects;

/**
 * Interface Intersectable represents geometric objects that can be intersected by rays.
 * It defines a method to find the intersection points of a ray with the geometry.
 */
public abstract class Intersectable {
    
    /**
     * Nested class representing an intersection between a ray and a geometry.
     */
    public static class Intersection {
        public final Geometry geometry; // Forward reference - resolved at runtime
        public final Point point;
        public final Material material;
        
        /**
         * Constructor for Intersection
         * @param geometry the geometry that was intersected
         * @param point the point of intersection
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Intersection)) return false;
            Intersection other = (Intersection) obj;
            return this.geometry == other.geometry && this.point.equals(other.point);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(geometry), point);
        }
        
        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }
    }
    
    /**
     * Protected abstract method to calculate intersections.
     * Must be implemented by subclasses.
     * @param ray the ray to intersect with
     * @return list of intersections (null if no intersections)
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
    
    /**
     * Public final method to find intersections using the NVI pattern.
     * @param ray the ray to intersect with
     * @return list of intersections (null if no intersections)
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }
    
    /**
     * Finds intersection points of the ray with the geometry.
     * Refactored to use the new Intersection-based approach.
     * @param ray the ray to intersect with
     * @return list of intersection points (null if no intersections)
     */
    public List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        if (intersections == null) {
            return null;
        }
        return intersections.stream()
                .filter(intersection -> intersection != null)
                .map(intersection -> intersection.point)
                .toList();
    }
}