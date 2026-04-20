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

    /**
     * Builder for Camera
     */
    public static class Builder {
        private Point location;
        private Vector vTo;
        private Vector vUp;
        private Point targetPoint;
        private Double vpWidth;
        private Double vpHeight;
        private Double vpDistance;
        private Integer nX;
        private Integer nY;

        public Builder setLocation(Point location) {
            this.location = location;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            this.vpWidth = width;
            this.vpHeight = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            this.vpDistance = distance;
            return this;
        }

        public Builder setResolution(int nx, int ny) {
            this.nX = nx;
            this.nY = ny;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            this.vTo = vTo;
            this.vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            this.targetPoint = target;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this.targetPoint = target;
            this.vUp = vUp;
            return this;
        }

        public Camera build() {
            Camera cam = new Camera();

            if (location == null) {
                throw new MissingResourceException("Camera location is missing", Camera.class.getName(), "location");
            }

            // Resolve direction
            if (vTo == null) {
                if (targetPoint != null) {
                    vTo = targetPoint.subtract(location).normalize();
                }
            }

            if (vTo == null) {
                throw new MissingResourceException("Camera direction is missing", Camera.class.getName(), "direction");
            }

            if (vUp == null) {
                // Default up vector
                vUp = Vector.AXIS_Y;
            }

            if (vpWidth == null || vpHeight == null) {
                throw new IllegalArgumentException("View plane size must be set");
            }

            if (vpWidth <= 0 || vpHeight <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }

            if (vpDistance == null) {
                throw new IllegalArgumentException("View plane distance must be set");
            }

            if (vpDistance <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive");
            }

            if (nX == null) {
                nX = 1;
            }
            if (nY == null) {
                nY = 1;
            }

            if (nX <= 0 || nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }

            // Normalize direction vectors
            vTo = vTo.normalize();
            vUp = vUp.normalize();

            // Compute right vector
            Vector vRight = vTo.crossProduct(vUp).normalize();

            // Assign to camera
            cam.location = location;
            cam.vTo = vTo;
            cam.vUp = vUp;
            cam.vRight = vRight;
            cam.vpWidth = vpWidth;
            cam.vpHeight = vpHeight;
            cam.vpDistance = vpDistance;
            cam.nX = nX;
            cam.nY = nY;

            // Compute view plane center and pixel sizes
            cam.pc = location.add(vTo.scale(vpDistance));
            cam.pixelWidth = vpWidth / nX;
            cam.pixelHeight = vpHeight / nY;

            return cam;
        }
    }
}
