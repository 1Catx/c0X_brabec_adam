package raster;

import java.util.Optional;

public class DepthBuffer implements Raster<Double> {
    private final double[][] buffer;
    private final int width;
    private final int height;

    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new double[width][height];
        clear(); // nastavíme default hned při vytvoření
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public void setValue(int x, int y, Double value) {
        if (!isInBounds(x, y) || value == null) return;
        buffer[x][y] = value;
    }

    @Override
    public Optional<Double> getValue(int x, int y) {
        if (!isInBounds(x, y)) return Optional.empty();
        return Optional.of(buffer[x][y]);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                buffer[x][y] = Double.POSITIVE_INFINITY; // "zatím nic nezapsáno" aka vše je blíž než tohle
            }
        }
    }
}
