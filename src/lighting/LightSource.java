package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import java.util.List;

/**
 * Marker interface for light sources.
 */
public interface LightSource {
    /*Calculating a normalized direction vector from a light source to an illuminated point*/
   Vector getL(Point p);

    /*Calculating the intensity of the light at a given point, taking into account factors such as distance and attenuation*/
   Color getIntensity(Point p);

    /**
     * Calculates the distance from the light source to a given point.
     * @param p the given point
     * @return the distance
     */
    double getDistance(Point p);

    /**
     * Gets a grid of points on the target area of the light source for Soft Shadows calculation.
     * If the light source has no physical size (radius = 0), it returns a list with a single point.
     * @param p The illuminated point on the geometry.
     * @return A list of points distributed on the light's surface area.
     */
    default List<Point> getTargetAreaPoints(Point p) {
        return null;
    }
}
