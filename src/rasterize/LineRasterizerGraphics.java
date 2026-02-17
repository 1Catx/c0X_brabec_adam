package rasterize;

import raster.RasterBufferedImage;
import transforms.Col;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer {

    private Color currentColor = Color.RED;

    public LineRasterizerGraphics(RasterBufferedImage raster) {
        super(raster);
    }

    public void setColor(Col col) {
        this.currentColor = new Color(col.getRGB());
    }

    //Když chceme použít currentColor, tedy bez zadání barvy v args (poté zavolá Bresenhama)
    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        Col col = new Col(currentColor.getRGB());
        rasterize(x1, y1, x2, y2, col);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Col color) {

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < raster.getWidth() && y1 >= 0 && y1 < raster.getHeight()) {
                raster.setValue(x1, y1, color);
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

}
