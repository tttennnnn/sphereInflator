import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // sliders for adjusting view
        JSlider horSlider = new JSlider(-180, 180, 30);
        JSlider vertSlider = new JSlider(SwingConstants.VERTICAL, -180, 180, 30);
        pane.add(horSlider, BorderLayout.SOUTH);
        pane.add(vertSlider, BorderLayout.EAST);

        // button for reset view
        JButton resetButton = new JButton("resetView");
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(40, 25));
        // button for color faces
        JToggleButton fillButton = new JToggleButton("colorFill");
        fillButton.setFocusPainted(false);
        fillButton.setPreferredSize(new Dimension(40, 25));
        // button for inflate/deflate
        JButton inflateButton = new JButton("Inflate");
        inflateButton.setFocusPainted(false);
        inflateButton.setPreferredSize(new Dimension(40, 25));
        JButton deflateButton = new JButton("Deflate");
        deflateButton.setFocusPainted(false);
        deflateButton.setPreferredSize(new Dimension(40, 25));

        // button pane
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPane.add(resetButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 10)));
        buttonPane.add(fillButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 10)));
        buttonPane.add(inflateButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 10)));
        buttonPane.add(deflateButton);

        pane.add(buttonPane, BorderLayout.NORTH);

        // initialize tetrahedron object
        Triangle[] tris = {
                new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100)),
                new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, -100)),
                new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100)),
                new Triangle(new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100))
        };
        renderedObj tetra = new renderedObj(tris);
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                // view angle
                double horAngle = Math.toRadians(horSlider.getValue()) - tetra.horAngle;
                double vertAngle = Math.toRadians(vertSlider.getValue()) - tetra.vertAngle;
                tetra.horAngle = Math.toRadians(horSlider.getValue());
                tetra.vertAngle = Math.toRadians(vertSlider.getValue());
                Matrix rotMatrix = new Matrix(
                        new double[] // around x-axis
                                { 1,            0,                    0,
                                  0,  Math.cos(vertAngle),   Math.sin(vertAngle),
                                  0, -Math.sin(vertAngle),   Math.cos(vertAngle)}
                );
                rotMatrix.mtp3x3(
                        new double[] // around y-axis
                                { Math.cos(horAngle), 0, -Math.sin(horAngle),
                                          0,          1,         0,
                                  Math.sin(horAngle), 0,  Math.cos(horAngle)}
                );
                // transform vectors
                for (Triangle face : tris){
                    face.updateVertices(rotMatrix.transform(face.v1),
                                        rotMatrix.transform(face.v2),
                                        rotMatrix.transform(face.v3));
                }
                tetra.faces = tris;
                //  inflate object
                for (int i = 0; i < tetra.inflatecount; i++){
                    tetra.faces = Renderer.inflate(tetra.faces);
                }
                // start painting
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.white);
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (fillButton.isSelected()){ // paint faces
                    BufferedImage colorFill =
                            new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // rasterize z axis
                    double[] depth = new double[getWidth()*getHeight()];
                    Arrays.fill(depth, Double.POSITIVE_INFINITY);

                    for (Triangle face : tetra.faces){
                        baryCentric bc = new baryCentric(face);
                        bc.translate(getWidth()/2., getHeight()/2.);
                        int minX = Math.max(0, bc.getminX());
                        int maxX = Math.min(getWidth() - 1, bc.getmaxX());
                        int minY = Math.max(0, bc.getminY());
                        int maxY = Math.min(getHeight() - 1, bc.getmaxY());
                        double triArea = baryCentric.triArea(bc.v1.x, bc.v1.y, bc.v2.x, bc.v2.y, bc.v3.x, bc.v3.y);

                        // light src @ (0, 0, -inf)
                        Vertex normal = Matrix.normal(face.v1, face.v2, face.v3);
                        Color shadeColor = Renderer.shadeColor(face.color, Math.abs(normal.z));

                        for (int x = minX ; x <= maxX; x++){
                            for (int y = minY; y <= maxY; y++){
                                double w1 = baryCentric.triArea(bc.v1.x, bc.v1.y, bc.v2.x, bc.v2.y, x, y) / triArea;
                                double w2 = baryCentric.triArea(bc.v1.x, bc.v1.y, x, y, bc.v3.x, bc.v3.y) / triArea;
                                if (0 <= w1 && w1 <= 1 && 0 <= w2 && w2 <= 1 && w1+w2 <= 1){
                                    double z = w1 * bc.v3.z + w2 * bc.v2.z + (1-w1-w2) * bc.v1.z;
                                    if (z < depth[y * getWidth() + x]){
                                        colorFill.setRGB(x, y, shadeColor.getRGB());
                                        depth[y * getWidth() + x] = z;
                                    }
                                }
                            }
                        }
                    }
                    g2.drawImage(colorFill, 0, 0, null);
                }
                else { // trace edges
                    g2.translate(getWidth() / 2, getHeight() / 2);
                    g2.setColor(Color.BLACK);
                    for (Triangle face : tetra.faces) {
                        Path2D path = new Path2D.Double();
                        path.moveTo(face.v1.x, face.v1.y);
                        path.lineTo(face.v2.x, face.v2.y);
                        path.lineTo(face.v3.x, face.v3.y);
                        path.closePath();
                        g2.draw(path);
                    }
                }
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        // repaint if input changes
        horSlider.addChangeListener(e -> renderPanel.repaint());
        vertSlider.addChangeListener(e -> renderPanel.repaint());
        resetButton.addActionListener(e -> {
            horSlider.setValue(0);
            vertSlider.setValue(0);
            tetra.resetFaces();
            renderPanel.repaint();
        });
        fillButton.addActionListener(e -> renderPanel.repaint());
        inflateButton.addActionListener(e -> {
            if (tetra.inflatecount < 5){
                tetra.inflatecount++;
                renderPanel.repaint();
            }
        });
        deflateButton.addActionListener(e -> {
            if (tetra.inflatecount > 0){
                tetra.inflatecount--;
                renderPanel.repaint();
            }
        });

        // set frame
        frame.setSize(800, 800);
        frame.setVisible(true);
    }
}

