package geometries.impl;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;
import geometries.api.BoundingBox;
import geometries.api.Geometry;
import geometries.api.Intersectable;
import primitives.*;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
   /** Ordered list of polygon vertices */
   protected final List<Point> _vertices;
   /** Plane containing the polygon */
   protected final Plane       _plane;
   /** Number of vertices */
   private final int           _size;

   /**
    * Constructs a convex polygon from ordered vertices.
    * <p>
    * The vertices must:
    * </p>
    * <ul>
    * <li>Contain at least three points</li>
    * <li>Be ordered along the polygon edge path</li>
    * <li>Lie in the same plane</li>
    * <li>Form a convex polygon</li>
    * </ul>
    * @param  vertices                 polygon vertices in edge order
    * @throws IllegalArgumentException if the vertices do not form a valid convex
    *                                  polygon
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");

      this._vertices = List.of(vertices);
      this._size     = vertices.length;

      // Create the supporting plane using the first three vertices.
      this._plane    = new Plane(vertices[0], vertices[1], vertices[2]);
      if (this._size == 3) {
         // עבור משולש, בונים את ה-box כאן ומסיימים
         this.box = new BoundingBox();
         for (Point p : vertices) this.box.add(p);
         return;
      }

      Vector  n        = _plane.getNormal(vertices[0]);
      Vector  edge1    = vertices[_size - 1].subtract(vertices[_size - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[_size - 1]);

      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < _size; ++i) {
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");

         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }

      // כאן ה-box נבנה רק אחרי שווידאנו שהמצולע תקין
      this.box = new BoundingBox();
      for (Point p : vertices) {
         this.box.add(p);
      }
   }

   @Override
   public Vector getNormal(Point point) { return _plane.getNormal(point); }

   /** Finds intersections of the polygon with a given ray.
    * @param ray the ray to intersect with
    * @return list of intersection objects (null if no intersections)
    */
   @Override
   protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray) {
      Vector v    = ray.direction();
      Point  head = ray.origin();
      Vector n    = _plane.getNormal(null);

      double nv = n.dotProduct(v);
      if (isZero(nv)) return null;

      Vector p0MinusHead;
      try {
         p0MinusHead = _vertices.get(0).subtract(head);
      } catch (IllegalArgumentException e) {
         return null;
      }

      double t = alignZero(n.dotProduct(p0MinusHead) / nv);
      if (t <= 0) return null;

      // Build vectors from ray origin to each vertex
      Vector[] vecs = new Vector[_size];
      for (int i = 0; i < _size; i++) {
         vecs[i] = _vertices.get(i).subtract(head);
      }

      // For a convex polygon all consecutive-edge cross products must have
      // the same sign when dotted with the ray direction
      boolean positive = false;
      for (int i = 0; i < _size; i++) {
         Vector ni;
         try {
            ni = vecs[i].crossProduct(vecs[(i + 1) % _size]).normalize();
         } catch (IllegalArgumentException e) {
            return null;
         }
         double sign = alignZero(v.dotProduct(ni));
         if (isZero(sign)) return null;
         if (i == 0) positive = sign > 0;
         else if (positive != (sign > 0)) return null;
      }

      return List.of(new Intersectable.Intersection(this, ray.getPoint(t)));
   }
}