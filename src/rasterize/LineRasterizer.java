package rasterize;

import transforms.Col;
import model.Line;
import raster.RasterBufferedImage;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public abstract void rasterize(int x1, int y1, int x2, int y2, Col color);


    public void rasterize(int x1, int y1, int x2, int y2) {
        rasterize(x1, y1, x2, y2, new Col(1, 1, 1));
    }
}
