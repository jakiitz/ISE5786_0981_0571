package renderer;

import geometries.impl.*;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

class BuildSceneTest {

    /** Thread count: -2=auto raw threads, -1=parallel stream, 0=single-threaded */
    private static final int     THREADS = 0;

    private final Scene          scene  = new Scene("Build Scene");
    private final Camera.Builder camera = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE)
            .setMultithreading(THREADS).setDebugPrint(1);

    @Test
    @SuppressWarnings("java:S109")
    void buildScene() {

        scene.setBackground(new Color(0, 0, 0));
        scene.setAmbientLight(new AmbientLight(new Color(8, 7, 6)));

        // ── FLOOR — warm oak wood
        scene.geometries.add(
                new Plane(new Point(0, -150, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(95, 65, 35))
                        .setMaterial(new Material()
                                .setKD(0.65).setKS(0.2).setShininess(40).setKR(0.08))
        );

         // from hear
        // ── WOODEN COMMODE (שידה מעודכנת - צמודה לקיר השמאלי וקרובה יותר) ──
        Material commodeMat = new Material()
                .setKD(0.65).setKS(0.20).setShininess(30);
        Color cCommode = new Color(90, 50, 25); // צבע עץ כהה קלאסי
        Color cHandles = new Color(212, 175, 55); // צבע זהב לידיות
        Material handleMat = new Material().setKD(0.5).setKS(0.8).setShininess(200);

        // הגדרת מיקום חדש: צמוד לקיר השמאלי (X=-220) וקרוב יותר למצלמה (Z=-240)
        double xMin = -220, xMax = -160; // רוחב 60 יחידות
        double yMin = -150, yMax = -90;  // גובה 60 יחידות מהרצפה
        double zMin = -240, zMax = -180; // עומק 60 יחידות

        Point c_blUp = new Point(xMin, yMax, zMax); // קדמי-שמאל-עליון
        Point c_brUp = new Point(xMax, yMax, zMax); // קדמי-ימין-עליון
        Point c_trUp = new Point(xMax, yMax, zMin); // אחורי-ימין-עליון
        Point c_tlUp = new Point(xMin, yMax, zMin); // אחורי-שמאל-עליון

        Point c_blDown = new Point(xMin, yMin, zMax); // קדמי-שמאל-תחתון
        Point c_brDown = new Point(xMax, yMin, zMax); // קדמי-ימין-תחתון
        Point c_trDown = new Point(xMax, yMin, zMin); // אחורי-ימין-תחתון
        Point c_tlDown = new Point(xMin, yMin, zMin); // אחורי-שמאל-תחתון

        // 1. פלטה עליונה
        scene.geometries.add(new Polygon(c_blUp, c_brUp, c_trUp, c_tlUp).setEmission(cCommode).setMaterial(commodeMat));
        // 2. חזית (מגירות הפונות אל המצלמה)
        scene.geometries.add(new Polygon(c_blDown, c_brDown, c_brUp, c_blUp).setEmission(cCommode).setMaterial(commodeMat));
        // 3. דופן ימנית (הדופן שפונה למרכז החדר ונראית היטב)
        scene.geometries.add(new Polygon(c_brDown, c_trDown, c_trUp, c_brUp).setEmission(cCommode).setMaterial(commodeMat));
        // (אין צורך בדופן שמאלית ואחורית כי הן צמודות לחלוטין לקיר השמאלי ולקיר האחורי)

        // 4. חריצים דקורטיביים למגירות בחזית השידה (ZMax + 0.5 כדי למנוע Z-fighting)
        scene.geometries.add(new Polygon(new Point(xMin+2, -110, zMax+0.5), new Point(xMax-2, -110, zMax+0.5), new Point(xMax-2, -111, zMax+0.5), new Point(xMin+2, -111, zMax+0.5))
                .setEmission(new Color(15, 10, 5)).setMaterial(commodeMat));
        scene.geometries.add(new Polygon(new Point(xMin+2, -130, zMax+0.5), new Point(xMax-2, -130, zMax+0.5), new Point(xMax-2, -131, zMax+0.5), new Point(xMin+2, -131, zMax+0.5))
                .setEmission(new Color(15, 10, 5)).setMaterial(commodeMat));

        // 5. ידיות מוזהבות במרכז החזית של כל מגירה
        double xCenter = (xMin + xMax) / 2;

        // מגירה עליונה (Y = -100)
        scene.geometries.add(new Cylinder(1.5, new Ray(new Point(xCenter, -100, zMax), new Vector(0, 0, 1)), 3).setEmission(cHandles).setMaterial(handleMat));
        // מגירה אמצעית (Y = -120)
        scene.geometries.add(new Cylinder(1.5, new Ray(new Point(xCenter, -120, zMax), new Vector(0, 0, 1)), 3).setEmission(cHandles).setMaterial(handleMat));
        // מגירה תחתונה (Y = -140)
        scene.geometries.add(new Cylinder(1.5, new Ray(new Point(xCenter, -140, zMax), new Vector(0, 0, 1)), 3).setEmission(cHandles).setMaterial(handleMat));
        // to hear
        // ── SHINY TRANSPARENT SPHERE ON THE COMMODE (FIXED OVEREXPOSURE) ──
        // ── SHINY GOLD CYLINDER ON THE COMMODE ──
        Material goldMat = new Material()
                .setKD(0.4)        // פיזור אור מתכתי
                .setKS(0.9)        // ברק חזק מאוד (Specular חריף)
                .setShininess(600) // נקודת אור מרוכזת וחדה למראה מתכת מלוטשת
                .setKR(0.12);      // השתקפות עדינה של הסביבה שמוסיפה למראה הריאליסטי של הזהב

        Color cGold = new Color(212, 175, 55); // צבע זהב קלאסי (Gold Metallic)

        // מיקום הצילינדר:
        // נקודת ההתחלה היא על משטח השידה (Y = -90) במרכז ה-X וה-Z שלה.
        // כיוון ה-Vector הוא כלפי מעלה (0, 1, 0) בציר ה-Y.
        // רדיוס: 14 יחידות, גובה (אורך הגליל): 8 יחידות.
        scene.geometries.add(
                new Cylinder(14, new Ray(new Point(-190, -90, -210), new Vector(0, 1, 0)), 8)
                        .setEmission(cGold)
                        .setMaterial(goldMat)
        );
        // to hear
        // --- 1. שלושה כדורים שקופים, מבריקים במיוחד וללא השתקפות על השולחן ---
        Material ultraShinyGlassMat = new Material()
                .setKD(0.0)         // אפס פיזור אטום - למניעת מראה חלבי
                .setKS(1.0)         // עוצמת ברק מקסימלית (Specular חזק)
                .setShininess(900)  // ריכוז גבוה מאוד - יוצר נקודת אור קטנה, חדה ומבריקה מהמנורה
                .setKT(0.80)        // שקיפות גבוהה ונקייה (80% מעבר אור)
                .setKR(0.0);        // ללא השתקפויות מראה של החדר בכלל!

        // כדור שקוף ומבריק סגול-חציל
        scene.geometries.add(new Sphere(new Point(-30, -38, -140), 12)
                .setEmission(new Color(65, 25, 90))
                .setMaterial(ultraShinyGlassMat));

        // כדור שקוף ומבריק כחול-ים
        scene.geometries.add(new Sphere(new Point(0, -38, -150), 12)
                .setEmission(new Color(15, 72, 130))
                .setMaterial(ultraShinyGlassMat));

        // כדור שקוף ומבריק ירוק-אזמרגד
        scene.geometries.add(new Sphere(new Point(30, -38, -160), 12)
                .setEmission(new Color(23, 70, 43))
                .setMaterial(ultraShinyGlassMat));

        /// --- 2. כדור כרום כהה ומלוטש (מותאם לחלל בהיר) ---
        Material chromeMat = new Material()
                .setKD(0.0)         // אפס פיזור אטום - חובה למתכת נקייה
                .setKS(0.95)        // ברק ספקולרי מקסימלי עבור נקודות האור הלבנות
                .setShininess(1200) // ריכוז חד מאוד של נקודות האור
                .setKT(0.0)         // אטום לחלוטין
                .setKR(0.78);       // הורדה קלה של ה-KR (מ-0.95 ל-0.78) כדי שהסביבה הבהירה לא תשתלט עליו

        // במקום שחור מוחלט, נותנים צבע פליטה כהה מאוד (אפור פחם / כחול נייבי כהה)
        // הצבע הכהה הזה "ייבלע" בתוך הרינדור ויעניק לכדור את הגוון המטאלי הכהה שלו
        Color cChrome = new Color(12, 14, 16);

        // יצירת הכדור בקדמת החדר
        scene.geometries.add(new Sphere(new Point(-100, -110, -50), 40)
                .setEmission(cChrome)
                .setMaterial(chromeMat));
        // --- 3. כדור גדול אטום בפינה האחורית השמאלית (גובה מרכז 115-) ---
        scene.geometries.add(new Sphere(new Point(-170, -115, -340), 35)
                .setEmission(new Color(25, 25, 112))
                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100).setKR(0.12)));

        // ── PYRAMID — פירמידת קריסטל טורקיז (מוזזת לקדמת השולחן בצד ימין) ──
        Material pyramidMat = new Material()
                .setKD(0.4).setKS(0.6).setShininess(120).setKR(0.15);
        Color cPyramid = new Color(0, 150, 136);

        // FROM HEAR
        // ── MULTIPLE PYRAMIDS LINE (Along Z axis) ──
        Material pyramidMat2 = new Material()
                .setKD(0.4).setKS(0.6).setShininess(120).setKR(0.15);
        Color cPyramid2 = new Color(0, 150, 136);

