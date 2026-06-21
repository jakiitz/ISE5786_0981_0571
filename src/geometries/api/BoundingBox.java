package geometries.api;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class BoundingBox {
    public double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
    public double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    public double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

    public BoundingBox() {}
    public BoundingBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    public void add(Point p) {
        // ב-Point שלך getXYZ() מחזיר את ה-Double3
        var xyz = p.getXYZ();

        // גישה לערכים ב-Double3 (הם מתודות record)
        double x = xyz._d1(), y = xyz._d2(), z = xyz._d3();

        if (x < minX) minX = x; if (x > maxX) maxX = x;
        if (y < minY) minY = y; if (y > maxY) maxY = y;
        if (z < minZ) minZ = z; if (z > maxZ) maxZ = z;
    }
    /**
     * Adds another BoundingBox to this one (merges them).
     */
    public void add(BoundingBox other) {
        if (other == null) return;
        if (other.minX < minX) minX = other.minX;
        if (other.maxX > maxX) maxX = other.maxX;
        if (other.minY < minY) minY = other.minY;
        if (other.maxY > maxY) maxY = other.maxY;
        if (other.minZ < minZ) minZ = other.minZ;
        if (other.maxZ > maxZ) maxZ = other.maxZ;
    }

    public boolean intersect(Ray ray) {
        Point p0 = ray.origin();
        var p0xyz = p0.getXYZ();
        var dir = ray.direction().getXYZ(); // ה-Vector שלך כנראה גם חושף את ה-Double3

        double ox = p0xyz._d1(), oy = p0xyz._d2(), oz = p0xyz._d3();
        double dx = dir._d1(), dy = dir._d2(), dz = dir._d3();

        // ... המשך פונקציית החיתוך כפי שכתבנו קודם ...
        double t1 = (minX - ox) / dx, t2 = (maxX - ox) / dx;
        double tMin = Math.min(t1, t2), tMax = Math.max(t1, t2);
        double ty1 = (minY - oy) / dy, ty2 = (maxY - oy) / dy;
        tMin = Math.max(tMin, Math.min(ty1, ty2));
        tMax = Math.min(tMax, Math.max(ty1, ty2));
        double tz1 = (minZ - oz) / dz, tz2 = (maxZ - oz) / dz;
        tMin = Math.max(tMin, Math.min(tz1, tz2));
        tMax = Math.min(tMax, Math.max(tz1, tz2));

        return tMax >= Math.max(0, tMin);
    }
}