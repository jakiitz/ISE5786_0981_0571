package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource{
    /** The position of the point light */
    protected final Point _position;
    private double _kC = 1;
    private double _kL = 0;
    private double _kQ = 0;

    /**
     * Constructor for PointLight
     * @param intensity the intensity of the point light
     * @param position the position of the point light
     */

    public PointLight(Color intensity, Point position) {
        super(intensity);
        _position = position;
    }

    /**
     * Sets the constant attenuation coefficient.
     * @param kC constant attenuation coefficient
     * @return this point light for chaining
     */
    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation coefficient.
     * @param kL linear attenuation coefficient
     * @return this point light for chaining
     */
    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation coefficient.
     * @param kQ quadratic attenuation coefficient
     * @return this points light for chaining
     */
    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    /**
     * Alias for {@link #setKc(double)}.
     * @param kC constant attenuation coefficient
     * @return this points light for chaining
     */
    public PointLight setKC(double kC) {
        return setKc(kC);
    }

    /**
     * Alias for {@link #setKl(double)}.
     * @param kL linear attenuation coefficient
     * @return this points light for chaining
     */
    public PointLight setKL(double kL) {
        return setKl(kL);
    }

    /**
     * Alias for {@link #setKq(double)}.
     * @param kQ quadratic attenuation coefficient
     * @return this points light for chaining
     */
    public PointLight setKQ(double kQ) {
        return setKq(kQ);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        double distance = p.distance(_position);
        double attenuation = _kC + _kL * distance + _kQ * distance * distance;
        return _intensity.reduce(attenuation);
    }
    @Override
    public double getDistance(Point p) {
        return p.distance(_position);
    }
}