package renderer;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import primitives.Ray;
import scene.Scene;

/**
 * Represents a camera in 3D space for rendering scenes.
 * Uses the Builder pattern for construction.
 * Supports single-threaded, raw-thread, and parallel-stream rendering modes.
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
    Point pc;
    double pixelWidth, pixelHeight;

    ImageWriter _imageWriter;
    RayTracerBase _rayTracer;

    // 0 = single-threaded, -1 = parallel stream, -2 = auto raw threads, N>0 = N raw threads
    private int _threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double _printInterval = 0;
    private PixelManager _pixelManager;

    // Package-private default constructor
    Camera() {}

    /**
     * Returns a new Builder instance for constructing a Camera.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera through a specific pixel on the view plane.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     * @return the constructed ray
     */
    public Ray constructRay(int xIndex, int yIndex) {
        double xOffset = (xIndex - (nX - 1) / 2.0) * pixelWidth;
        double yOffset = ((nY - 1) / 2.0 - yIndex) * pixelHeight;
        Point pixelCenter = pc;
        if (xOffset != 0)
            pixelCenter = pixelCenter.add(vRight.scale(xOffset));
        if (yOffset != 0)
            pixelCenter = pixelCenter.add(vUp.scale(yOffset));
        return new Ray(location, pixelCenter.subtract(location));
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
     * Validates resources, initialises the pixel manager, then dispatches to
     * the appropriate rendering strategy based on {@code _threadsCount}.
     *
     * @return this Camera for method chaining
     */
    public Camera renderImage() {
        if (_imageWriter == null)
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        if (_rayTracer == null)
            throw new MissingResourceException("RayTracer is not set", Camera.class.getName(), "RayTracer");

        _pixelManager = new PixelManager(nY, nX, _printInterval);

        return switch (_threadsCount) {
            case 0  -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Single-threaded rendering — iterates over every pixel sequentially.
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; i++)
            for (int j = 0; j < nX; j++)
                castRay(j, i);
        return this;
    }

    /**
     * Multi-threaded rendering using {@code _threadsCount} raw Java threads.
     * Each thread pulls the next available pixel from the PixelManager.
     */
    private Camera renderImageRawThreads() {
        LinkedList<Thread> threads = new LinkedList<>();
        for (int t = 0; t < _threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = _pixelManager.nextPixel()) != null)
                    castRay(pixel.row(), pixel.col());
            }));
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return this;
    }

    /**
     * Multi-threaded rendering using parallel streams over both axes.
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(y -> IntStream.range(0, nX).parallel()
                        .forEach(x -> castRay(x, y)));
        return this;
    }

    /**
     * Casts a ray through pixel (xIndex, yIndex), traces its colour, and records it.
     */
    private void castRay(int xIndex, int yIndex) {
        Color color = _rayTracer.traceRay(constructRay(xIndex, yIndex));
        _imageWriter.writePixel(xIndex, yIndex, color);
        _pixelManager.pixelDone();
    }

    /**
     * Prints a grid over the rendered image.
     *
     * @param interval pixel spacing between grid lines
     * @param color    grid line colour
     * @return this Camera for method chaining
     */
    public Camera printGrid(int interval, Color color) {
        if (_imageWriter == null)
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        for (int i = 0; i < nY; i++)
            for (int j = 0; j < nX; j++)
                if (i % interval == 0 || j % interval == 0)
                    _imageWriter.writePixel(j, i, color);
        return this;
    }

    /**
     * Delegates image file creation to the ImageWriter.
     *
     * @param fileName output file name
     */
    public void writeToImage(String fileName) {
        if (_imageWriter == null)
            throw new MissingResourceException("ImageWriter is not set", Camera.class.getName(), "ImageWriter");
        _imageWriter.writeToImage(fileName);
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /** Builder for Camera using a fluent API. */
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
        private int threadsCount = 0;
        private double printInterval = 0;

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE)
                this._rayTracer = new SimpleRayTracer(scene);
            else
                throw new IllegalArgumentException("Unsupported RayTracerType: " + type);
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

        /**
         * Configures multi-threading mode.
         *
         * @param threads 0 = single-threaded, -1 = parallel stream,
         *                -2 = auto (cores − {@code SPARE_THREADS}), N>0 = N raw threads
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("threads must be >= -2");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors();
                threadsCount = cores <= 2 ? 1 : cores - SPARE_THREADS;
            } else {
                threadsCount = threads;
            }
            return this;
        }

        /**
         * Enables periodic progress printing during rendering.
         *
         * @param interval seconds between progress prints (0 disables)
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("print interval must be >= 0");
            printInterval = interval;
            return this;
        }

        /** Constructs and returns the fully initialised Camera. */
        public Camera build() {
            if (location == null)
                throw new MissingResourceException("Camera location is missing", Camera.class.getName(), "location");

            if (vTo == null && targetPoint != null)
                vTo = targetPoint.subtract(location).normalize();

            if (vTo == null)
                throw new MissingResourceException("Camera direction is missing", Camera.class.getName(), "direction");

            if (vUp == null)
                vUp = Vector.AXIS_Y;

            if (vpWidth == null || vpHeight == null || vpWidth <= 0 || vpHeight <= 0)
                throw new IllegalArgumentException("View plane size must be positive");

            if (vpDistance == null || vpDistance <= 0)
                throw new IllegalArgumentException("View plane distance must be positive");

            if (nX == null) nX = 1;
            if (nY == null) nY = 1;

            if (nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            if (this._rayTracer == null)
                this.setRayTracer(new Scene("test"), RayTracerType.SIMPLE);

            Camera cam = new Camera();
            cam._imageWriter = new ImageWriter(nX, nY);
            cam._rayTracer   = this._rayTracer;

            vTo = vTo.normalize();
            vUp = vUp.normalize();
            Vector vRight = vTo.crossProduct(vUp).normalize();

            cam.location    = location;
            cam.vTo         = vTo;
            cam.vUp         = vUp;
            cam.vRight      = vRight;
            cam.vpWidth     = vpWidth;
            cam.vpHeight    = vpHeight;
            cam.vpDistance  = vpDistance;
            cam.nX          = nX;
            cam.nY          = nY;
            cam.pc          = location.add(vTo.scale(vpDistance));
            cam.pixelWidth  = vpWidth  / nX;
            cam.pixelHeight = vpHeight / nY;
            cam._threadsCount  = threadsCount;
            cam._printInterval = printInterval;

            return cam;
        }
    }
}
