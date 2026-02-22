package texture;

import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Texture {
    private final BufferedImage img;
    private final int w, h;

    public Texture(BufferedImage img) {
        this.img = img;
        this.w = img.getWidth();
        this.h = img.getHeight();
    }

    public static Texture fromResource(String path) {
        try (InputStream is = Texture.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found on classpath: " + path);
            }
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new IllegalArgumentException("ImageIO.read could not decode image: " + path + " (unsupported format or corrupted file)");
            }
            return new Texture(img);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image: " + path, e);
        }
    }

    public static Texture fromFile(String path) {
    try {
        BufferedImage img = ImageIO.read(new java.io.File(path));
        if (img == null) throw new IllegalArgumentException("Cannot decode image file: " + path);
        return new Texture(img);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

    public Col sample(double u, double v) {
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        int x = (int) Math.floor(u * (w - 1));
        int y = (int) Math.floor((1.0 - v) * (h - 1));

        int argb = img.getRGB(x, y);
        return new Col(argb);
    }
}