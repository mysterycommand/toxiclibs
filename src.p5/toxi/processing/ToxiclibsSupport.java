package toxi.processing;

import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.AABB;
import toxi.geom.AxisAlignedCylinder;
import toxi.geom.Cone;
import toxi.geom.Ellipse;
import toxi.geom.Line2D;
import toxi.geom.Line3D;
import toxi.geom.Plane;
import toxi.geom.Ray2D;
import toxi.geom.Ray3D;
import toxi.geom.Rect;
import toxi.geom.Sphere;
import toxi.geom.Triangle;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

/**
 * In addition to providing new drawing commands, this class provides wrappers
 * for using datatypes of the toxiclibs core package directly with Processing's
 * drawing commands. The class can be configured to work with any PGraphics
 * instance (incl. offscreen buffers).
 */
public class ToxiclibsSupport {

    protected static final Logger logger =
            Logger.getLogger(ToxiclibsSupport.class.getName());

    protected PApplet app;
    protected PGraphics gfx;

    public ToxiclibsSupport(PApplet app) {
        this(app, app.g);
    }

    public ToxiclibsSupport(PApplet app, PGraphics gfx) {
        this.app = app;
        this.gfx = gfx;
    }

    public final void box(AABB box) {
        mesh(box.toMesh(), false, 0);
    }

    public final void box(AABB box, boolean smooth) {
        mesh(box.toMesh(), smooth, 0);
    }

    public final void cone(Cone cone) {
        mesh(cone.toMesh(6), false, 0);
    }

    public final void cone(Cone cone, int res, boolean smooth) {
        mesh(cone.toMesh(res), false, 0);
    }

    public final void cylinder(AxisAlignedCylinder cylinder) {
        mesh(cylinder.toMesh(), false, 0);
    }

    public final void cylinder(AxisAlignedCylinder cylinder, int res,
            boolean smooth) {
        mesh(cylinder.toMesh(res, 0), smooth, 0);
    }

    public final void ellipse(Ellipse e) {
        Vec2D r = e.getRadii();
        switch (gfx.ellipseMode) {
            case PConstants.CENTER:
                gfx.ellipse(e.x, e.y, r.x * 2, r.y * 2);
                break;
            case PConstants.RADIUS:
                gfx.ellipse(e.x, e.y, r.x, r.y);
                break;
            case PConstants.CORNER:
            case PConstants.CORNERS:
                gfx.ellipse(e.x - r.x, e.y - r.y, r.x * 2, r.y * 2);
                break;
            default:
                logger.warning("invalid ellipse mode: " + gfx.ellipseMode);
        }
    }

    /**
     * @return the gfx
     */
    public PGraphics getGraphics() {
        return gfx;
    }

    public final void line(Line2D line) {
        gfx.line(line.a.x, line.a.y, line.b.x, line.b.y);
    }

    public final void line(Line3D line) {
        gfx.line(line.a.x, line.a.y, line.a.z, line.b.x, line.b.y, line.b.z);
    }

    public final void mesh(TriangleMesh mesh) {
        mesh(mesh, false, 0);
    }

    public final void mesh(TriangleMesh mesh, boolean smooth) {
        mesh(mesh, smooth, 0);
    }

