package lighting;

import primitives.Color;

/**
 * Base class for all light types.
 */
abstract class Light {
    /** Original light intensity */
    protected final Color _intensity;

    /**
     * Constructor for Light.
     * @param intensity the original light intensity
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Gets the original light intensity.
     * @return the original light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}
