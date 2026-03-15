package primitives;

/**
 * Class representing a vector in 3D space.
 * Inherits from Point and must not be a zero vector.
 */
public class Vector extends Point {

    /**
     * Constructor to initialize vector with three double values.
     * @param x coordinate on X axis
     * @param y coordinate on Y axis
     * @param z coordinate on Z axis
     * @throws IllegalArgumentException if it's a zero vector
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
    }

    /**
     * Constructor to initialize vector with a Double3 object.
     * @param xyz the Double3 coordinates
     * @throws IllegalArgumentException if it's a zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /** Unit vector on the X axis */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /** Unit vector on the Y axis */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /** Unit vector on the Z axis */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Calculates dot product between two vectors.
     * @param other the other vector
     * @return scalar result
     */
    public double dotProduct(Vector other) {
        // שים לב לסוגריים שהתווספו כאן
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    /**
     * Calculates cross product between two vectors.
     * @param other the other vector
     * @return a new vector perpendicular to both
     */
    public Vector crossProduct(Vector other) {
        // שים לב לסוגריים שהתווספו כאן
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    /**
     * Adds two vectors.
     * @param other the other vector
     * @return a new vector sum
     */
    public Vector add(Vector other) {
        return new Vector(_xyz.add(other._xyz));
    }

    /**
     * Scales the vector by a scalar factor.
     * @param scalar the scaling factor
     * @return a new scaled vector
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Calculates the squared length of the vector.
     * @return squared length of the vector
     */
    public double lengthSquared() {
        return this.dotProduct(this);
    }

    /**
     * Calculates the actual length of the vector.
     * @return actual length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector to a unit vector.
     * @return a new normalized vector
     */
    public Vector normalize() {
        return scale(1 / length());
    }

    @Override
    public String toString() {
        return "Vector: " + _xyz.toString();
    }
}