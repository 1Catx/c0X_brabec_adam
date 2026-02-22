package renderer;

import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import solid.Solid;
import texture.Texture;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

public class Renderer {
    public enum RenderMode { WIREFRAME, FILLED }
    private RenderMode mode = RenderMode.WIREFRAME;
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

    /*=== switcher === */
    public void renderSolid(Solid solid) {
        switch (mode) {
            case WIREFRAME -> renderWireframe(solid);
            case FILLED -> renderFilled(solid);
        }
    }

    /*=== wire === */
    private void renderWireframe(Solid solid) {
        if (lineRasterizer instanceof LineRasterizerGraphics lg) {
            lg.setColor(solid.getColor());
        }

        for (int i = 0; i < solid.getIb().size() - 1; i += 2) {
            int ia = solid.getIb().get(i);
            int ib = solid.getIb().get(i + 1);

            Point3D a = solid.getVb().get(ia);
            Point3D b = solid.getVb().get(ib);

            Point3D ndcA = toNdc(a, solid);
            Point3D ndcB = toNdc(b, solid);
            if (ndcA == null || ndcB == null) continue;

            //pro debug vypnout
            if (!isZInRange01(ndcA) || !isZInRange01(ndcB)) continue;

            Vec3D sa = transformToWindow(ndcA);
            Vec3D sb = transformToWindow(ndcB);

            lineRasterizer.rasterize(
                    (int) Math.round(sa.getX()),
                    (int) Math.round(sa.getY()),
                    (int) Math.round(sb.getX()),
                    (int) Math.round(sb.getY())
            );
        }
    }

    /*=== filled ===*/
    private void renderFilled(Solid solid) {
        if (solid.getIbTriangles().isEmpty()) return;

        for (int i = 0; i < solid.getIbTriangles().size() - 2; i += 3) {
            int ia = solid.getIbTriangles().get(i);
            int ib = solid.getIbTriangles().get(i + 1);
            int ic = solid.getIbTriangles().get(i + 2);

            Point3D pa = solid.getVb().get(ia);
            Point3D pb = solid.getVb().get(ib);
            Point3D pc = solid.getVb().get(ic);

            // UV z vtb (musí existovat a mít stejnou délku jako vb)
            var uva = solid.getVtb().get(ia);
            var uvb = solid.getVtb().get(ib);
            var uvc = solid.getVtb().get(ic);

            // clip space (ještě nedělit w)
            Point3D ca = toClip(pa, solid);
            Point3D cb = toClip(pb, solid);
            Point3D cc = toClip(pc, solid);
            if (ca == null || cb == null || cc == null) continue;

            double invWA = 1.0 / ca.getW();
            double invWB = 1.0 / cb.getW();
            double invWC = 1.0 / cc.getW();

            // NDC
            Point3D ndcA = ca.mul(invWA);
            Point3D ndcB = cb.mul(invWB);
            Point3D ndcC = cc.mul(invWC);

            // pro debug vypnout
            if (!isZInRange01(ndcA) || !isZInRange01(ndcB) || !isZInRange01(ndcC)) continue;

            Vec3D sa2 = transformToWindow(ndcA);
            Vec3D sb2 = transformToWindow(ndcB);
            Vec3D sc2 = transformToWindow(ndcC);

            VertexOut sa = new VertexOut(sa2.getX(), sa2.getY(), ndcA.getZ(),
                    invWA, uva.getX() * invWA, uva.getY() * invWA);

            VertexOut sb = new VertexOut(sb2.getX(), sb2.getY(), ndcB.getZ(),
                    invWB, uvb.getX() * invWB, uvb.getY() * invWB);

            VertexOut sc = new VertexOut(sc2.getX(), sc2.getY(), ndcC.getZ(),
                    invWC, uvc.getX() * invWC, uvc.getY() * invWC);

            Texture tex = solid.isTextureEnabled() ? solid.getTexture() : null;
            triangleRasterizer.rasterize(sa, sb, sc, solid.getColor(), tex);
        }
    }

    /*=== help === */
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

    public void setMode(RenderMode mode) { 
        this.mode = mode; 
    }

    public RenderMode getMode() { 
        return mode; 
    }    

    private Point3D toNdc(Point3D p, Solid solid) {
        // MODEL
        Point3D out = p.mul(solid.getModel());

        // VIEW
        out = out.mul(view);

        // za kamerou
        if (out.getW() <= 0) return null;

        // PROJ
        out = out.mul(proj);

        double w = out.getW();
        if (Math.abs(w) < 1e-6) return null;

        // NDC
        return out.mul(1.0 / w);
    }

    private boolean isZInRange01(Point3D pNdc) {
        double z = pNdc.getZ();
        return z >= 0 && z <= 1;
    }

    private Point3D toClip(Point3D p, Solid solid) {
        Point3D out = p.mul(solid.getModel());
        out = out.mul(view);

        if (out.getW() <= 0) return null;

        out = out.mul(proj);
        double w = out.getW();
        if (Math.abs(w) < 1e-6) return null;

        return out;
    }
}