    public void mesh(TriangleMesh mesh, boolean smooth, float normalLength) {
        gfx.beginShape(PConstants.TRIANGLES);
        if (smooth) {
            for (TriangleMesh.Face f : mesh.faces) {
                gfx.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
                gfx.vertex(f.a.x, f.a.y, f.a.z);
                gfx.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
                gfx.vertex(f.b.x, f.b.y, f.b.z);
                gfx.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
                gfx.vertex(f.c.x, f.c.y, f.c.z);
            }
        } else {
            for (TriangleMesh.Face f : mesh.faces) {
                gfx.normal(f.normal.x, f.normal.y, f.normal.z);
                gfx.vertex(f.a.x, f.a.y, f.a.z);
                gfx.vertex(f.b.x, f.b.y, f.b.z);
                gfx.vertex(f.c.x, f.c.y, f.c.z);
            }
        }
        gfx.endShape();
        if (normalLength > 0) {
            if (smooth) {
                for (TriangleMesh.Vertex v : mesh.vertices.values()) {
                    Vec3D w = v.add(v.normal.scale(normalLength));
                    Vec3D n = v.normal.scale(127);
                    gfx.stroke(n.x + 128, n.y + 128, n.z + 128);
                    gfx.line(v.x, v.y, v.z, w.x, w.y, w.z);
                }
            } else {
                for (TriangleMesh.Face f : mesh.faces) {
                    Vec3D c = f.a.add(f.b).addSelf(f.c).scaleSelf(1f / 3);
                    Vec3D d = c.add(f.normal.scale(20));
                    Vec3D n = f.normal.scale(127);
                    gfx.stroke(n.x + 128, n.y + 128, n.z + 128);
                    gfx.line(c.x, c.y, c.z, d.x, d.y, d.z);
                }
            }
        }
    }

    public final void plane(Plane plane, float size) {
        mesh(plane.toMesh(size), false, 0);
    }

    public final void ray(Ray2D ray, float length) {
        Vec2D e = ray.getPointAtDistance(length);
        gfx.line(ray.x, ray.y, e.x, e.y);
    }

    public final void ray(Ray3D ray, float length) {
        Vec3D e = ray.getPointAtDistance(length);
        gfx.line(ray.x, ray.y, ray.z, e.x, e.y, e.z);
    }

    public final void rect(Rect r) {
        switch (gfx.rectMode) {
            case PConstants.CORNER:
                gfx.rect(r.x, r.y, r.width, r.height);
                break;
            case PConstants.CORNERS:
                gfx.rect(r.x, r.y, r.x + r.width, r.y + r.height);
                break;
            case PConstants.CENTER:
                gfx.rect(r.x + r.width * 0.5f, r.y + r.height * 0.5f, r.width,
                        r.height);
                break;
            case PConstants.RADIUS:
                float rw = r.width * 0.5f;
                float rh = r.height * 0.5f;
                gfx.rect(r.x + rw, r.y + rh, rw, rh);
                break;
            default:
                logger.warning("invalid rect mode: " + gfx.rectMode);
        }
    }

    /**
     * @param gfx
     *            the gfx to set
     */
    public void setGraphics(PGraphics gfx) {
        this.gfx = gfx;
    }

    // FIXME replace with mesh drawing, blocked by issue #2
    public final void sphere(Sphere sphere) {
        gfx.pushMatrix();
        gfx.translate(sphere.x, sphere.y, sphere.z);
        gfx.sphere(sphere.radius);
        gfx.popMatrix();
    }

    public final void triangle(Triangle tri, boolean isFullShape) {
        if (isFullShape) {
            gfx.beginShape(PConstants.TRIANGLES);
        }
        gfx.vertex(tri.a.x, tri.a.y, tri.a.z);
        gfx.vertex(tri.b.x, tri.b.y, tri.b.z);
        gfx.vertex(tri.c.x, tri.c.y, tri.c.z);
        if (isFullShape) {
            gfx.endShape();
        }
    }

    public final void triangle(Triangle2D tri, boolean isFullShape) {
        if (isFullShape) {
            gfx.beginShape(PConstants.TRIANGLES);
        }
        gfx.vertex(tri.a.x, tri.a.y);
        gfx.vertex(tri.b.x, tri.b.y);
        gfx.vertex(tri.c.x, tri.c.y);
        if (isFullShape) {
            gfx.endShape();
        }
    }

    public final void vertex(Vec2D v) {
        gfx.vertex(v.x, v.y);
    }

    public final void vertex(Vec3D v) {
        gfx.vertex(v.x, v.y);
    }

}
