package controller;

import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import renderer.Renderer;
import solid.*;
import view.Panel;
import raster.ZBuffer;
import texture.Texture;

import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Mat4RotX;
import transforms.Mat4RotY;
import transforms.Mat4Transl;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class Controller3D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Renderer renderer;
    private ZBuffer zbuffer;

    //Výběr aktivního tělesa
    private final Solid[] solids;
    private int activeIndex = 0;

    //Objekty
    private Solid arrowX = new ArrowX();
    private Solid arrowY = new ArrowY();
    private Solid arrowZ = new ArrowZ();
    private Solid cube   = new Cube(0.9);
    private Solid sphere = new Sphere(0.6, 16, 24);
    private Solid tetra  = new Tetrahedron(0.9);
    private Solid cone   = new Cone(0.5, 1.0, 24);

    private int lastX, lastY;
    private boolean mouseDown = false;

    private final double MOVE_SPEED = 0.1; // krok WSAD
    private final double ROTATE_SPEED = 0.005; // citlivost myši

    private double fov = Math.toRadians(90); // aktuální FOV
    private final double ZOOM_STEP = Math.toRadians(5); // krok zoomu (5°)

    private Camera camera;
    private Mat4 proj;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.panel.setFocusable(true);
        this.panel.setFocusTraversalKeysEnabled(false);
        this.panel.requestFocusInWindow();

        lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

        zbuffer = new ZBuffer(panel.getRaster());

        camera = new Camera()
                .withPosition(new Vec3D(0.5, -1.5, 1.5))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);

        proj = new Mat4PerspRH(
                fov,
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                0.1,
                100
        );

        renderer = new Renderer(
                lineRasterizer,
                zbuffer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                proj
        );

        solids = new Solid[] {sphere, cube, cone, tetra};

        cube.setModel(new Mat4Transl(1.8, 0.0, 0.0));
        sphere.setModel(new Mat4Transl(1.8, 0.0, 0.0));
        tetra.setModel(new Mat4Transl( 0.5, 0.2, 0.0));
        cone.setModel (new Mat4Transl( 0.0, 0.0,-1.0));

        sphere.setTexture(Texture.fromFile("C:\\Users\\A\\Desktop\\New Z-Buff\\c03_brabec_adam\\src\\texture\\scratchedTexture.png"));
        cube.setTexture(Texture.fromFile("C:\\Users\\A\\Desktop\\New Z-Buff\\c03_brabec_adam\\src\\texture\\pikaSmol.png"));
        cone.setTexture(Texture.fromFile("C:\\Users\\A\\Desktop\\New Z-Buff\\c03_brabec_adam\\src\\texture\\triangleTexture.png"));
        tetra.setTexture(Texture.fromFile("C:\\Users\\A\\Desktop\\New Z-Buff\\c03_brabec_adam\\src\\texture\\geometryTexture.png"));

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
                    case KeyEvent.VK_M -> {
                        renderer.setMode(renderer.getMode() == 
                        Renderer.RenderMode.WIREFRAME ? Renderer.RenderMode.FILLED : Renderer.RenderMode.WIREFRAME);
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_TAB -> {
                        activeIndex = (activeIndex + 1) % solids.length;
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_LEFT -> {
                        moveActive(-MOVE_SPEED, 0, 0);
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        moveActive(+MOVE_SPEED, 0, 0);
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_UP -> {
                        moveActive(0, 0, +MOVE_SPEED);
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_DOWN -> {
                        moveActive(0, 0, -MOVE_SPEED);
                        drawScene();
                        return;
                    }
                    case KeyEvent.VK_J -> { 
                        rotateActiveY(+0.1); 
                        drawScene(); 
                        return; 
                    }
                    case KeyEvent.VK_L -> { 
                        rotateActiveY(-0.1); 
                        drawScene(); 
                        return; 
                    }
                    case KeyEvent.VK_I -> { 
                        rotateActiveX(+0.1); 
                        drawScene(); 
                        return; 
                    }
                    case KeyEvent.VK_K -> { 
                        rotateActiveX(-0.1); 
                        drawScene(); 
                        return; 
                    }
                    case KeyEvent.VK_T -> {
                        Solid s = solids[activeIndex];
                        s.toggleTexture();
                        drawScene();
                        return;
                    }

                    default -> { return; }
                }
                drawScene();
            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation(); 
                fov += notches * ZOOM_STEP;

                fov = Math.max(Math.toRadians(15), Math.min(Math.toRadians(120), fov));

                updateProjection();
                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();
        zbuffer.clear();

        renderer.setView(camera.getViewMatrix());
        renderer.setProj(proj);

        renderer.renderSolid(arrowX);        
        renderer.renderSolid(arrowY);
        renderer.renderSolid(arrowZ);
        renderer.renderSolid(cube);
        renderer.renderSolid(sphere);
        renderer.renderSolid(tetra);
        renderer.renderSolid(cone);

        panel.repaint();
    }

    private void updateProjection() {
        double aspect = panel.getRaster().getHeight() / (double) panel.getRaster().getWidth();
        proj = new Mat4PerspRH(
                fov,
                aspect,
                0.1,
                100
        );

        renderer.setProj(proj);  // předáme novou matici rendereru
    }

    private void moveActive(double dx, double dy, double dz) {
        Solid s = solids[activeIndex];
        s.setModel(s.getModel().mul(new Mat4Transl(dx, dy, dz)));
    }

    private void rotateActiveY(double angle) {
        Solid s = solids[activeIndex];
        s.setModel(new Mat4RotY(angle).mul(s.getModel()));
    }

    private void rotateActiveX(double angle) {
        Solid s = solids[activeIndex];
        s.setModel(new Mat4RotX(angle).mul(s.getModel()));
    }
}
