package solid;

import transforms.Col;
import transforms.Point3D;

public class ArrowZ extends Solid {
	public ArrowZ() {
        color = new Col(0x0000ff);

        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 0.8));
        vb.add(new Point3D(-0.2, 0, 0.8));
        vb.add(new Point3D(0, 0, 1));
        vb.add(new Point3D(0.2, 0, 0.8));

        ib.add(0);
        ib.add(1);

        ib.add(2);
        ib.add(3);

        ib.add(4);
        ib.add(2);

        ib.add(3);
        ib.add(4); 
    }

}
