/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.algorithms;

import graph.algorithms.gui.smacof.datastructures.TriMat;
import graph.algorithms.gui.smacof.datastructures.Mat;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * Utils contains some usefull functions, that are partly needed in order to
 * compute the Smacof.
 *
 * @author philipp
 */
public class Utils {

    /**
     * Converts a Mat Object to a Map<Integer, Point2D.Float> representation,
     * the matrix's second dimension has to have length 2 for good reasons.
     *
     * @param m the mat
     * @return map
     */
    public static Map<Integer, Point2D.Float> matToMap(Mat m) {
        Map<Integer, Point2D.Float> result_map = new HashMap<>(m.getFirstDimSize());
        for (int i = 0; i < m.getFirstDimSize(); i++) {
            result_map.put(Integer.parseInt(m.getLabel(i)), new Point2D.Float((float) m.getColumn(i)[0], (float) m.getColumn(i)[1]));
        }
        return result_map;
    }

    /**
     * Converts a Map to a Mat object.
     *
     * @param map
     * @return a mat object with the map content
     */
    public static Mat mapToMat(Map<Integer, double[]> map) {
        Object[] values = map.values().toArray();
        Object[] keys = map.keySet().toArray();
        double[] v = (double[]) values[0];
        Mat mat = new Mat(map.size(), v.length);
        String[] labels = new String[map.size()];
        for (int i = 0; i < keys.length; i++) {
            labels[i] = String.valueOf(keys[i]);
        }
        mat.setLabel(labels);
        for (int i = 0; i < map.size(); i++) {
            v = (double[]) values[i];
            for (int j = 0; j < v.length; j++) {
                mat.setElement(i, j, v[j]);
            }
        }
        return mat;
    }

    /**
     * Converts a Map to a TriMat object.
     *
     * @param map
     * @return a trimat object with the map content
     */
    public static TriMat mapToTriMat(Map<Integer, double[]> map) {
        Object[] values = map.values().toArray();
        Object[] keys = map.keySet().toArray();
        double[] v = (double[]) values[0];
        TriMat mat = new TriMat(map.size());
        String[] labels = new String[map.size()];
        for (int i = 0; i < keys.length; i++) {
            labels[i] = String.valueOf(keys[i]);
        }
        mat.setLabels(labels);
        for (int i = 0; i < map.size(); i++) {
            v = (double[]) values[i];
            for (int j = i; j < v.length; j++) {
                mat.setElement(i, j, v[j]);
            }
        }
        return mat;
    }

    /**
     * Generate a Map with size mapsize, containing random double arrays, where
     * the random numbers are element of [minvalue, maxvalue].
     *
     * @param mapsize size of the map
     * @param vecsize size of a vector
     * @param maxvalue maximum of random values
     * @param minvalue minimum of random values
     * @return
     */
    public static Map<Integer, double[]> generateRandMap(int mapsize, int vecsize, int minvalue, int maxvalue) {
        Map<Integer, double[]> map = new HashMap<>(mapsize);
        double[] vec = new double[vecsize];
        Random random = new Random();
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < vecsize; j++) {
                vec[j] = random.nextInt(maxvalue - minvalue + 1) + minvalue;
            }
            map.put(i, vec);
        }
        return map;
    }

    /**
     * Prints the given map on the command line.
     *
     * @param m map
     */
    public static void printMap(Map<Integer, double[]> m) {
        for (Integer i : m.keySet()) {
            System.out.println(i + ": " + Arrays.toString(m.get(i)));
        }
    }

}