// נניח שאנו רוצים 3 פירמידות בשורה אחת אחרי השניה לעומק
        for (int i = 0; i < 3; i++) {
            double offsetZ = i * -80; // 60- כדי להתקדם לעומק החדר (לכיוון הערכים השליליים של Z)

            Point apex = new Point(110, -50,   10 + offsetZ);      // קודקוד עליון (היה 40-)
            Point pBL  = new Point(80,  -150, -20 + offsetZ);      // בסיס: אחורי-שמאל (היה 70-)
            Point pBR  = new Point(140, -150, -20 + offsetZ);      // בסיס: אחורי-ימין (היה 70-)
            Point pFR  = new Point(140, -150,  40 + offsetZ);      // בסיס: קדמי-ימין (היה 10-)
            Point pFL  = new Point(80,  -150,  40 + offsetZ);      // בסיס: קדמי-שמאל (היה 10-)
            // בסיס הפירמידה
            scene.geometries.add(new Polygon(pFL, pFR, pBR, pBL).setEmission(cPyramid2).setMaterial(pyramidMat));
            // פאות משולשות
            scene.geometries.add(new Polygon(pFL, pFR, apex).setEmission(cPyramid2).setMaterial(pyramidMat2));
            scene.geometries.add(new Polygon(pFR, pBR, apex).setEmission(cPyramid2).setMaterial(pyramidMat2));
            scene.geometries.add(new Polygon(pBR, pBL, apex).setEmission(cPyramid2).setMaterial(pyramidMat2));
            scene.geometries.add(new Polygon(pBL, pFL, apex).setEmission(cPyramid2).setMaterial(pyramidMat2));
        }
        // TO HEAR

        // ----------------------------------------------------


        //-----------------------
        // ── CEILING — off-white
        scene.geometries.add(
                new Plane(new Point(0, 150, 0), new Vector(0, -1, 0))
                        .setEmission(new Color(195, 190, 180))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.1).setShininess(10))
        );

        // ── LEFT WALL — warm ivory
        scene.geometries.add(
                new Plane(new Point(-220, 0, 0), new Vector(1, 0, 0))
                        .setEmission(new Color(175, 162, 140))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.08).setShininess(10))
        );

        // ── RIGHT WALL — warm ivory
        scene.geometries.add(
                new Plane(new Point(220, 0, 0), new Vector(-1, 0, 0))
                        .setEmission(new Color(175, 162, 140))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.08).setShininess(10))
        );

        // ── BACK WALL — warm ivory
        scene.geometries.add(
                new Plane(new Point(0, 0, -380), new Vector(0, 0, 1))
                        .setEmission(new Color(175, 162, 140))
                        .setMaterial(new Material()
                                .setKD(0.7).setKS(0.08).setShininess(10))
        );

        // ── MIRROR 1 — in the back-right corner, angled 45°
        scene.geometries.add(
                new Plane(new Point(220, 0, -380), new Vector(-1, 0, 1))
                        .setEmission(new Color(4, 4, 6))
                        .setMaterial(new Material()
                                .setKD(0.03).setKS(0.05).setShininess(500).setKR(0.92))
        );



        // ── PINK SPHERES (RIGHT SIDE) — three stacked balls standing by the mirror corner
        Material pinkMat = new Material()
                .setKD(0.65).setKS(0.35).setShininess(80);
        Color cPink = new Color(220, 90, 130);

        scene.geometries.add(new Sphere(new Point(165, -110, -310), 40).setEmission(cPink).setMaterial(pinkMat));
        scene.geometries.add(new Sphere(new Point(165, -42, -310), 28).setEmission(cPink).setMaterial(pinkMat));
        scene.geometries.add(new Sphere(new Point(165, 4, -310), 18).setEmission(cPink).setMaterial(pinkMat));

        // ── PINK SPHERES (LEFT SIDE) — three stacked balls standing by the left wall
        scene.geometries.add(new Sphere(new Point(-165, -110, -310), 40).setEmission(cPink).setMaterial(pinkMat));
        scene.geometries.add(new Sphere(new Point(-165, -42, -310), 28).setEmission(cPink).setMaterial(pinkMat));
        scene.geometries.add(new Sphere(new Point(-165, 4, -310), 18).setEmission(cPink).setMaterial(pinkMat));

        // ── TABLE WITH THICK TABLETOP ──
        Material tableMat = new Material()
                .setKD(0.70).setKS(0.22).setShininess(35).setKR(0.05);
        Color cWood = new Color(128, 78, 38);

        Point tlUp = new Point(-81.5, -50, -156.0); // Back-Left
        Point trUp = new Point( 36.5, -50, -224.0); // Back-Right
        Point brUp = new Point( 81.5, -50, -144.0); // Front-Right
        Point blUp = new Point(-36.5, -50,  -76.0); // Front-Left

        Point tlDown = new Point(-81.5, -65, -156.0);
        Point trDown = new Point( 36.5, -65, -224.0);
        Point brDown = new Point( 81.5, -65, -144.0);
        Point blDown = new Point(-36.5, -65,  -76.0);

        // 1. פלטה עליונה
        scene.geometries.add(new Polygon(blUp, brUp, trUp, tlUp).setEmission(cWood).setMaterial(tableMat));
        // 2. דופן קדמית
        scene.geometries.add(new Polygon(blDown, brDown, brUp, blUp).setEmission(cWood).setMaterial(tableMat));
        // 3. דופן ימנית
        scene.geometries.add(new Polygon(brDown, trDown, trUp, brUp).setEmission(cWood).setMaterial(tableMat));
        // 4. דופן אחורית
        scene.geometries.add(new Polygon(trDown, tlDown, tlUp, trUp).setEmission(cWood).setMaterial(tableMat));
        // 5. דופן שמאלית
        scene.geometries.add(new Polygon(tlDown, blDown, blUp, tlUp).setEmission(cWood).setMaterial(tableMat));

        // Four legs
        Material legMat = new Material()
                .setKD(0.65).setKS(0.25).setShininess(40);
        Color cLeg = new Color(108, 65, 30);

        scene.geometries.add(new Cylinder(6, new Ray(new Point(-32, -150,  -85), new Vector(0, 1, 0)), 85).setEmission(cLeg).setMaterial(legMat));
        scene.geometries.add(new Cylinder(6, new Ray(new Point( 72, -150, -145), new Vector(0, 1, 0)), 85).setEmission(cLeg).setMaterial(legMat));
        scene.geometries.add(new Cylinder(6, new Ray(new Point( 32, -150, -215), new Vector(0, 1, 0)), 85).setEmission(cLeg).setMaterial(legMat));
        scene.geometries.add(new Cylinder(6, new Ray(new Point(-72, -150, -155), new Vector(0, 1, 0)), 85).setEmission(cLeg).setMaterial(legMat));

        // ── REALISTIC MIRROR 2 WITH WOODEN FRAME ──
        Material realisticMirrorMat = new Material()
                .setKD(0.0).setKS(0.02).setShininess(1000).setKT(0.0).setKR(0.98);

        scene.geometries.add(
                new Polygon(
                        new Point(-120, -20, -348),
                        new Point(  10, -20, -348),
                        new Point(  10, 120, -348),
                        new Point(-120, 120, -348))
                        .setEmission(new Color(0, 0, 0))
                        .setMaterial(realisticMirrorMat)
        );

        // Wooden frame
        Material frameMat = new Material().setKD(0.5).setKS(0.1).setShininess(20);
        Color frameColor = new Color(50, 30, 15);
        double fThick = 6;

        scene.geometries.add(new Polygon(new Point(-120-fThick, 120, -349), new Point(10+fThick, 120, -349), new Point(10+fThick, 120+fThick, -349), new Point(-120-fThick, 120+fThick, -349)).setEmission(frameColor).setMaterial(frameMat));
        scene.geometries.add(new Polygon(new Point(-120-fThick, -20-fThick, -349), new Point(10+fThick, -20-fThick, -349), new Point(10+fThick, -20, -349), new Point(-120-fThick, -20, -349)).setEmission(frameColor).setMaterial(frameMat));
        scene.geometries.add(new Polygon(new Point(-120-fThick, -20, -349), new Point(-120, -20, -349), new Point(-120, 120, -349), new Point(-120-fThick, 120, -349)).setEmission(frameColor).setMaterial(frameMat));
        scene.geometries.add(new Polygon(new Point(10, -20, -349), new Point(10+fThick, -20, -349), new Point(10+fThick, 120, -349), new Point(10, 120, -349)).setEmission(frameColor).setMaterial(frameMat));

        // ── PHYSICAL PENDANT LAMP ──
        scene.geometries.add(
                new Cylinder(1.5, new Ray(new Point(0, 120, -150), new Vector(0, 1, 0)), 30)
                        .setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))
        );

        scene.geometries.add(
                new Sphere(new Point(0, 110, -150), 12)
                        .setEmission(new Color(255, 245, 200))
                        .setMaterial(new Material()
                                .setKD(0.1)
                                .setKS(0.9)
                                .setShininess(200)
                                .setKT(0.3))
        );

        // ── LIGHTS ──
        // 1. SpotLight
        // ── LIGHTS (OPTIMIZED DESIGN) ──

        // 1. ה-SpotLight המרכזי (אור משימה - ממוקד לשולחן ולכדורים השקופים)
        // גוון חם, עוצמה טובה שמייצרת דרמה במרכז החדר
        scene.lights.add(
                new SpotLight(
                        new Color(130, 115, 85),
                        new Point(0, 95, -150),
                        new Vector(0, -1, 0))
                        .setKl(0.0004).setKq(0.000008)
        );

        // 2. ה-PointLight המרכזי (אור מילוי רך - מאיר את הקירות והחלל הכללי)
        // הורדנו מעט עוצמה (מ-45 ל-30) כדי שלא ישרוף את מה שעל השולחן
        scene.lights.add(
                new PointLight(
                        new Color(30, 28, 25),
                        new Point(0, 95, -150))
                        .setKl(0.0003).setKq(0.000005)
        );

        // 3. מקור אור חדש: ספוט עדין מעל הקומודה והכדור המתכתי (צד שמאל)
        // המטרה: לייצר השתקפויות יפות (Highlights) על החומרים המבריקים בצד שמאל
        scene.lights.add(
                new SpotLight(
                        new Color(50, 45, 35), // אור רך וחם
                        new Point(-180, 130, -210), // ממוקם גבוה מעל הקומודה
                        new Vector(0, -1, 0)) // מכוון ישירות למטה אל הקומודה והכדור
                        .setKl(0.0005).setKq(0.00001)
        );

        // ── CAMERA ──
        camera
                .setLocation(new Point(-60, 110, 410))
                .setDirection(new Point(60, -50, -200), Vector.AXIS_Y)
                .setVpDistance(180)
                .setVpSize(240, 160)
                .setResolution(4500, 3000)
                .build()
                .renderImage()
                .writeToImage("buildScene");
    }
}