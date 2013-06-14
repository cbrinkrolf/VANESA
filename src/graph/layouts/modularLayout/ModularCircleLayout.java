package graph.layouts.modularLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ModularCircleLayout implements ModularLayout {

    private final static Logger logger =
            Logger.getLogger(ModularCircleLayout.class.getName());
    private MDNode[] vertices;
    private MDNode parent;
    private double constant_k;
    double radii[], theta, sin2, diagonals[];

    public ModularCircleLayout(MDNode _parent, double _constant_k) {
        this.parent = _parent;
        vertices =
                parent.getChildren().toArray(new MDNode[0]);
        this.constant_k = _constant_k;
    }

    public void doLayout() {
        this.initialize();
//        int minX = 0, maxX = 0, minY = 0, maxY = 0;

        for (int i = 0; i < vertices.length; i++) {
            int idx1 = i - 1, idx2 = i + 1;
            if (idx1 < 0) {
                idx1 = vertices.length - 1;
            }
            if (idx2 == vertices.length) {
                idx2 = 0;
            }
            MDNode n = vertices[i], np = vertices[idx1], ns = vertices[idx2];
            if (i == 0) {
                radii[idx1] = (diagonals[i] +
                        this.constant_k + diagonals[idx1]) / sin2;
            }
            if (i != vertices.length - 1) {
                radii[i] = (diagonals[i] +
                        this.constant_k + diagonals[idx2]) / sin2;
            }
            double r = 0;
            if (diagonals[i] == diagonals[idx1]) {
                r = radii[idx1];
            } else if (diagonals[i] == diagonals[idx2]) {
                r = radii[i];
            } else {
                r = Math.max(radii[idx1], radii[i]);
            }
            logger.log(Level.ALL,"r:" + r);
            int x = (int) Math.round(r * Math.cos(theta * i)),
                    y = (int) Math.round(r * Math.sin(theta * i));
//            n.getCenter().setLocation(x, y);
            n.translate(x, y);
//            minX = Math.min(minX, x);
//            minY = Math.min(minY, y);
//            maxX = Math.max(maxX, x);
//            maxY = Math.max(maxY, y);
        }
//        int width = (Math.max(Math.abs(minX), maxX) + k) * 2,
//                height = (Math.max(Math.abs(minY), maxY) + k) * 2;
//        node.setSize(width, height);        
//        logger.debug("circle layout:" + Arrays.asList(this.vertices));
//        for (MDNode n : vertices) {
//            logger.debug(n + "-->" + n.getCenter());
//        }
    }

    public void initialize() {
        radii = new double[vertices.length];
        theta = Math.PI * 2 / vertices.length;
        sin2 = 2 * Math.sin(theta / 2);
        diagonals = new double[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            diagonals[i] = Math.sqrt(
                    Math.pow(vertices[i].getWidth(), 2) +
                    Math.pow(vertices[i].getHeight(), 2))/2.0;
        }
    }

    public void setConstant_k(double constant_k) {
        this.constant_k = constant_k;
    }
}
