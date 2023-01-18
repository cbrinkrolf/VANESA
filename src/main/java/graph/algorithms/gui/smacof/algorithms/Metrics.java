/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.algorithms;

import java.util.Map;

import graph.algorithms.gui.smacof.datastructures.Mat;
import graph.algorithms.gui.smacof.datastructures.TriMat;
import graph.algorithms.gui.smacof.datastructures.Vector;

/**
 * This class contains the functions for the minkowski distances, it also has
 * the functions to compute the distance matrix Delta for any given map.
 *
 * @author philipp
 */
public class Metrics {

    /**
     * Calculates the minkowsky distance between two vector objects, these two
     * vectors are not allowed to differ in length - otherwise the result is
     * NaN.
     *
     * @param a vector
     * @param b vector
     * @param p the power that determines the specific minkowsky distance: e.g.
     * 1 would lead to city block metric, 2 lead to euclidean metric ...
     * @return
     */
    public static double minkowskyDistance(Vector a, Vector b, int p) {
        if (a.getSize() == b.getSize()) {
            double sum = 0;
            for (int i = 0; i < a.getSize(); i++) {
                sum += Math.pow(Math.abs(a.getElement(i) - b.getElement(i)), p);
            }
            return Math.pow(sum, 1.0 / (double) p);
        } else {
            return Double.NaN;
        }
    }

    /**
     * Calculates the minkowsky distance between two double arrays, these two
     * arrays must have the same length - otherwise the result will be NaN.
     *
     * @param a data array
     * @param b data array
     * @param p the power that determines the specific minkowsky distance: e.g.
     * 1 would lead to city block metric, 2 lead to euclidean metric ...
     * @return
     */
    public static double minkowskyDistance(double[] a, double[] b, int p) {
        if (a.length == b.length) {
            double sum = 0;
            for (int i = 0; i < a.length; i++) {
                sum += Math.pow(Math.abs(a[i] - b[i]), p);
            }
            return Math.pow(sum, 1.0 / (double) p);
        } else {
            return Double.NaN;
        }
    }

    /**
     * Calculates the corresponding distance matrix to a bunch of read-in
     * vectors; the distance matrix is stored in a triangular matrix.
     *
     * @param mat an array of vectors
     * @param metric an integer that determines which of the minkowsky metrics
     * should be used
     * @return
     */
    public static TriMat calcTriDistanceMatrix(Vector[] mat, int metric) {
        TriMat dist_mat = new TriMat(mat.length);
        for (int i = 0; i < mat.length; i++) {
            dist_mat.setLabel(i, mat[i].getLabel());
            for (int j = i; j < mat.length; j++) {
                dist_mat.setElement(j, i, minkowskyDistance(mat[i], mat[j], metric));
            }
        }
        return dist_mat;
    }

    /**
     * Calculates the corresponding distance matrix to a read-in matrix; the
     * distance matrix is stored in a triangular matrix.
     *
     * @param mat a matrix
     * @param metric an integer that determines which of the minkowsky metrics
     * should be used
     * @return
     */
    public static TriMat calcTriDistanceMatrix(Mat mat, int metric) {
        TriMat dist_mat = new TriMat(mat.getFirstDimSize());
        dist_mat.setLabels(mat.getLabels());
        for (int i = 0; i < mat.getFirstDimSize(); i++) {
            for (int j = i; j < mat.getFirstDimSize(); j++) {
                dist_mat.setElement(j, i, minkowskyDistance(mat.getColumn(i), mat.getColumn(j), metric));
            }
        }
        return dist_mat;
    }

    /**
     * Calculates the corresponding distance matrix to a bunch of arrays; the
     * distance matrix is stored in a triangular matrix.
     *
     * @param map
     * @param metric
     * @return
     */
    public static TriMat calcTriDistanceMatrix(Map<Integer, double[]> map, int metric) {
        TriMat dist_mat = new TriMat(map.size());
        Object[] values = map.values().toArray();
        Object[] keys = map.keySet().toArray();
//        System.out.println(Arrays.toString(keys));
//        for (int k = 0; k < values.length; k++) {
//            System.out.println(Arrays.toString((double[]) values[k]));
//        }
        String[] labels = new String[map.size()];
        for (int i = 0; i < keys.length; i++) {
            labels[i] = String.valueOf(keys[i]);
        }
        dist_mat.setLabels(labels);
        for (int i = 0; i < values.length; i++) {
            for (int j = i; j < values.length; j++) {
                dist_mat.setElement(j, i, minkowskyDistance((double[]) values[i], (double[]) values[j], metric));
            }
        }
        return dist_mat;
    }

    public static TriMat calcTriDissimilarityMatrix(Map<Integer, double[]> map, Dissimilarities dis, int metric) {
        TriMat dist_mat = new TriMat(map.size());
        Object[] values = map.values().toArray();
        Object[] keys = map.keySet().toArray();
//        System.out.println(Arrays.toString(keys));
//        for (int k = 0; k < values.length; k++) {
//            System.out.println(Arrays.toString((double[]) values[k]));
//        }
        String[] labels = new String[map.size()];
        for (int i = 0; i < keys.length; i++) {
            labels[i] = String.valueOf(keys[i]);
        }
        dist_mat.setLabels(labels);
        /*
         Calculates the dissimilarity TriMat Matrix according to the choosen
         dissimilarity function:
         */
        switch (dis) {
            case NONE:
                dist_mat = Utils.mapToTriMat(map);
                break;
            case EUCLIDEAN:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, minkowskyDistance((double[]) values[i], (double[]) values[j], 2));
                    }
                }
                break;
            case MINKOWSKI:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, minkowskyDistance((double[]) values[i], (double[]) values[j], metric));
                    }
                }
                break;
            case MAHALANOBIS:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.mahalanobis((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case CANBERRA:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.canberra((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case DIVERGENCE:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.divergence((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case BRAY_CURTIS:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.brayCurtis((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case SOERGEL:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.soergel((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case BAHATTACHARYYA:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.bahattacharyya((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case WAVE_HEDGES:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.waveHedges((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case ANGULAR_SEPERATION:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.angularSeperation((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
            case CORRELATION:
                for (int i = 0; i < values.length; i++) {
                    for (int j = i; j < values.length; j++) {
                        dist_mat.setElement(j, i, DissimilarityFunctions.correlation((double[]) values[i], (double[]) values[j]));
                    }
                }
                break;
        }
        // eleminate possible NaN candidates, that can occur in some 
        // dissimilaritiy measures, if the dissimilarity from a vector
        // is calculated to itself (divide by 0 an stuff)
        for (int i = 0; i < values.length; i++) {
            if (Double.isNaN(dist_mat.getElement(i, i))) {
                dist_mat.setElement(i, i, 0f);
            }
        }
        return dist_mat;
    }

}

