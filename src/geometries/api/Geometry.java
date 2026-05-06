package geometries.api;

import primitives.Vector;
import primitives.Point;
import primitives.Color;
import primitives.Material;

/**
 * Abstract base class for geometric objects in 3D space.
 * Provides common functionality for all geometries.
 */
public abstract class Geometry extends Intersectable {
    private Color emission = Color.BLACK;
    private Material material = new Material();

    /**
     * Returns the normal vector to the geometry at the specified point.
     * @param point the point on the geometry
     * @return the normal vector
     */
    public abstract Vector getNormal(Point point);

    /**
     * Sets the emission color of the geometry.
     * @param emission the emission color
     * @return this for chaining
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Gets the emission color of the geometry.
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the material of the geometry.
     * @param material the material
     * @return this for chaining
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Gets the material of the geometry.
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }
}
