package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static primitives.Util.alignZero;

/* Spotlight source */
public class SpotLight extends PointLight
{
    /** The direction of the spotlight */
    private final Vector _direction;

    /** Narrow beam factor for controlling spotlight concentration */
    private int _narrowBeam = 1;

    /**
     * Constructor for SpotLight
     * @param intensity the intensity of the spotlight
     * @param position the position of the spotlight
     * @param direction the direction of the spotlight
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        _direction = direction.normalize();
    }

    /**
     * Sets the narrow beam factor.
     * A higher value makes the spotlight beam narrower.
     * @param narrowBeam the narrow beam factor
     * @return this spotlight for chaining
     */
    public SpotLight setNarrowBeam(int narrowBeam) {
        _narrowBeam = narrowBeam;
        return this;
    }

    /**
     * Sets the constant attenuation coefficient.
     * @param kC constant attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * Sets the linear attenuation coefficient.
     * @param kL linear attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation coefficient.
     * @param kQ quadratic attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    /**
     * Alias for {@link #setKc(double)}.
     * @param kC constant attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKC(double kC) {
        return setKc(kC);
    }

    /**
     * Alias for {@link #setKl(double)}.
     * @param kL linear attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKL(double kL) {
        return setKl(kL);
    }

    /**
     * Alias for {@link #setKq(double)}.
     * @param kQ quadratic attenuation coefficient
     * @return this spotlight for chaining
     */
    @Override
    public SpotLight setKQ(double kQ) {
        return setKq(kQ);
    }

    @Override
    public Color getIntensity(Point p) {
        double projection = alignZero(_direction.dotProduct(getL(p)));

        if (projection <= 0) {
            return Color.BLACK;
        }

        return super.getIntensity(p).scale(Math.pow(projection, _narrowBeam));
    }
}