package renderer;

import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import primitives.Ray;
import scene.Scene;

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

    ImageWriter _imageWriter;
    RayTracerBase _rayTracer;


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
     * Renders the image by casting rays through each pixel and using the ray tracer to determine the color.
     * The resulting image is stored in the ImageWriter.
     * @return this Camera instance for chaining
     */
    Camera renderImage()
    {
        // Define a renderImage Camera() method and implement it with a loop over all pixels,
        // inside which a castRay helper method is invoked for each pixel.
        if(_imageWriter == null)
        {
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        }
        if(_rayTracer == null)        {
            throw new MissingResourceException("RayTracer is not set", Camera.class.getName(), "RayTracer");
        }
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }

        return this; // Supports method chaining

    }
    /**
     * Casts a ray through a specific pixel and writes its color to the image.
     * @param xIndex The column index of the pixel
     * @param yIndex The row index of the pixel
     */
    private void castRay(int xIndex, int yIndex) {
        Ray ray = constructRay(xIndex, yIndex);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(xIndex, yIndex, color);
    }
    /**
     * Prints a grid on top of the existing image without casting new rays.
     * @param interval The size of each square in the grid (in pixels)
     * @param color The color of the grid lines
     * @return the camera object itself
     */
    public Camera printGrid(int interval, Color color) {
        if (_imageWriter == null) {
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        }
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this; // Supports method chaining
    }
    /**
     * Delegates the image creation to the image writer.[cite: 1]
     * @param fileName the name of the output file
     */
    public void writeToImage(String fileName) {
        if (_imageWriter == null) {
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        }
        _imageWriter.writeToImage(fileName);
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
        private RayTracerBase _rayTracer;

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported RayTracerType: " + type);
            }
            return this;
        }

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
            // check for required parameters and set defaults where needed
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
                vUp = Vector.AXIS_Y;
            }

            // check that vTo and vUp are not parallel
            if (vpWidth == null || vpHeight == null || vpWidth <= 0 || vpHeight <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }

            if (vpDistance == null || vpDistance <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive");
            }

            if (nX == null) nX = 1;
            if (nY == null) nY = 1;

            if (nX <= 0 || nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }

            // check if it is null
            if (this._rayTracer == null) {
                this.setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            // create
            Camera cam = new Camera();

            // create the ImageWriter and assign it to the camera
            cam._imageWriter = new ImageWriter(nX, nY);

            // assign the RayTracer to the camera
            cam._rayTracer = this._rayTracer;

            // normalize direction vectors and compute the right vector
            vTo = vTo.normalize();
            vUp = vUp.normalize();
            Vector vRight = vTo.crossProduct(vUp).normalize();

            cam.location = location;
            cam.vTo = vTo;
            cam.vUp = vUp;
            cam.vRight = vRight;
            cam.vpWidth = vpWidth;
            cam.vpHeight = vpHeight;
            cam.vpDistance = vpDistance;
            cam.nX = nX;
            cam.nY = nY;


            cam.pc = location.add(vTo.scale(vpDistance));
            cam.pixelWidth = vpWidth / nX;
            cam.pixelHeight = vpHeight / nY;

            // The camera is now fully initialized and ready to use
            return cam;
        }
    }
}

