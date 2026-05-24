package primitives;

/**
 * Class Material represents the material properties of a geometry.
 * This is a PDS (Public Data Structure) containing material properties like ambient attenuation.
 */
public class Material {
    public Double3 kA = Double3.ONE; // default ambient attenuation coefficient is 1 (full ambient light)
    public Double3 kD = Double3.ZERO; // default diffuse attenuation coefficient is 0 (no diffuse reflection)
    public Double3 kS = Double3.ZERO; // default specular attenuation coefficient is 0 (no specular reflection)
    public Double3 kT = Double3.ZERO;
    public Double3 kR = Double3.ZERO;
    public int nShininess = 0;

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

    /**
     * Sets the diffuse attenuation coefficient.
     * @param kD the diffuse attenuation coefficient as Double3
     * @return this for chaining
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse attenuation coefficient using a single double value.
     * @param kD the diffuse attenuation coefficient as a single double
     * @return this for chaining
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Alias for {@link #setKD(Double3)}.
     * @param kD the diffuse attenuation coefficient as Double3
     * @return this for chaining
     */
    public Material setKd(Double3 kD) {
        return setKD(kD);
    }

    /**
     * Alias for {@link #setKD(double)}.
     * @param kD the diffuse attenuation coefficient as a single double
     * @return this for chaining
     */
    public Material setKd(double kD) {
        return setKD(kD);
    }

    /**
     * Sets the specular attenuation coefficient.
     * @param kS the specular attenuation coefficient as Double3
     * @return this for chaining
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular attenuation coefficient using a single double value.
     * @param kS the specular attenuation coefficient as a single double
     * @return this for chaining
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Alias for {@link #setKS(Double3)}.
     * @param kS the specular attenuation coefficient as Double3
     * @return this for chaining
     */
    public Material setKs(Double3 kS) {
        return setKS(kS);
    }

    /**
     * Alias for {@link #setKS(double)}.
     * @param kS the specular attenuation coefficient as a single double
     * @return this for chaining
     */
    public Material setKs(double kS) {
        return setKS(kS);
    }

    /**
     * Sets the shininess factor.
     * @param nShininess shininess factor
     * @return this for chaining
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
    /**
     * Sets the transparency coefficient (kT)
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the reflection coefficient (kR)
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }
}