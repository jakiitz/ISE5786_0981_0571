package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Geometries represents a composite collection of geometries that can be intersected by rays.
 * 
 * This class implements the Composite Design Pattern, allowing a hierarchical grouping of geometric objects.
 * It can contain any collection of Intersectable objects, including other Geometries instances, enabling
 * nested scene structures. When a ray intersection is calculated, the method delegates the calculation to
 * each contained geometry and aggregates all intersection results into a single list.
 * 
 * Key Features:
 * - Treats individual geometries and collections uniformly
 * - Supports recursive composition (Geometries can contain other Geometries)
 * - Aggregates intersection results from all child geometries
 * - Provides a convenient way to manage groups of objects in a scene
 * 
 * Example usage:
 * <pre>
 * Geometries scene = new Geometries(sphere1, sphere2, triangle1);
 * Geometries subGroup = new Geometries(plane1, plane2);
 * scene.add(subGroup);  // Can add both leaf and composite geometries
 * List<Intersection> intersections = scene.findIntersections(ray);  // Gets all intersections
 * </pre>
 */
public class Geometries extends Intersectable {
    
    /** Collection of all geometries contained in this composite (leaf or composite geometries) */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Constructs a Geometries instance with a variable number of geometries.
     * All provided geometries are added to the internal collection.
     * 
     * @param geometries varargs array of Intersectable objects to add to this composite
     */
    public Geometries(Intersectable... geometries) {
        // Initialize the composite with provided geometries
        add(geometries);
    }

    /**
     * Adds one or more geometries to this composite collection.
     * Can add both individual geometries (leaves) and other Geometries instances (composites).
     * 
     * @param geometries varargs array of Intersectable objects to add
     */
    public void add(Intersectable... geometries) {
        // Add all provided geometries to the internal collection
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Calculates all intersection points where a ray intersects with any contained geometry.
     * This method implements the core of the Composite Pattern by delegating to each child geometry
     * and aggregating the results.
     * 
     * Algorithm:
     * 1. Initialize result list as null (lazy initialization for efficiency)
     * 2. For each geometry in the collection:
     *    - Calculate intersections for that geometry with the ray
     *    - If intersections exist, add them to the result list
     * 3. Return the aggregated list of all intersections
     * 
     * @param ray the ray to test for intersections with contained geometries
     * @return a list of all intersections found from all contained geometries,
     *         or null if no intersections exist
     */
    @Override
    protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray) {
        // Step 1: Initialize result list as null for lazy initialization (more efficient)
        List<Intersectable.Intersection> result = null;
        
        // Step 2: Iterate through all contained geometries
        for (Intersectable item : geometries) {
            // Step 3: Calculate intersections for this geometry with the ray
            var itemIntersections = item.calcIntersections(ray);
            
            // Step 4: If this geometry has intersections, add them to the result
            if (itemIntersections != null) {
                // Step 5: Create result list on first intersection found (lazy initialization)
                if (result == null) {
                    result = new ArrayList<>();
                }
                // Step 6: Aggregate all intersections from this geometry into the result list
                result.addAll(itemIntersections);
            }
        }
        
        // Step 7: Return the aggregated list (null if no intersections found)
        return result;
    }
}
