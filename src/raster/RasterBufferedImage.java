package raster;

import transforms.Col;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class RasterBufferedImage implements Raster<Col> {

    private final BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public RasterBufferedImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void setValue(int x, int y, Col value) {
        if (!isInBounds(x, y) || value == null) return;
        image.setRGB(x, y, value.getRGB());
    }

    @Override
    public Optional<Col> getValue(int x, int y) {
        if (!isInBounds(x, y)) return Optional.empty();
        return Optional.of(new Col(image.getRGB(x, y)));
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
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                image.setRGB(x, y, 0x000000);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}
