package primitives;

import java.util.List;
import geometries.api.Intersectable.Intersection;

import static primitives.Util.isZero;

/**
 * Represents a ray in a 3D Cartesian coordinate system.
 * A ray is defined by a starting point (origin) and a normalized direction vector.
 * The direction vector is automatically normalized upon ray construction to ensure consistency.
 *
 * Rays are commonly used in ray tracing algorithms for intersection detection and lighting calculations.
 */
public class Ray {

    /** The starting point of the ray in 3D space */
    private final Point _origin;

    /** The direction vector of the ray (guaranteed to be normalized to unit length) */
    private final Vector _direction;

    /** Constant for the size of moving first point of ray for shading rays */
    private static final double DELTA = 0.1;

    /**
     * Constructor for ray with a slight offset from the origin point to avoid self-intersection.
     * The offset is applied along the normal vector n.
     *
     * @param head the original starting point
     * @param direction the direction vector
     * @param n the normal vector at the point
     */
    public Ray(Point head, Vector direction, Vector n) {
        // Calculate the dot product to see if we move in the direction of the normal or opposite
        double nv = n.dotProduct(direction);
        Vector deltaVector = n.scale(nv > 0 ? DELTA : -DELTA);
        this._origin = head.add(deltaVector);
        this._direction = direction.normalize();
    }
    /**
     * Constructs a ray with a specified starting point and direction vector.
     * The direction vector is automatically normalized to ensure it has unit length,
     * providing consistency for mathematical operations and distance calculations.
     *
     * @param origin    the starting point of the ray in 3D space (non-null)
     * @param direction the direction vector of the ray; will be normalized automatically (non-null)
     */
    public Ray(Point origin, Vector direction) {
        // Store the origin point as-is
        this._origin = origin;
        // Normalize the direction vector to unit length for consistent calculations
        this._direction = direction.normalize();
    }

    /**
     * Retrieves the starting point (origin) of the ray.
     *
     * @return the origin point from which the ray extends
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Retrieves the direction vector of the ray.
     *
     * @return the normalized direction vector (unit length)
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Calculates a point on the ray at a given distance parameter t from the origin.
     * The formula is: P(t) = origin + t * direction
     *
     * Special case: When t = 0, returns the origin point directly without calculation.
     *
     * @param t the distance parameter along the ray direction
     * @return the point at distance t along the ray, or the origin if t is zero
     */
    public Point getPoint(double t) {
        if (isZero(t)) return _origin;
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException ignored) {
            // t is so small that direction.scale(t) underflows to a zero vector;
            // treat the displacement as zero and return the origin.
            return _origin;
        }
    }

    /**
     * Checks if this ray is equal to another object.
     * Two rays are equal if they have the same origin point and the same direction vector.
     *
     * @param obj the object to compare with this ray
     * @return true if the object is a Ray with identical origin and direction; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        // Check if comparing with the same object instance (identity check)
        if (this == obj) return true;
        // Check if the object is an instance of Ray
        if (!(obj instanceof Ray)) return false;
        // Cast to Ray and compare both origin and direction vectors
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    /**
     * Generates a hash code for this ray based on its origin and direction.
     * Consistent with the equals method: two equal rays will have the same hash code.
     *
     * @return the hash code value for this ray
     */
    @Override
    public int hashCode() {
        // Start with hash of the origin point (handle null case)
        int result = _origin != null ? _origin.hashCode() : 0;
        // Combine with hash of direction using prime number multiplier for better distribution
        result = 31 * result + (_direction != null ? _direction.hashCode() : 0);
        return result;
    }

    /**
     * Generates a string representation of the ray.
     * Format: "Ray{origin=[Point], direction=[Vector]}"
     *
     * @return a string description of this ray with its origin and direction
     */
    @Override
    public String toString() {
        return "Ray{" + "origin=" + _origin + ", direction=" + _direction + '}';
    }

    /**
     * Finds the intersection point closest to the ray's origin from a list of intersections.
     * Uses squared distance comparison to avoid expensive square root calculations.
     *
     * @param intersections a list of intersections to search through (may contain null values)
     * @return the intersection closest to the ray's origin, or null if the list is null, empty, or contains no valid intersections
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        // Step 1: Validate input - check if list is null or empty
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        // Step 2: Initialize variable to track the closest intersection found
        Intersection closestIntersection = null;
        
        // Step 3: Initialize minimum distance to positive infinity (ensures first valid intersection will be closer)
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        // Step 4: Iterate through all intersections in the list
        for (Intersection intersection : intersections) {
            // Step 5: Skip null intersections
            if (intersection != null) {
                // Step 6: Calculate squared distance from ray origin to this intersection point
                // Using squared distance avoids expensive sqrt operation for comparison
                double distSq = origin().distanceSquared(intersection.point);
                
                // Step 7: Check if this intersection is closer than the current closest
                if (distSq < minDistanceSquared) {
                    // Step 8: Update the minimum distance found
                    minDistanceSquared = distSq;
                    
                    // Step 9: Update the closest intersection to this intersection
                    closestIntersection = intersection;
                }
            }
        }
        
        // Step 10: Return the closest intersection found (null if no valid intersections)
        return closestIntersection;
    }

    /**
     * Finds the point closest to the ray's origin from a list of points.
     * Uses squared distance comparison to avoid expensive square root calculations.
     * Per specification, for Essential Part (EP): assumes the list is not null/empty.
     * For Beyond the Visible (BV): includes null/empty checks for robustness.
     *
     * @param points a list of points to search through (may contain null values)
     * @return the point closest to the ray's origin, or null if the list is null, empty, or contains no valid points
     */
    public Point findClosestPoint(List<Point> points) {
        // Validation check: return null if the list is null or empty
        if (points == null || points.isEmpty()) {
            return null;
        }

        // Initialize variable to track the closest point found
        Point closestPoint = null;
        // Initialize minimum distance to positive infinity (ensures first valid point will be closer)
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        // Iterate through all points in the list
        for (Point p : points) {
            // Calculate squared distance from ray origin to current point
            // Performance optimization: use distanceSquared to avoid unnecessary sqrt calls
            double distSq = origin().distanceSquared(p);
            
            // Check if this point is closer than the current closest point
            if (distSq < minDistanceSquared) {
                // Update the minimum distance found
                minDistanceSquared = distSq;
                // Update the closest point to this point
                closestPoint = p;
            }
        }
        
        // Return the closest point found (null if no valid points)
        return closestPoint;
    }
}