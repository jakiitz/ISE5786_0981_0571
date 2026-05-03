package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;
// import geometries.Geometries; // יש לייבא את מחלקת הגיאומטריות שלך

/**
 * מחלקת סצנה (PDS - Public Data Structure)
 */
public class Scene {
    public String name;
    public Color background = Color.BLACK; // ברירת מחדל שחור
    public AmbientLight ambientLight = AmbientLight.NONE; // ברירת מחדל NONE
    public Geometries geometries = new Geometries(); // מאותחל לאובייקט חדש

    /**
     * Constructor for Scene
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    // Setters for the fields to allow chaining

    /**
     * Sets the background color of the scene.
     * @param background
     * @return
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     * @param ambientLight
     * @return
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }


}