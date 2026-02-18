package solid;

import transforms.Col;
import transforms.Point3D;

public class Cube extends Solid {

    public Cube(double size) {
        color = new Col(0xffaa00);

        double s = size / 2.0;

        // vrcholy (vertex buffer)
        // spodní čtverec (z = -s)
        vb.add(new Point3D(-s, -s, -s));
        vb.add(new Point3D(+s, -s, -s));
        vb.add(new Point3D(+s, +s, -s));
        vb.add(new Point3D(-s, +s, -s));

        // horní čtverec (z = +s)
        vb.add(new Point3D(-s, -s, +s));
        vb.add(new Point3D(+s, -s, +s));
        vb.add(new Point3D(+s, +s, +s));
        vb.add(new Point3D(-s, +s, +s));

        // hrany (index buffer)
        // spodní čtverec
        addEdge(0, 1);
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(3, 0);

        // horní čtverec
        addEdge(4, 5);
        addEdge(5, 6);
        addEdge(6, 7);
        addEdge(7, 4);

        // svislé hrany
        addEdge(0, 4);
        addEdge(1, 5);
        addEdge(2, 6);
        addEdge(3, 7);
    }

}
