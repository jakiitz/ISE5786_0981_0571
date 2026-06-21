package geometries.impl;

import geometries.api.Intersectable;
import geometries.api.BoundingBox;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import primitives.Ray;
import primitives.Point;

public class Geometries extends Intersectable {
    private final List<Intersectable> geometries = new ArrayList<>();

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    public void add(Intersectable... geometriesToAdd) {
        Collections.addAll(this.geometries, geometriesToAdd);

        // בנייה מחדש של הקופסה התוחמת
        this.box = new BoundingBox();
        for (Intersectable g : this.geometries) {
            if (g.box == null) {
                // אם יש בקבוצה גוף אינסופי (כמו מישור), הקבוצה כולה נחשבת אינסופית!
                this.box = null;
                return;
            }
            this.box.add(g.box);
        }
    }

    @Override
    protected List<Intersectable.Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersectable.Intersection> result = null;
        for (Intersectable item : geometries) {
            var itemIntersections = item.calcIntersections(ray);
            if (itemIntersections != null) {
                if (result == null) result = new ArrayList<>();
                result.addAll(itemIntersections);
            }
        }
        return result;
    }

    public void buildBVH() {
        if (geometries.size() <= 2) return;

        // 1. הפרדה בין גופים סופיים לאינסופיים (מישורים)
        List<Intersectable> finites = new ArrayList<>();
        List<Intersectable> infinites = new ArrayList<>();

        for (Intersectable g : geometries) {
            if (g.box != null) finites.add(g);
            else infinites.add(g);
        }

        // אם אין מספיק גופים סופיים כדי לבנות עץ, עוצרים
        if (finites.size() <= 2) return;

        // 2. בניית ה-BVH רק על הגופים הסופיים
        BoundingBox centersBox = new BoundingBox();
        for (Intersectable g : finites) {
            centersBox.add(new Point(
                    (g.box.minX + g.box.maxX) / 2,
                    (g.box.minY + g.box.maxY) / 2,
                    (g.box.minZ + g.box.maxZ) / 2));
        }

        double dx = centersBox.maxX - centersBox.minX;
        double dy = centersBox.maxY - centersBox.minY;
        double dz = centersBox.maxZ - centersBox.minZ;
        int axis = (dx > dy && dx > dz) ? 0 : (dy > dz ? 1 : 2);

        finites.sort((g1, g2) -> {
            double c1 = (axis == 0) ? (g1.box.minX + g1.box.maxX) / 2 : (axis == 1) ? (g1.box.minY + g1.box.maxY) / 2 : (g1.box.minZ + g1.box.maxZ) / 2;
            double c2 = (axis == 0) ? (g2.box.minX + g2.box.maxX) / 2 : (axis == 1) ? (g2.box.minY + g2.box.maxY) / 2 : (g2.box.minZ + g2.box.maxZ) / 2;
            return Double.compare(c1, c2);
        });

        int mid = finites.size() / 2;
        List<Intersectable> leftList = new ArrayList<>(finites.subList(0, mid));
        List<Intersectable> rightList = new ArrayList<>(finites.subList(mid, finites.size()));

        Geometries left = new Geometries(leftList.toArray(new Intersectable[0]));
        Geometries right = new Geometries(rightList.toArray(new Intersectable[0]));

        left.buildBVH();
        right.buildBVH();

        // 3. ניקוי הרשימה והרכבה מחדש - מחזירים את המישורים
        this.geometries.clear();
        this.add(left, right); // הוספת עץ ה-BVH

        if (!infinites.isEmpty()) {
            this.add(infinites.toArray(new Intersectable[0])); // הוספת המישורים בחזרה
        }
    }
}