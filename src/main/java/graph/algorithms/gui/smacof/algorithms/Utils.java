package graph.algorithms.gui.smacof.algorithms;

import graph.algorithms.gui.smacof.datastructures.TriMat;

import java.util.Map;

/**
 * Utils contains some useful functions, that are partly needed in order to compute the Smacof.
 *
 * @author philipp
 */
public class Utils {
    /**
     * Converts a Map to a TriMat object.
     *
     * @return a trimat object with the map content
     */
    public static TriMat mapToTriMat(Map<Integer, double[]> map) {
        Object[] values = map.values().toArray();
        Object[] keys = map.keySet().toArray();
        TriMat mat = new TriMat(map.size());
        String[] labels = new String[map.size()];
        for (int i = 0; i < keys.length; i++) {
            labels[i] = String.valueOf(keys[i]);
        }
        mat.setLabels(labels);
        for (int i = 0; i < map.size(); i++) {
            double[] v = (double[]) values[i];
            for (int j = i; j < v.length; j++) {
                mat.setElement(i, j, v[j]);
            }
        }
        return mat;
    }
}
