package primitives;

public class Vector extends Point {

    /**
     * Unit vector on the X axis
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /**
     * Unit vector on the Y axis
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /**
     * Unit vector on the Z axis
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    public Vector crossProduct(Vector other) {
        return new Vector(
                _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2(),
                _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3(),
                _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1()
        );
    }

    public Vector add(Vector other) {
        Vector result = new Vector(_xyz.add(other._xyz));
        // אם התוצאה היא וקטור אפס, הקונסטרקטור כבר יזרוק IllegalArgumentException
        return result;
    }

    /**
     * Multiplies the vector by a scalar value, returning a new scaled vector.
     * @param scalar
     * @return a new Vector that is this vector scaled by the given scalar
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Returns the squared length of the vector, which is more efficient to compute than the actual length.
     * @return the squared length of the vector
     */

    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Returns the length (magnitude) of the vector, which is the square root of the squared length.
     * @return
     */

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns a new vector in the same direction as this vector but with a length of 1 (unit vector).
     * @return
     */

    public Vector normalize() {
        return scale(1 / length());
    }

    /**
     * Returns the X coordinate of the vector.
     * @return the X coordinate
     */
    public double getX() {
        return _xyz._d1();
    }

    /**
     * Returns the Y coordinate of the vector.
     * @return the Y coordinate
     */
    public double getY() {
        return _xyz._d2();
    }

    /**
     * Returns the Z coordinate of the vector.
     * @return the Z coordinate
     */
    public double getZ() {
        return _xyz._d3();
    }


    @Override
    public String toString() {
        return "Vector: " + super.toString();
    }
}