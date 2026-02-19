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

        // === wire index buffer ===
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

        // === triangle index buffer ===
        // spodní stěna
        addTriangle(0, 2, 1);
        addTriangle(0, 3, 2);

        // horní stěna
        addTriangle(4, 5, 6);
        addTriangle(4, 6, 7);

        // přední stěna
        addTriangle(0, 1, 5);
        addTriangle(0, 5, 4);

        // zadní stěna
        addTriangle(3, 6, 2);
        addTriangle(3, 7, 6);

        // levá stěna
        addTriangle(0, 7, 3);
        addTriangle(0, 4, 7);

        // pravá stěna
        addTriangle(1, 2, 6);
        addTriangle(1, 6, 5);

    }

}
