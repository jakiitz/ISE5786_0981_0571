package renderer;

import geometries.impl.*;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * "The Golden Hall" – an ornate room with four pillars, a warm golden floor,
 * a ceiling, and a hourglass resting on a pedestal in the centre.
 *
 * Geometry inventory
 * ──────────────────
 *  Plane      × 2   floor + ceiling
 *  Cylinder   × 4   corner pillars
 *  Polygon    × 6   cube pedestal (6 square faces, each split into 2 triangles → 12 triangles, but we use Polygon directly)
 *  Sphere     × 2   hourglass bulbs (top & bottom)
 *  Triangle   × 8   hourglass waist (4 top-cone + 4 bottom-cone faces)
 *
 * Effects demonstrated
 * ────────────────────
 *  • Reflection  – floor, pillar surfaces
 *  • Transparency – top hourglass bulb (glass)
 *  • Shadows      – all objects cast shadows on the golden floor
 *  • Phong        – specular highlights on pillars and hourglass
 */
class ReflectionHallTest {

   private final Scene          scene  = new Scene("The Golden Hall");
   private final Camera.Builder camera = Camera.getBuilder()
           .setRayTracer(scene, RayTracerType.SIMPLE);

   // ── shared material helpers ────────────────────────────────────────────────

   /** Warm gold: reflective, shiny */
   private static final Material GOLD_FLOOR = new Material()
           .setKD(0.4).setKS(0.5).setShininess(120)
           .setKR(0.45);

   /** Marble pillar: soft reflection, high specular */
   private static final Material MARBLE = new Material()
           .setKD(0.5).setKS(0.6).setShininess(150)
           .setKR(0.25);

   /** Stone ceiling: matte, slight ambient */
   private static final Material STONE = new Material()
           .setKD(0.6).setKS(0.1).setShininess(10);

   /** Wooden pedestal cube */
   private static final Material WOOD = new Material()
           .setKD(0.7).setKS(0.3).setShininess(40)
           .setKR(0.05);

   /** Glass hourglass bulb – top (transparent) */
   private static final Material GLASS = new Material()
           .setKD(0.05).setKS(0.5).setShininess(200)
           .setKT(0.75).setKR(0.15);

   /** Amber sand bulb – bottom (opaque warm orange) */
   private static final Material SAND_GLASS = new Material()
           .setKD(0.4).setKS(0.4).setShininess(80)
           .setKT(0.35).setKR(0.1);

   /** Metal neck of the hourglass */
   private static final Material METAL = new Material()
           .setKD(0.2).setKS(0.8).setShininess(250)
           .setKR(0.5);

   // ── colours ───────────────────────────────────────────────────────────────

   private static final Color COL_GOLD      = new Color(180, 130, 20);
   private static final Color COL_CREAM     = new Color(240, 220, 170);
   private static final Color COL_PILLAR    = new Color(210, 190, 140);
   private static final Color COL_CEILING   = new Color(100,  80,  50);
   private static final Color COL_WOOD      = new Color(110,  60,  20);
   private static final Color COL_GLASS_TOP = new Color( 30, 100, 130);
   private static final Color COL_SAND      = new Color(200, 120,  30);
   private static final Color COL_METAL     = new Color( 60,  50,  40);

   // ── geometry helpers ──────────────────────────────────────────────────────

   /**
    * Builds one upright cylinder (pillar).
    * axis goes from (cx, floorY, cz) upward by 'height'.
    */
   private Cylinder pillar(double cx, double cz,
                           double radius, double height, double floorY) {
      return (Cylinder) new Cylinder(
              radius,
              new Ray(new Point(cx, floorY, cz), new Vector(0, 1, 0)),
              height
      ).setEmission(COL_PILLAR).setMaterial(MARBLE);
   }

   /**
    * Builds a rectangular face (Polygon) for the cube pedestal.
    * Vertices must be coplanar and in order.
    */
   private Polygon face(Point a, Point b, Point c, Point d) {
      return (Polygon) new Polygon(a, b, c, d)
              .setEmission(COL_WOOD).setMaterial(WOOD);
   }

