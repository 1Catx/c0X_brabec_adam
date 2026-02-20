package solid;

import transforms.Col;
import transforms.Point3D;

public class ArrowX extends Solid {
	public ArrowX() {
        color = new Col(0xff0000);

        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0.8, 0, 0));
        vb.add(new Point3D(0.8, 0, -0.2));
        vb.add(new Point3D(1, 0, 0));
        vb.add(new Point3D(0.8, 0, 0.2));

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
