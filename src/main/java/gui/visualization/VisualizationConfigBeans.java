package gui.visualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import gui.MainWindow;

public class VisualizationConfigBeans {
    private final List<Bean> beansList = new ArrayList<>();

    public List<Bean> parseAndAdjust(HashMap<String, Map<String, Object>> object, boolean doAdjust) {
        for (String key : object.keySet()) {
            Bean bean = new Bean();
            bean.setName(key);
            bean.setShape(object.get(key).get("shape").toString());
            bean.setSizefactor(Double.parseDouble(object.get(key).get("sizefactor").toString()));
            bean.setColorRed((int) object.get(key).get("red"));
            bean.setColorGreen((int) object.get(key).get("green"));
            bean.setColorBlue((int) object.get(key).get("blue"));
            beansList.add(bean);
        }
        if (doAdjust) {
            MainWindow w = MainWindow.getInstance();
            if (w.getCurrentPathway() != null) {
                Pathway pathway = GraphInstance.getPathway();
                Collection<BiologicalNodeAbstract> allNodes = pathway.getAllGraphNodes();
                w.setBeansList(beansList);
                for (BiologicalNodeAbstract nodes : allNodes) {
                    w.nodeAttributeChanger(nodes, true);
                }
            }
        }
        return beansList;
    }

    public static class Bean {
        private String name;
        private String shape;
        private double sizefactor;
        private int colorRed;
        private int colorGreen;
        private int colorBlue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShape() {
            return shape;
        }

        public void setShape(String shape) {
            this.shape = shape;
        }

        public double getSizefactor() {
            return sizefactor;
        }

        public void setSizefactor(double sizefactor) {
            this.sizefactor = sizefactor;
        }

        public int getColorRed() {
            return colorRed;
        }

        public void setColorRed(int colorRed) {
            this.colorRed = colorRed;
        }

        public int getColorGreen() {
            return colorGreen;
        }

        public void setColorGreen(int colorGreen) {
            this.colorGreen = colorGreen;
        }

        public int getColorBlue() {
            return colorBlue;
        }

        public void setColorBlue(int colorBlue) {
            this.colorBlue = colorBlue;
        }
    }
}
