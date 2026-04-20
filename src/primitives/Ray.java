package primitives;

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

}