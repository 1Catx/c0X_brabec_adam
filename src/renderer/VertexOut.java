package renderer;

public class VertexOut {
    public final double x, y, z;
    public final double invW;
    public final double uOverW, vOverW;

    public VertexOut(double x, double y, double z, double invW, double uOverW, double vOverW) {
        this.x = x; 
        this.y = y; 
        this.z = z;
        this.invW = invW;
        this.uOverW = uOverW;
        this.vOverW = vOverW;
    }
}