class Renderer {
    static Color shadeColor(Color c, double shade) {
        int r = (int) (c.getRed() * Math.pow(shade, 1 / 2.4));
        int g = (int) (c.getGreen() * Math.pow(shade, 1 / 2.4));
        int b = (int) (c.getBlue() * Math.pow(shade, 1 / 2.4));
        return new Color(r, g, b);
    }
    static Triangle[] inflate(Triangle[] faces){
        Triangle[] aug = new Triangle[4 * faces.length];
        int i = 0;
        for (Triangle face : faces){
            Vertex m1 =
                    new Vertex((face.v1.x + face.v2.x)/2, (face.v1.y + face.v2.y)/2, (face.v1.z + face.v2.z)/2);
            Vertex m2 =
                    new Vertex((face.v2.x + face.v3.x)/2, (face.v2.y + face.v3.y)/2, (face.v2.z + face.v3.z)/2);
            Vertex m3 =
                    new Vertex((face.v1.x + face.v3.x)/2, (face.v1.y + face.v3.y)/2, (face.v1.z + face.v3.z)/2);

            aug[4*i] = new Triangle(m1, m2, m3, face.color);
            aug[4*i + 1] = new Triangle(face.v1, m1, m3, face.color);
            aug[4*i + 2] = new Triangle(face.v2, m1, m2, face.color);
            aug[4*i + 3] = new Triangle(face.v3, m2, m3, face.color);
            i++;
        }
        double radius = Math.sqrt(faces[0].v1.x * faces[0].v1.x + faces[0].v1.y * faces[0].v1.y + faces[0].v1.z * faces[0].v1.z);
        for (Triangle face : aug){
            for (Vertex v : new Vertex[] { face.v1, face.v2, face.v3 }) {
                double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / radius;
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }
        return aug;
    }
}