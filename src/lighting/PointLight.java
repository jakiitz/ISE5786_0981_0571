package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import primitives.Util;

import java.util.LinkedList;
import java.util.List;

public class PointLight extends Light implements LightSource {
    /** The position of the point light */
    protected final Point _position;
    private double _kC = 1;
    private double _kL = 0;
    private double _kQ = 0;

    // --- תוספות למיני פרויקט 1 (Soft Shadows) ---
    private double _radius = 0; // רדיוס ברירת מחדל הוא 0 (צל חד)
    private int _gridSize = 1;  // גודל הרשת. 1x1 אומר קרן אחת (ללא צללים רכים)

    public PointLight(Color intensity, Point position) {
        super(intensity);
        _position = position;
    }

    // ... ה-Setters הקיימים שלך ל-Kc, Kl, Kq ...
    public PointLight setKc(double kC) { _kC = kC; return this; }
    public PointLight setKl(double kL) { _kL = kL; return this; }
    public PointLight setKq(double kQ) { _kQ = kQ; return this; }
    public PointLight setKC(double kC) { return setKc(kC); }
    public PointLight setKL(double kL) { return setKl(kL); }
    public PointLight setKQ(double kQ) { return setKq(kQ); }

    /**
     * Sets the radius of the light source for Soft Shadows (Area Light).
     */
    public PointLight setRadius(double radius) {
        _radius = radius;
        return this;
    }

    /**
     * Sets the resolution of the grid for Soft Shadows (e.g., 9 means a 9x9 grid).
     */
    public PointLight setGridSize(int gridSize) {
        _gridSize = gridSize;
        return this;
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        double distance = p.distance(_position);
        double attenuation = _kC + _kL * distance + _kQ * distance * distance;
        return _intensity.reduce(attenuation);
    }

    @Override
    public double getDistance(Point p) {
        return p.distance(_position);
    }

    @Override
    public List<Point> getTargetAreaPoints(Point p) {
        List<Point> points = new LinkedList<>();
        points.add(_position); // תמיד נוסיף את נקודת המרכז

        // אם אין רדיוס או שהרשת היא 1, מחזירים רק את נקודת המרכז (צל חד)
        if (_radius == 0 || _gridSize <= 1) {
            return points;
        }

        // יצירת מערכת צירים למישור האור (ניצב לקרן המרכזית)
        Vector l = getL(p);
        Vector vUp;

        // בחירת וקטור עזר ליצירת מישור (מונע התאפסות)
        if (Util.isZero(l.getX()) && Util.isZero(l.getY())) {
            vUp = Vector.AXIS_X;
        } else {
            vUp = new Vector(-l.getY(), l.getX(), 0).normalize();
        }
        Vector vRight = l.crossProduct(vUp).normalize();

        double step = (_radius * 2) / _gridSize;

        for (int i = 0; i < _gridSize; i++) {
            for (int j = 0; j < _gridSize; j++) {
                // מתעלמים מנקודת המרכז כי כבר הוספנו אותה
                if (i == _gridSize / 2 && j == _gridSize / 2 && _gridSize % 2 != 0) {
                    continue;
                }

                double xMove = -_radius + (i + 0.5) * step;
                double yMove = -_radius + (j + 0.5) * step;

                // Jittering: הזזה אקראית קטנה בתוך התא למראה טבעי יותר (מונע Artifacts)
                xMove += (Math.random() - 0.5) * step;
                yMove += (Math.random() - 0.5) * step;

                Point gridPoint = _position;
                if (!Util.isZero(xMove)) gridPoint = gridPoint.add(vRight.scale(xMove));
                if (!Util.isZero(yMove)) gridPoint = gridPoint.add(vUp.scale(yMove));

                points.add(gridPoint);
            }
        }
        return points;
    }
}