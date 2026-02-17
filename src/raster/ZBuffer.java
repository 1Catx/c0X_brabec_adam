package raster;

import transforms.Col;

import java.util.Optional;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void clear() {
        imageBuffer.clear();
        depthBuffer.clear();
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if (color == null) return;

        // mimo obrazovku nic nekreslit
        if (x < 0 || x >= imageBuffer.getWidth() || y < 0 || y >= imageBuffer.getHeight()) return;

        Optional<Double> currentOpt = depthBuffer.getValue(x, y);
        if (currentOpt.isEmpty()) return;

        double currentZ = currentOpt.get();

        // Když je nový pixel blíž, přepíšeme
        if (z < currentZ) {
            depthBuffer.setValue(x, y, z);
            imageBuffer.setValue(x, y, color);
        }
    }

    public Raster<Double> getDepthBuffer() {
        return depthBuffer;
    }
}
