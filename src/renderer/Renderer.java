package renderer;

import rasterize.LineRasterizerGraphics;

import rasterize.LineRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

public class Renderer {
    
    private LineRasterizer lineRasterizer;
    private int width, heigth;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, int width, int heigth, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.heigth = heigth;
        this.view = view;
        this.proj = proj;
    }

    public void renderSolid(Solid solid) {
    if (lineRasterizer instanceof LineRasterizerGraphics lg) { 
        lg.setColor(solid.getColor()); // jinak by všechno bylo v jedné barvě
    }

    for (int i = 0; i < solid.getIb().size() - 1; i += 2) {
        int indexA = solid.getIb().get(i);
        int indexB = solid.getIb().get(i + 1);

        Point3D pointA = solid.getVb().get(indexA);
        Point3D pointB = solid.getVb().get(indexB);

        // MODEL: object -> world
        pointA = pointA.mul(solid.getModel());
        pointB = pointB.mul(solid.getModel());

        // VIEW: world -> camera
        pointA = pointA.mul(view);
        pointB = pointB.mul(view);

        // hrany s bodem za kamerou (w <= 0) nekreslíme
        if (pointA.getW() <= 0 || pointB.getW() <= 0) {
            continue;
        }

        // PROJECTION: camera -> clip space
        pointA = pointA.mul(proj);
        pointB = pointB.mul(proj);

        // DEHOMOGENIZACE: clip -> NDC
        double wA = pointA.getW();
        double wB = pointB.getW();

        // OŘEZÁNÍ: když je w skoro nulové, hranu radši nekreslíme
        if (Math.abs(wA) < 1e-6 || Math.abs(wB) < 1e-6) {
            continue;
        }

        // DEHOMOGENIZACE: transformace z clip space do NDC (dělení souřadnic w)
        pointA = pointA.mul(1.0 / wA);
        pointB = pointB.mul(1.0 / wB);

        //RYCHLÉ OŘEZÁNÍ PODLE Z
        if (pointA.getZ() < 0 || pointB.getZ() < 0) continue; //near plane
        if (pointA.getZ() > 1 || pointB.getZ() > 1) continue; //far plane

        // TRANSFORMACE: do okna obrazovky, NDC -> screen space
        Vec3D vecA = transformToWindow(pointA);
        Vec3D vecB = transformToWindow(pointB);

        lineRasterizer.rasterize(
                (int) Math.round(vecA.getX()),
                (int) Math.round(vecA.getY()),
                (int) Math.round(vecB.getX()),
                (int) Math.round(vecB.getY())
        );
    }
}

    private Vec3D transformToWindow(Point3D p) {
        return new Vec3D(p).mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2., (heigth - 1) / 2., 1));
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
