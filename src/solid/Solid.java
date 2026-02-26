package solid;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec2D;
import texture.Texture;
import java.util.ArrayList;
import java.util.List;

public abstract class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Vec2D> vtb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>(); //hrany (2)
    protected List<Integer> ibTriangles = new ArrayList<>(); //trojúhelníky (3)
    protected Col color = new Col(0xffffff);
    protected Mat4 model = new Mat4Identity();
    protected Texture texture;
    private boolean textureEnabled = false;
    private boolean emissive = false;




    public List<Point3D> getVb() {
        return vb;
    }

    public List<Vec2D> getVtb() { 
        return vtb; 
    }

    public List<Integer> getIb() {
        return ib;
    }

    public Col getColor() {
        return color;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void addEdge(int a, int b) {
        ib.add(a);
        ib.add(b);
    }

    public void addTriangle(int a, int b, int c) {
        ibTriangles.add(a);
        ibTriangles.add(b);
        ibTriangles.add(c);
    }

    public List<Integer> getIbTriangles() {
        return ibTriangles;
    }

    public void setColor(Col c) {
        this.color = c;
    }

    public void setTexture(Texture t) { 
        this.texture = t;
    }

    public Texture getTexture() { 
        return texture; 
    }

    public boolean isTextureEnabled() {
        return textureEnabled;
    }

    public void toggleTexture() {
        textureEnabled = !textureEnabled;
    }

    public boolean isEmissive() { 
        return emissive; 
    
    }
    
    public void setEmissive(boolean emissive) { 
        this.emissive = emissive; 
    }
}
