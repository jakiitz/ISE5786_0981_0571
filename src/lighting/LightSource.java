package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

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
}
