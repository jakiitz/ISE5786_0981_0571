package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/* Directional light source */
public class DirectionalLight extends Light implements LightSource{
    /** The direction of the light */
    private final Vector _direction;

    /**
     * Constructor for DirectionalLight
     * @param intensity the intensity of the light
     * @param direction the direction of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        _direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity;
    }
    @Override
    public double getDistance(Point p) {
        return Double.POSITIVE_INFINITY;
    }
}
