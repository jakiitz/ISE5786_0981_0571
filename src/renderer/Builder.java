package renderer;

import primitives.Point;
import primitives.Vector;
import java.util.MissingResourceException;

/**
 * Builder class for constructing Camera instances.
 */
public class Builder {
    private final Camera _camera = new Camera();
    // Temporary fields
    private Vector _to;
    private Point _target;
    private Vector _up;

    /**
     * Sets the camera location.
     * @param location the camera position
     * @return this Builder
     */
    public Builder setLocation(Point location) {
        _camera.location = location;
        return this;
    }

    /**
     * Sets the camera direction using to and up vectors.
     * @param to the direction vector
     * @param up the up vector
     * @return this Builder
     */
    public Builder setDirection(Vector to, Vector up) {
        _to = to;
        _up = up;
        return this;
    }

    /**
     * Sets the camera direction towards a target point with up vector.
     * @param target the target point
     * @param up the up vector
     * @return this Builder
     */
    public Builder setDirection(Point target, Vector up) {
        _target = target;
        _up = up;
        return this;
    }

    /**
     * Sets the camera direction towards a target point.
     * @param target the target point
     * @return this Builder
     */
    public Builder setDirection(Point target) {
        _target = target;
        _up = Vector.AXIS_Y;
        return this;
    }

    /**
     * Sets the view plane size.
     * @param width the width of the view plane
     * @param height the height of the view plane
     * @return this Builder
     */
    public Builder setVpSize(double width, double height) {
        _camera.vpWidth = width;
        _camera.vpHeight = height;
        return this;
    }

    /**
     * Sets the view plane distance.
     * @param distance the distance to the view plane
     * @return this Builder
     */
    public Builder setVpDistance(double distance) {
        _camera.vpDistance = distance;
        return this;
    }

    /**
     * Sets the view plane resolution.
     * @param nX number of pixels in width
     * @param nY number of pixels in height
     * @return this Builder
     */
    public Builder setResolution(int nX, int nY) {
        _camera.nX = nX;
        _camera.nY = nY;
        return this;
    }

    /**
     * Builds the Camera instance.
     * @return the constructed Camera
     */
    public Camera build() {
        checkResolution();
        checkLocationAndDirection();
        checkViewPlane();
        return _camera.clone();
    }

    // Helper methods
    private void checkResolution() {
        if (_camera.nX <= 0 || _camera.nY <= 0) {
            throw new IllegalArgumentException("Resolution must be positive");
        }
    }

    private void checkLocationAndDirection() {
        if (_camera.location == null || _up == null || (_to == null && _target == null)) {
            throw new MissingResourceException("Missing camera parameters", "Camera", "location/direction");
        }
        Vector to = _to;
        if (to == null) {
            to = _target.subtract(_camera.location);
        }
        _camera.vTo = to.normalize();
        Vector cross = _camera.vTo.crossProduct(_up);
        if (cross.lengthSquared() == 0) {
            throw new IllegalArgumentException("Vectors are parallel");
        }
        _camera.vRight = cross.normalize();
        _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
    }

    private void checkViewPlane() {
        if (_camera.vpWidth <= 0 || _camera.vpHeight <= 0 || _camera.vpDistance <= 0) {
            throw new IllegalArgumentException("View plane parameters must be positive");
        }
        _camera.pc = _camera.location.add(_camera.vTo.scale(_camera.vpDistance));
        _camera.pixelWidth = _camera.vpWidth / _camera.nX;
        _camera.pixelHeight = _camera.vpHeight / _camera.nY;
    }
}
