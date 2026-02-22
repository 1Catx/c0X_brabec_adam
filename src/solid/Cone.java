package solid;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Cone extends Solid {

    public Cone(double radius, double height, int segments) {
        color = new Col(0xff55aa);

        if (segments < 3) segments = 3;

        int apexIndex = vb.size();
        vb.add(new Point3D(0, height, 0));
        vtb.add(new Vec2D(0.5, 1.0));

        int baseStart = vb.size();
        for (int i = 0; i < segments; i++) {
            double a = 2.0 * Math.PI * i / segments;
            double x = radius * Math.cos(a);
            double z = radius * Math.sin(a);
            vb.add(new Point3D(x, 0, z));

            double u = i / (double) segments;
            double v = 0.0;
            vtb.add(new Vec2D(u, v));
        }

        // === TRIANGLES plášť kuželu ===
        //center základny
        int centerIndex = vb.size();
        vb.add(new Point3D(0, 0, 0));
        vtb.add(new Vec2D(0.5, 0.5));

        for (int i = 0; i < segments; i++) {
            int curr = baseStart + i;
            int next = baseStart + ((i + 1) % segments);

            //hrany
            addEdge(curr, next);
            addEdge(apexIndex, curr);

            //plášť
            addTriangle(apexIndex, curr, next);

            //podstava; debugging = pokud postava zmízí prohodit curr/next
            addTriangle(centerIndex, next, curr);
        }
    }


}
