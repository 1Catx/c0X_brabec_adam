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
addTriangle(0, 1, 2);
addTriangle(0, 1, 3);
addTriangle(0, 2, 3);
addTriangle(1, 2, 3);

    }
}
