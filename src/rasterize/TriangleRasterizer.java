package rasterize;

import renderer.VertexOut;
import raster.ZBuffer;
import transforms.Col;

public class TriangleRasterizer {

    private final ZBuffer zbuffer;

    public TriangleRasterizer(ZBuffer zbuffer) {
        this.zbuffer = zbuffer;
    }

    private double edge(double ax, double ay, double bx, double by, double px, double py) {
        return (px - ax) * (by - ay) - (py - ay) * (bx - ax);
    }

    public void rasterize(VertexOut a, VertexOut b, VertexOut c, Col color) {
        int minX = (int) Math.floor(Math.min(a.x, Math.min(b.x, c.x)));
        int maxX = (int) Math.ceil (Math.max(a.x, Math.max(b.x, c.x)));
        int minY = (int) Math.floor(Math.min(a.y, Math.min(b.y, c.y)));
        int maxY = (int) Math.ceil (Math.max(a.y, Math.max(b.y, c.y)));

        double area = edge(a.x, a.y, b.x, b.y, c.x, c.y);
        if (area == 0) return;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double px = x + 0.5;
                double py = y + 0.5;

                double w0 = edge(b.x, b.y, c.x, c.y, px, py);
                double w1 = edge(c.x, c.y, a.x, a.y, px, py);
                double w2 = edge(a.x, a.y, b.x, b.y, px, py);

                if ((area > 0 && (w0 < 0 || w1 < 0 || w2 < 0)) ||
                    (area < 0 && (w0 > 0 || w1 > 0 || w2 > 0))) {
                    continue;
                }

                double alpha = w0 / area;
                double beta  = w1 / area;
                double gamma = w2 / area;

                double z = alpha * a.z + beta * b.z + gamma * c.z;

                zbuffer.setPixelWithZTest(x, y, z, color);
            }
        }
    }
}
