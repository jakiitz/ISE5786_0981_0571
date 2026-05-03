package lighting;

import primitives.Color;

/**
 * Class AmbientLight represents the ambient light in a scene, which is a constant light that illuminates all objects equally.
 */
public final class AmbientLight {
    /** intensity of the ambient light */
    private final Color _intensity;

    /** A constant representing no ambient light (black color) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

        /**
        * Constructor for AmbientLight
        * @param intensity the intensity of the ambient light
        */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Gets the intensity of the ambient light.
     * @return the intensity color of the ambient light
     */
    public Color getIntensity() {
        return _intensity;
    }
}