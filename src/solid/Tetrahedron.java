package solid;

import transforms.Col;
import transforms.Point3D;

public class Tetrahedron extends Solid {

    public Tetrahedron(double size) {
        color = new Col(0x00ccff);

        double s = size / 2.0;

        vb.add(new Point3D(+s, +s, +s)); // 0
        vb.add(new Point3D(-s, -s, +s)); // 1
        vb.add(new Point3D(-s, +s, -s)); // 2
        vb.add(new Point3D(+s, -s, -s)); // 3

        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 3);
        addEdge(1, 2);
        addEdge(1, 3);
        addEdge(2, 3);
    }

    private void addEdge(int a, int b) {
        ib.add(a);
        ib.add(b);
    }
}