   // ── test ──────────────────────────────────────────────────────────────────

   @Test
   @SuppressWarnings("java:S109")
   void goldenHall() {

      final double FLOOR_Y   = -100;
      final double CEILING_Y =  220;

      // ── FLOOR ──────────────────────────────────────────────────────────
      scene.geometries.add(
              new Plane(new Point(0, FLOOR_Y, 0), new Vector(0, 1, 0))
                      .setEmission(COL_GOLD)
                      .setMaterial(GOLD_FLOOR)
      );

      // ── CEILING ────────────────────────────────────────────────────────
      scene.geometries.add(
              new Plane(new Point(0, CEILING_Y, 0), new Vector(0, -1, 0))
                      .setEmission(COL_CEILING)
                      .setMaterial(STONE)
      );

      // ── FOUR PILLARS (corners) ─────────────────────────────────────────
      //   viewed from front: left-front, right-front, left-back, right-back
      double pillarH = CEILING_Y - FLOOR_Y;   // floor to ceiling
      double pillarR = 12;
      scene.geometries.add(
              pillar(-120, -80,  pillarR, pillarH, FLOOR_Y),   // front-left
              pillar( 120, -80,  pillarR, pillarH, FLOOR_Y),   // front-right
              pillar(-120, -280, pillarR, pillarH, FLOOR_Y),   // back-left
              pillar( 120, -280, pillarR, pillarH, FLOOR_Y)    // back-right
      );

      // ── PEDESTAL CUBE (centre of room) ────────────────────────────────
      //   a 40×40×40 cube centred at (0, FLOOR_Y+20, -180)
      double cx = 0, cy = FLOOR_Y + 20, cz = -180;
      double hs = 20; // half-size
      // 8 corner points
      Point pA = new Point(cx-hs, cy-hs, cz+hs); // front-bottom-left
      Point pB = new Point(cx+hs, cy-hs, cz+hs); // front-bottom-right
      Point pC = new Point(cx+hs, cy+hs, cz+hs); // front-top-right
      Point pD = new Point(cx-hs, cy+hs, cz+hs); // front-top-left
      Point pE = new Point(cx-hs, cy-hs, cz-hs); // back-bottom-left
      Point pF = new Point(cx+hs, cy-hs, cz-hs); // back-bottom-right
      Point pG = new Point(cx+hs, cy+hs, cz-hs); // back-top-right
      Point pH = new Point(cx-hs, cy+hs, cz-hs); // back-top-left

      scene.geometries.add(
              face(pA, pB, pC, pD),  // front
              face(pF, pE, pH, pG),  // back
              face(pD, pC, pG, pH),  // top
              face(pA, pE, pF, pB),  // bottom
              face(pE, pA, pD, pH),  // left
              face(pB, pF, pG, pC)   // right
      );

      // ── HOURGLASS ─────────────────────────────────────────────────────
      //   rests on top of the pedestal: top of cube is at cy+hs = FLOOR_Y+40
      double pedestalTop = cy + hs;  // y of cube top face
      double bulbR       = 18;       // radius of each glass sphere
      double waistY      = pedestalTop + bulbR + 10; // centre between the two bulbs
      double topBulbCY   = waistY + bulbR + 5;
      double botBulbCY   = waistY - bulbR - 5;

      // bottom bulb – sand-filled (opaque amber)
      scene.geometries.add(
              new Sphere(new Point(cx, botBulbCY, cz), bulbR)
                      .setEmission(COL_SAND)
                      .setMaterial(SAND_GLASS)
      );

      // top bulb – empty glass (transparent)
      scene.geometries.add(
              new Sphere(new Point(cx, topBulbCY, cz), bulbR)
                      .setEmission(COL_GLASS_TOP)
                      .setMaterial(GLASS)
      );

      // metal waist ring (4 triangles forming an X-cross silhouette around the neck)
      double neckY = waistY;
      double neckR = 5;   // neck half-width
      double neckH = 8;   // half-height of neck band
      scene.geometries.add(
              // front-face of neck band (2 triangles)
              new Triangle(
                      new Point(cx - neckR, neckY - neckH, cz + neckR),
                      new Point(cx + neckR, neckY - neckH, cz + neckR),
                      new Point(cx,         neckY + neckH, cz + neckR))
                      .setEmission(COL_METAL).setMaterial(METAL),
              new Triangle(
                      new Point(cx - neckR, neckY + neckH, cz + neckR),
                      new Point(cx + neckR, neckY + neckH, cz + neckR),
                      new Point(cx,         neckY - neckH, cz + neckR))
                      .setEmission(COL_METAL).setMaterial(METAL),
              // back-face of neck band (2 triangles)
              new Triangle(
                      new Point(cx - neckR, neckY - neckH, cz - neckR),
                      new Point(cx + neckR, neckY - neckH, cz - neckR),
                      new Point(cx,         neckY + neckH, cz - neckR))
                      .setEmission(COL_METAL).setMaterial(METAL),
              new Triangle(
                      new Point(cx - neckR, neckY + neckH, cz - neckR),
                      new Point(cx + neckR, neckY + neckH, cz - neckR),
                      new Point(cx,         neckY - neckH, cz - neckR))
                      .setEmission(COL_METAL).setMaterial(METAL),
              // left-face
              new Triangle(
                      new Point(cx - neckR, neckY - neckH, cz - neckR),
                      new Point(cx - neckR, neckY - neckH, cz + neckR),
                      new Point(cx - neckR, neckY + neckH, cz))
                      .setEmission(COL_METAL).setMaterial(METAL),
              // right-face
              new Triangle(
                      new Point(cx + neckR, neckY - neckH, cz - neckR),
                      new Point(cx + neckR, neckY - neckH, cz + neckR),
                      new Point(cx + neckR, neckY + neckH, cz))
                      .setEmission(COL_METAL).setMaterial(METAL),
              // top cap triangle
              new Triangle(
                      new Point(cx - neckR, neckY + neckH, cz - neckR),
                      new Point(cx + neckR, neckY + neckH, cz - neckR),
                      new Point(cx,         neckY + neckH, cz + neckR))
                      .setEmission(COL_METAL).setMaterial(METAL),
              // bottom cap triangle
              new Triangle(
                      new Point(cx - neckR, neckY - neckH, cz - neckR),
                      new Point(cx + neckR, neckY - neckH, cz - neckR),
                      new Point(cx,         neckY - neckH, cz + neckR))
                      .setEmission(COL_METAL).setMaterial(METAL)
      );

      // ── LIGHTING ──────────────────────────────────────────────────────
      scene.setAmbientLight(new AmbientLight(new Color(25, 20, 10)));

      // warm overhead spotlight (chandelierlike) – casts shadows from pillars & hourglass
      scene.lights.add(
              new SpotLight(new Color(900, 780, 500),
                      new Point(0, 200, -160),
                      new Vector(0, -1, -0.1))
                      .setKl(0.00002).setKq(0.0000005)
      );

      // soft fill light from front-left – reveals glass transparency
      scene.lights.add(
              new PointLight(new Color(300, 250, 180),
                      new Point(-200, 80, 150))
                      .setKl(0.0002).setKq(0.000005)
      );

      // warm backlight – adds depth and glows on the gold floor reflection
      scene.lights.add(
              new PointLight(new Color(200, 140, 60),
                      new Point(0, 50, -350))
                      .setKl(0.0003).setKq(0.000008)
      );

      // ── CAMERA ────────────────────────────────────────────────────────
      camera
              .setLocation(new Point(0, 80, 300))
              .setDirection(new Point(0, FLOOR_Y + 80, -180), Vector.AXIS_Y)
              .setVpDistance(350)
              .setVpSize(300, 300)
              .setResolution(600, 600)
              .build()
              .renderImage()
              .writeToImage("goldenHall");
   }
}