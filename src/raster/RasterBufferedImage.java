package raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {

    private BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        int w = getWidth();
        int h = getHeight();

        if(x < 0 || x >= w) return;
        if(y < 0 || y >= h) return;
        
        image.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        int w = getWidth();
        int h = getHeight();

        if (x < 0 || x >= w) return 0;
        if (y < 0 || y >= h) return 0;

        return image.getRGB(x, y);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, getWidth(), getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}