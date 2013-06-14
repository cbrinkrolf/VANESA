/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;


/**
 *
 * @author test
 */
public class ModularGridLayout implements ModularLayout {

//    private final static Logger logger =
//            Logger.getLogger(ModularGridLayout.class);
    private MDNode[] vertices;
    private MDNode parent;
    private double constant_k;
    private int rowCount,  columnCount;

    public ModularGridLayout(MDNode _parent, double _constant_k) {
        this.parent = _parent;
        vertices =
                parent.getChildren().toArray(new MDNode[0]);
        this.constant_k = _constant_k;
    }

    public void doLayout() {
        this.initialize();
        if (this.columnCount == 0) {
            return;
//        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        }
        double wmax = 0, hmax[] = new double[rowCount], sumW[] = new double[rowCount], sumH = 0;
        for (int i = 0; i < rowCount; i++) {
            sumW[i] = 0;
            for (int j = 0; j < columnCount; j++) {
                int idx = i * columnCount + j;
                if (idx < vertices.length) {
                    sumW[i] += (vertices[idx].getWidth() + this.constant_k);
                    hmax[i] = Math.max(hmax[i], vertices[idx].getHeight());
                } else {
                    break;
                }
            }
            sumW[i] -= this.constant_k;
            wmax = Math.max(wmax, sumW[i]);
            sumH += (hmax[i] + this.constant_k);
        }
        sumH -= this.constant_k;
        double startY = -sumH / 2;
        for (int i = 0; i < rowCount; i++) {
            int c = columnCount - 1;
            if (i == rowCount - 1 && vertices.length % columnCount > 0) {
                c = vertices.length % columnCount - 1;
            }
            double dist = (wmax - sumW[i]) / c + this.constant_k;
            double y = startY + hmax[i] / 2;
            double startX = -wmax / 2;
            for (int j = 0; j < columnCount; j++) {
                int idx = i * columnCount + j;
                if (idx < vertices.length) {
                    int w = vertices[idx].getWidth();
                    double x = startX + w / 2;
                    startX += (dist + w);
                    vertices[idx].translate(
                            (int) Math.round(x),
                            (int) Math.round(y));
                }
            }
            startY += hmax[i] + this.constant_k;
        }
//        node.setSize(wmax, sumH);
    }

    public void initialize() {
        columnCount = (int) Math.ceil(Math.sqrt(vertices.length));
        rowCount = vertices.length / columnCount;
        if (vertices.length % columnCount > 0) {
            ++rowCount;
        }

    }

    public void setConstant_k(double constant_k) {
        this.constant_k = constant_k;
    }
}
