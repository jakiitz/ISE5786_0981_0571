package primitives;

/**
 * Class Material represents the material properties of a geometry.
 * This is a PDS (Public Data Structure) containing material properties like ambient attenuation.
 */
public class Material {
    public Double3 kA = Double3.ONE;
    
    /**
     * Sets the ambient attenuation coefficient.
     * @param kA the ambient attenuation coefficient as Double3
     * @return this for chaining
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }
    
    /**
     * Sets the ambient attenuation coefficient using a single double value.
     * Creates a Double3 with the same value for all components.
     * @param kA the ambient attenuation coefficient as a single double
     * @return this for chaining
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}
