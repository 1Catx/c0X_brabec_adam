package solid;

import transforms.Col;
import transforms.Point3D;

public class Sphere extends Solid {

    public Sphere(double radius, int stacks, int slices) {
        color = new Col(0xaaff55);

        if (stacks < 2) stacks = 2;
        if (slices < 3) slices = 3;

        for (int j = 0; j <= stacks; j++) {
            double theta = Math.PI * j / stacks;
            double y = radius * Math.cos(theta);
            double r = radius * Math.sin(theta);

            for (int i = 0; i < slices; i++) {
                double phi = 2.0 * Math.PI * i / slices;
                double x = r * Math.cos(phi);
                double z = r * Math.sin(phi);
                vb.add(new Point3D(x, y, z));
            }
        }

        for (int j = 0; j <= stacks; j++) {
            for (int i = 0; i < slices; i++) {
                int curr = index(j, i, slices);

                int nextI = index(j, (i + 1) % slices, slices);
                addEdge(curr, nextI);

                if (j < stacks) {
                    int nextJ = index(j + 1, i, slices);
                    addEdge(curr, nextJ);
                }
            }
        }
    }

    private int index(int j, int i, int slices) {
        return j * slices + i;
    }

}
