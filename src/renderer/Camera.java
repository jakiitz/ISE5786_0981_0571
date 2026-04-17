package renderer;

import java.util.MissingResourceException;
import primitives.Point;
import primitives.Vector;
import primitives.Ray;

/**
 * Represents a camera in 3D space for rendering scenes.
 * Uses the Builder pattern for construction.
 */
public class Camera implements Cloneable {
    // Camera location
    Point location;
    // Camera direction vectors
    Vector vTo, vUp, vRight;
    // View plane dimensions
    double vpWidth, vpHeight, vpDistance;
    // View plane resolution
    int nX = 1, nY = 1;
    // Computed fields
    Point pc; // Center of view plane
    double pixelWidth, pixelHeight;

    // Package-private default constructor
    Camera() {}

    /**
     * Returns a new Builder instance for constructing a Camera.
     * @return a new Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera through a specific pixel on the view plane.
     * @param xIndex the column index of the pixel
     * @param yIndex the row index of the pixel
     * @return the constructed ray
     */
    public Ray constructRay(int xIndex, int yIndex) {
        double xOffset = (xIndex - (nX - 1) / 2.0) * pixelWidth;
        double yOffset = ((nY - 1) / 2.0 - yIndex) * pixelHeight;
        Point pixelCenter = pc;
        if (xOffset != 0) {
            pixelCenter = pixelCenter.add(vRight.scale(xOffset));
        }
        if (yOffset != 0) {
            pixelCenter = pixelCenter.add(vUp.scale(yOffset));
        }
        Vector direction = pixelCenter.subtract(location);
        return new Ray(location, direction);
    }

    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
