package solid;

import transforms.Col;
import transforms.Point3D;

public class Prism extends Solid {

    public Prism(int n, double radius, double height) {
        color = new Col(0xaa00ff);

        double h = height / 2.0;

        // VRCHOLY
        // spodní 0..n-1   (z = -h)
        // horní  n..2n-1  (z = +h)
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);

            vb.add(new Point3D(x, y, -h));  // spodní
            vb.add(new Point3D(x, y, +h));  // horní
        }

        // HRANY

        // spodní polygon
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            addEdge(bottom(i), bottom(next));
        }

        // horní polygon
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            addEdge(top(i), top(next));
        }

        // svislé hrany
        for (int i = 0; i < n; i++) {
            addEdge(bottom(i), top(i));
        }
    }

    private int bottom(int i) {
        return i * 2;
    }

    private int top(int i) {
        return i * 2 + 1;
    }


}
