package primitives;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a ray in a 3D Cartesian coordinate system.
 * A ray is defined by a starting point and a direction vector.
 */
public class Ray {

    /** The starting point of the ray */
    private final Point _origin;

    /** The direction vector of the ray (always normalized) */
    private final Vector _direction;

    /**
     * Constructor to initialize a ray with a starting point and a direction vector.
     * The direction vector is automatically normalized upon creation.
     * * @param origin    the starting point of the ray
     * @param direction the direction vector of the ray
     */
    public Ray(Point origin, Vector direction) {
        this._origin = origin;
        this._direction = direction.normalize();
    }

    /**
     * Gets the starting point of the ray.
     * * @return the origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Gets the direction vector of the ray.
     * * @return the normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    public Point getPoint(double t)
    {
        if (isZero(t)) {
            return _origin;
        }
        return _origin.add(_direction.scale(t));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ray)) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        int result = _origin != null ? _origin.hashCode() : 0;
        result = 31 * result + (_direction != null ? _direction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ray{" + "origin=" + _origin + ", direction=" + _direction + '}';
    }

    /**
     * Finds the point closest to the ray's origin from a list of points.
     * @param points list of intersection points
     * @return the closest point, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {
        // Note: Per instructions, we can assume the list is not null/empty for EP,
        // but the BV check handles null cases.
        if (points == null || points.isEmpty()) {
            return null;
        }

        Point closestPoint = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Point p : points) {
            // Performance optimization: use distanceSquared to avoid unnecessary sqrt calls[cite: 2]
            double distSq = origin().distanceSquared(p);
            if (distSq < minDistanceSquared) {
                minDistanceSquared = distSq;
                closestPoint = p;
            }
        }
        return closestPoint;
    }
}