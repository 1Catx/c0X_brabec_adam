package renderer;

import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

public class Renderer {
    
    private LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private final ZBuffer zbuffer;
    private int width, heigth;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, ZBuffer zbuffer, int width, int heigth, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.heigth = heigth;
        this.view = view;
        this.proj = proj;
        this.zbuffer = zbuffer;
        this.triangleRasterizer = new TriangleRasterizer(zbuffer);
    }

    public void renderSolid(Solid solid) {
        if (lineRasterizer instanceof LineRasterizerGraphics lg) { 
            lg.setColor(solid.getColor()); // jinak by všechno bylo v jedné barvě
        }

        for (int i = 0; i < solid.getIbTriangles().size() - 2; i += 3) {
            int indexA = solid.getIbTriangles().get(i);
            int indexB = solid.getIbTriangles().get(i + 1);
            int indexC = solid.getIbTriangles().get(i + 2);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);
            Point3D pointC = solid.getVb().get(indexC);

            // MODEL: object -> world
            pointA = pointA.mul(solid.getModel());
            pointB = pointB.mul(solid.getModel());
            pointC = pointC.mul(solid.getModel());

            // VIEW: world -> camera
            pointA = pointA.mul(view);
            pointB = pointB.mul(view);
            pointC = pointC.mul(view);

            // hrany s bodem za kamerou (w <= 0) nekreslíme
            if (pointA.getW() <= 0 || pointB.getW() <= 0 || pointC.getW() <= 0) continue;

            // PROJECTION: camera -> clip space
            pointA = pointA.mul(proj);
            pointB = pointB.mul(proj);
            pointC = pointC.mul(proj);

            // DEHOMOGENIZACE: clip -> NDC
            double wA = pointA.getW();
            double wB = pointB.getW();
            double wC = pointC.getW();

            // OŘEZÁNÍ: když je w skoro nulové, radši nekreslíme
            if (Math.abs(wA) < 1e-6 || Math.abs(wB) < 1e-6 || Math.abs(wC) < 1e-6) continue;

            // DEHOMOGENIZACE: transformace z clip space do NDC (dělení souřadnic w)
            pointA = pointA.mul(1.0 / wA);
            pointB = pointB.mul(1.0 / wB);
            pointC = pointC.mul(1.0 / wC);
/* 
            //RYCHLÉ OŘEZÁNÍ PODLE Z
            if (pointA.getZ() < 0 || pointA.getZ() > 1) continue;
            if (pointB.getZ() < 1 || pointB.getZ() > 1) continue;
            if (pointC.getZ() < 1 || pointC.getZ() > 1) continue;
*/

            // TRANSFORMACE: do okna obrazovky, NDC -> screen space
            Vec3D vecA = transformToWindow(pointA);
            Vec3D vecB = transformToWindow(pointB);
            Vec3D vecC = transformToWindow(pointC);

            VertexOut sa = new VertexOut(vecA.getX(), vecA.getY(), pointA.getZ());
            VertexOut sb = new VertexOut(vecB.getX(), vecB.getY(), pointB.getZ());
            VertexOut sc = new VertexOut(vecC.getX(), vecC.getY(), pointC.getZ());

            triangleRasterizer.rasterize(sa, sb, sc, solid.getColor());
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

    private VertexOut transformToScreen(Point3D p, int width, int height) {
        double x = (p.getX() * 0.5 + 0.5) * (width - 1);
        double y = (1.0 - (p.getY() * 0.5 + 0.5)) * (height - 1);
        double z = p.getZ();

        return new VertexOut(x, y, z);
    }

}
