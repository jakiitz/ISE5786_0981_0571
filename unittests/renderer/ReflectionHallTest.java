package renderer;

import geometries.impl.*;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * "Crystal Stage" – a dramatic showcase scene.
 *
 * Layout (all effects clearly visible):
 *   - Mirror floor (kR=0.4)          → REFLECTION
 *   - Crystal centre sphere (kT=0.6) → TRANSPARENCY + shadow partial
 *   - Two coloured matte spheres      → crisp SHADOWS on floor
 *   - Two triangles forming a V-backdrop → reflects the spheres
 *   - SpotLight from upper-front      → SHADOWS + specular highlights
 *   - PointLight warm fill            → soft secondary lighting
 */
class FinalSceneTest {

    private final Scene          scene  = new Scene("Crystal Stage");
    private final Camera.Builder camera = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    @SuppressWarnings("java:S109")
    void crystalStage() {

        // ── MATERIALS ─────────────────────────────────────────────────────────

        // Mirror floor – dark, reflective
        Material mirrorFloor = new Material()
                .setKD(0.25).setKS(0.5).setShininess(100)
                .setKR(0.05);

        // Crystal glass sphere – transparent centre piece
        Material crystal = new Material()
                .setKD(0.15).setKS(0.3).setShininess(150)
                .setKT(0.2).setKR(0.05);

        // Deep red matte sphere
        Material redMatte = new Material()
                .setKD(0.7).setKS(0.3).setShininess(40);

        // Deep teal matte sphere
        Material tealMatte = new Material()
                .setKD(0.7).setKS(0.3).setShininess(40);

        // Backdrop triangles – dark, slightly reflective
        Material backdrop = new Material()
                .setKD(0.4).setKS(0.3).setShininess(20)
                .setKR(0.2);

        // ── COLOURS ───────────────────────────────────────────────────────────

        Color cFloor    = new Color(10,  10,  14);
        Color cCrystal  = new Color(20, 40, 60);
        Color cRed      = new Color(180,  30,  20);
        Color cTeal     = new Color( 20, 130, 130);
        Color cBackdrop = new Color( 60,  55,  80);

        // ── GEOMETRY ──────────────────────────────────────────────────────────

        // 1. Mirror floor
        scene.geometries.add(
                new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                        .setEmission(cFloor).setMaterial(mirrorFloor)
        );

        // 2. Crystal centre sphere
        scene.geometries.add(
                new Sphere(new Point(0, 10, -100), 50)
                        .setEmission(cCrystal).setMaterial(crystal)
        );

        // 3. Red sphere – left
        scene.geometries.add(
                new Sphere(new Point(-130, -10, -130), 35)
                        .setEmission(cRed).setMaterial(redMatte)
        );

        // 4. Teal sphere – right
        scene.geometries.add(
                new Sphere(new Point(130, -10, -130), 35)
                        .setEmission(cTeal).setMaterial(tealMatte)
        );

        // 5 & 6. Two triangles forming a V-shaped backdrop
        scene.geometries.add(
                new Triangle(
                        new Point(-400, -50, -350),
                        new Point(   0, -50, -220),
                        new Point(-200, 280, -350))
                        .setEmission(cBackdrop).setMaterial(backdrop),
                new Triangle(
                        new Point( 400, -50, -350),
                        new Point(   0, -50, -220),
                        new Point( 200, 280, -350))
                        .setEmission(cBackdrop).setMaterial(backdrop)
        );

        // ── LIGHTING ──────────────────────────────────────────────────────────

        // Very low ambient
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 16)));

        // Main spotlight – cold blue-white from upper front-left
        // casts shadows of all three spheres onto the floor
        scene.lights.add(
                new SpotLight(new Color(150, 155, 175),
                        new Point(-150, 250, 200),
                        new Vector(0.5, -1.2, -1))
                        .setKl(0.00008).setKq(0.000001)
        );

        // Warm fill from front-right – reveals the crystal transparency
        scene.lights.add(
                new PointLight(new Color(280, 180, 80),
                        new Point(200, 100, 250))
                        .setKl(0.0003).setKq(0.000006)
        );

        // Subtle back-light – rim light on the backdrop
        scene.lights.add(
                new PointLight(new Color(60, 40, 120),
                        new Point(0, 80, -380))
                        .setKl(0.0008).setKq(0.00002)
        );

        // ── CAMERA ────────────────────────────────────────────────────────────
        camera
                .setLocation(new Point(0, 80, 400))
                .setDirection(new Point(0, -20, -100), Vector.AXIS_Y)
                .setVpDistance(400)
                .setVpSize(300, 200)
                .setResolution(800, 533)
                .build()
                .renderImage()
                .writeToImage("crystalStage111");
    }
}