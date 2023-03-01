package graph.algorithms.gui;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import gui.ImagePath;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mlewinsk This class Applies a coloring theme to a given pathway/network. The input maps Node-IDs (not jung
 * 'vXXX') to a specific value which will be translated to a color range (max-min). The color range is given through a
 * Bitmap.
 */
public class GraphColorizer {
    private double min;
    private double range;
    private final int imageWidth;
    private final Map<BiologicalNodeAbstract, Double> nodeValues;
    private final BufferedImage rangeSource;

    public GraphColorizer(Map<BiologicalNodeAbstract, Double> nodeValues, int rangeImage, boolean logarithmic) {
        this.nodeValues = logarithmic ? logDataSet(nodeValues) : nodeValues;
        // Get image and assign the rangewidth
        Image img;
        switch (rangeImage) {
            case 0:
                img = ImagePath.getInstance().getImageIcon("colorrange_bluesea.png").getImage();
                break;
            case 1:
                img = ImagePath.getInstance().getImageIcon("colorrange_skyline.png").getImage();
                break;
            case 2:
                img = ImagePath.getInstance().getImageIcon("colorrange_darkmiddle.png").getImage();
                break;
            case 3:
                img = ImagePath.getInstance().getImageIcon("colorrange_dark.png").getImage();
                break;
            case 4:
                img = ImagePath.getInstance().getImageIcon("colorrange_rainbow.png").getImage();
                break;
            default:
                img = ImagePath.getInstance().getImageIcon("colorrange_bluesea.png").getImage();
                break;
        }
        rangeSource = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = rangeSource.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        imageWidth = rangeSource.getWidth(null);
        evaluateData();
        assignNodeColoring();
    }

    /**
     * convert dataset to logarithmic scale, standard is linear
     */
    private Map<BiologicalNodeAbstract, Double> logDataSet(Map<BiologicalNodeAbstract, Double> nodeValues) {
        Map<BiologicalNodeAbstract, Double> logTable = new HashMap<>();
        for (Map.Entry<BiologicalNodeAbstract, Double> entry : nodeValues.entrySet()) {
            logTable.put(entry.getKey(), Math.log10(entry.getValue()));
        }
        return logTable;
    }

    private void evaluateData() {
        min = Double.MAX_VALUE;
        double max = Double.MIN_NORMAL;
        for (Map.Entry<BiologicalNodeAbstract, Double> entry : nodeValues.entrySet()) {
            max = Math.max(max, entry.getValue());
            min = Math.min(min, entry.getValue());
        }
        range = max - min;
    }

    private void assignNodeColoring() {
        for (Map.Entry<BiologicalNodeAbstract, Double> entry : nodeValues.entrySet()) {
            int imageIndex = (int) (((imageWidth - 1) / range) * (entry.getValue() - min));
            entry.getKey().setColor(new Color(rangeSource.getRGB(imageIndex, 0)));
        }
    }
}
