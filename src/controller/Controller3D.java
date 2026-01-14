package controller;

import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import renderer.Renderer;
import solid.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Mat4Transl;
import transforms.Point3D;
import transforms.Vec3D;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller3D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Renderer renderer;

    private Solid arrow = new Arrow();
    private Solid axisX = new AxisX();
    private Solid axisY = new AxisY();
    private Solid axisZ = new AxisZ();

    private Solid cube = new Cube(1.0);
    private Solid prism = new Prism(10, 0.5, 1.0);

    // 4 body pro křivku
    Point3D p0 = cube.getVb().get(0);
    Point3D p3 = cube.getVb().get(6);
    Point3D p1 = new Point3D(0, 1, 1.5);
    Point3D p2 = new Point3D(-1, 0.5, 0.2);

    BezierCurve curve = new BezierCurve(p0, p1, p2, p3);

    private int lastX, lastY;
    private boolean mouseDown = false;

    private final double MOVE_SPEED = 0.1;     // krok WSAD
    private final double ROTATE_SPEED = 0.005; // citlivost myši

    private Camera camera;
    private Mat4 proj;

    public Controller3D(Panel panel) {
        this.panel = panel;

        this.panel.setFocusable(true);
        this.panel.requestFocusInWindow();


        lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

        camera = new Camera()
                .withPosition(new Vec3D(0.5, -1.5, 1.5))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);

        proj = new Mat4PerspRH(
                Math.toRadians(90),
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                0.1,
                100
        );

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                proj
        );

        cube.setModel(new Mat4Transl(0.7, 0.5, 0)); // posun krychle
        prism.setModel(new Mat4Transl(-1.0, 0, 0)); //posun prismu

        curve = new BezierCurve(p0, p1, p2, p3);
        curve.setModel(cube.getModel());

        initListeners();

        drawScene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDown = false;
            }
        });

        // dragování měníme azimut/zenit
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!mouseDown) return;

                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                camera = camera
                        .addAzimuth(dx * ROTATE_SPEED)
                        .addZenith(-dy * ROTATE_SPEED);

                lastX = e.getX();
                lastY = e.getY();

                drawScene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> camera = camera.forward(MOVE_SPEED);
                    case KeyEvent.VK_S -> camera = camera.backward(MOVE_SPEED);
                    case KeyEvent.VK_A -> camera = camera.left(MOVE_SPEED);
                    case KeyEvent.VK_D -> camera = camera.right(MOVE_SPEED);
                    default -> { return; }
                }
                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        renderer.setView(camera.getViewMatrix());
        renderer.setProj(proj);

        renderer.renderSolid(axisX);
        renderer.renderSolid(axisY);
        renderer.renderSolid(axisZ);

        renderer.renderSolid(arrow);

        renderer.renderSolid(cube);
        renderer.renderSolid(prism);

        renderer.renderSolid(curve);

        panel.repaint();
    }
}
