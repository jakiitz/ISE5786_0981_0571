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
}
