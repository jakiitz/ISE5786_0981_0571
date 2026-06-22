'''
# Java 3D Ray Tracer

A powerful and efficient Java-based 3D Ray Tracing engine. Built with a focus on performance, extensible architecture (SOLID principles), and rendering high-quality images.

**Authors:** Aharon Sadoun & Yair Shushan

## Overview
This engine supports advanced rendering techniques to generate realistic 3D scenes:
* **Bounding Volume Hierarchy (BVH):** Significantly accelerates rendering times (from O(N) to O(log N)) by organizing finite geometries into a binary tree of Axis-Aligned Bounding Boxes (AABB).
* **Soft Shadows (Distributed Ray Tracing):** Simulates area lights using a sampling grid to create realistic, blurred shadow edges (penumbra).
* **Multi-threading:** Fully utilizes multi-core CPUs by parallelizing pixel calculations across threads.
* **Core Geometries:** Sphere, Plane, Triangle, Polygon, Cylinder, and Tube.
* **Lighting Models:** Ambient, Directional, Point, and Spot lights with Phong reflection model.

## Performance Benchmarks
* **Resolution:** 4500x3000
* **Soft Shadows:** 9x9 Grid
* **Threads:** All available cores (`setMultithreading(-1)`)

| Configuration | Render Time | Performance Gain |
| :--- | :--- | :--- |
| Base Engine (No BVH) | ~120 minutes | - |
| **With BVH Optimization** | **~7.5 minutes** | **~16x Faster** |

## Quick Start
```java
import geometries.impl.*;
import lighting.*;
import primitives.*;
import renderer.*;
import scene.Scene;

public class Main {
    public static void main(String[] args) {
        Scene scene = new Scene("Main Scene");

        ImageWriter writer = new ImageWriter("Render_4K", 4500, 3000);

        Camera camera = Camera.getBuilder()
            .setLocation(new Point(0, 0, 1000))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(200, 150)
            .setVpDistance(1000)
            .setImageWriter(writer)
            .setRayTracer(new SimpleRayTracer(scene))
            .setMultithreading(-1) 
            .setGridSize(9)        
            .build();

        camera.renderImage().writeToImage();
    }
}

## Project Structure
| Directory | Description |
| :--- | :--- |
| `src/geometries/` | Intersectable shapes and BVH implementation. |
| `src/lighting/` | Light sources and illumination models. |
| `src/primitives/` | Core mathematical constructs. |
| `src/renderer/` | Camera, RayTracer pipeline, ImageWriter. |
| `src/scene/` | Scene definition holding geometries and lighting. |
| `unittests/` | Comprehensive JUnit test suite. |



## Gallery

Here is a showcase of scenes rendered using our engine, demonstrating various lighting models, reflections, and complex geometries:

### Final 4K Render (Showcasing Soft Shadows & BVH)
<img width="1500" height="1000" alt="buildScene" src="https://github.com/user-attachments/assets/7ac17914-62de-460e-a695-cc54b2b0fe32" />

'''
