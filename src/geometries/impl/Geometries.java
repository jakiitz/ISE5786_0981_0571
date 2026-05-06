package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Geometries represents a collection of geometries that can be intersected by rays.
 */
public class Geometries extends Intersectable {
    private final List<Intersectable> geometries = new ArrayList<>();

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    @Override
    protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersectable.Intersection> result = null;
        for (Intersectable item : geometries) {
            var itemIntersections = item.calcIntersections(ray);
            if (itemIntersections != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}
