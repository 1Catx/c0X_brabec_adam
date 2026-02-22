package solid;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Tetrahedron extends Solid {

    public Tetrahedron(double size) {
        color = new Col(0x00ccff);

        double s = size / 2.0;

        vb.add(new Point3D(+s, +s, +s));
        vb.add(new Point3D(-s, -s, +s));
        vb.add(new Point3D(-s, +s, -s));
        vb.add(new Point3D(+s, -s, -s));

        vtb.add(new Vec2D(0.5, 1.0));
        vtb.add(new Vec2D(0.0, 0.0));
        vtb.add(new Vec2D(1.0, 0.0));
        vtb.add(new Vec2D(0.5, 0.5));

        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 3);
        addEdge(1, 2);
        addEdge(1, 3);
        addEdge(2, 3);

        addTriangle(0, 1, 2);
        addTriangle(0, 1, 3);
        addTriangle(0, 2, 3);
        addTriangle(1, 2, 3);

    }
}
