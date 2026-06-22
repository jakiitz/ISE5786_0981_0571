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

## Installation & Setup
1. **Prerequisites:** Java 11 or higher, Maven.
2. **Clone the repository:**
   ```bash
   git clone [https://github.com/](https://github.com/)[Your-Username]/ISE5786_0981_0571.git
   cd ISE5786_0981_0571