package primitives;

/**
 * Basic class representing a point with 3 coordinates in the 3D space.
 * [cite: 70, 71]
 */
public class Point {
    /** The coordinates of the point */
    protected final Double3 _xyz;

    /** Constant for the origin point (0,0,0) */
    public static final Point ZERO = new Point(0d, 0d, 0d);

    /**
     * Primary constructor to initialize point with three double values.
     * @param x coordinate on the X axis
     * @param y coordinate on the Y axis
     * @param z coordinate on the Z axis
     * [cite: 75, 76]
     */
    public Point(double x, double y, double z) {
        this._xyz = new Double3(x, y, z);
    }

    /**
     * Constructor to initialize point with a Double3 object.
     * @param xyz the Double3 coordinates
     * [cite: 76]
     */
    public Point(Double3 xyz) {
        this._xyz = xyz;
    }

    /**
     * Creates a vector by subtracting another point from this point.
     * @param other the point to subtract
     * @return a new Vector from the other point to this point
     * [cite: 78]
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Adds a vector to this point to create a new point.
     * @param vector the vector to add
     * @return a new Point shifted by the vector
     * [cite: 78]
     */
    public Point add(Vector vector){
        return new Point(this._xyz.add(vector._xyz));
    }

    /**
     * Calculates the squared distance between two points.
     * @param other the other point
     * @return distance squared
     * [cite: 78]
     */
    public double distanceSquared(Point other) {
        Double3 diff = this._xyz.subtract(other._xyz);
        Double3 squared = diff.product(diff);
        return squared._d1() + squared._d2() + squared._d3();
    }

    /**
     * Calculates the distance between two points.
     * @param other the other point
     * @return actual distance (using square root)
     * [cite: 79]
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof Point other)
                && _xyz.equals(other._xyz);
    }

    @Override
    public String toString() {
        return _xyz.toString();
    }
}