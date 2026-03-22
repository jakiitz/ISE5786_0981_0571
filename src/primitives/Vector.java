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

    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    public double lengthSquared() {
        return dotProduct(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector normalize() {
        return scale(1 / length());
    }

    @Override
    public String toString() {
        return "Vector: " + super.toString();
    }
}