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
import java.util.HashMap;
import java.util.Map;
import graph.algorithms.gui.smacof.view.SmacofView;

/**
 * The Smacof 'main' class: contains the smacof algorithm and all functions
 * necessary to compute it.
 *
 * @author philipp
 */
public class Smacof {

    private final Mat TEST_Z = getTestZ();

    public Smacof() {

    }

    /**
     * The SMACOF (scaling by majorizing a convex function) algorithm computes a
     * map with low dimensional data vectors, that represent the high
     * dimensional data vector map with the lowest possible error; SMACOF is a
     * map that obtains the distances.
     *
     * @param data
     * @param maxiter
     * @param epsilon
     * @param dim_res
     * @param dis
     * @param metric
     * @param print
     * @return a map with (id, vector) tuples
     */
    public Mat smacofAlgorithm(Map<Integer, double[]> data,
            int maxiter,
            double epsilon,
            int dim_res,
            Dissimilarities dis,
            int metric,
            boolean print) {
        // before we start we have to calculate the dissimilarities:
        TriMat Delta = Metrics.calcTriDissimilarityMatrix(data, dis, metric);
        if (print) {
            System.out.println("DELTA");
            System.out.println(Delta);
        }
        Mat X = null;
        Mat Z = new Mat(Delta.getSize(), dim_res);

        Z.initRand();
        // Test line with text book example:
        // Z = TEST_Z;
        X = Z;
        if (print) {
            System.out.println("Z");
            System.out.println(Z);
        }
        TriMat D_Z = Metrics.calcTriDistanceMatrix(Z, metric);
        if (print) {
            System.out.println("D(Z)");
            System.out.println(D_Z);
        }
        TriMat B_Z = computeB_Z(null, Delta, D_Z);
        if (print) {
            System.out.println("B(Z)");
            System.out.println(B_Z);
        }
        double sigma_prev = computeSigma(null, X, Delta, metric);
        double sigma = 0.0f;
        if (print) {
        	System.out.println("starting iteration:");
            System.out.println("iteration:0" + " epsilon:" + Math.abs(sigma_prev - sigma));
        }
        int k = 1;
        StopWatch swatch = new StopWatch();
        while (k < maxiter && Math.abs(sigma_prev - sigma) > epsilon) {
            swatch.start();
            sigma_prev = computeSigma(null, X, Delta, metric);
            Z = X;
            X = guttmanTransformation(null, Delta, Z, metric);
            sigma = computeSigma(null, X, Delta, metric);
            swatch.end();
            SmacofView.setLabelcuriteration(k);
            SmacofView.setLabelcurepsilon(sigma_prev - sigma);
            SmacofView.setLabelcurtime(swatch.getSecs());
            //if (print) {
            //    System.out.println("k:" + k + " sigma(k):" + sigma + " eps:" + Math.abs(sigma_prev - sigma) + " in " + swatch.getSecs() + " seconds");
            //}
            k++;
        }
        if(print) {
        	System.out.println("used iterations:" + k);
        	System.out.println("final configuration:");
        }
        Object[] keys = data.keySet().toArray();
        String[] str_keys = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            str_keys[i] = String.valueOf(keys[i]);
        }
        X.setLabel(str_keys);
        if(print) {
        	System.out.println(X);
        }
        return X;
    }

    /**
     * Perfoms the Guttman-Transformation.
     *
     * @param W
     * @param Delta
     * @param Z
     * @param metric
     * @return
     */
    public Mat guttmanTransformation(TriMat W, TriMat Delta, Mat Z, int metric) {
        Mat X_update;
        double norm_factor = 1.0f / (double) Delta.getSize();
        // if all weights are equal to 1, use simple guttman formula
        if (W == null) {
            X_update = computeB_Z(null, Delta, Metrics.calcTriDistanceMatrix(Z, metric)).matrixMultiplication(Z).scalarMultiplication(norm_factor);
        } // if we have weights, we have to use a more advanced formula
        else {
            Mat V = computeV(W);
            Mat pseudoInverse = V.moorePenroseInverse();
            Mat tmp = computeB_Z(W, Delta, Metrics.calcTriDistanceMatrix(Z, metric)).matrixMultiplication(Z);
            System.out.println(pseudoInverse);
            System.out.println(tmp);
            X_update = pseudoInverse.matrixMultiplication(computeB_Z(W, Delta, Metrics.calcTriDistanceMatrix(Z, metric)).matrixMultiplication(Z));
            System.out.println(X_update);
        }
        return X_update;
    }

    /**
     * Computes the V matrix to the corresponding W Matrix.
     *
     * @param W
     * @return V
     */
    public Mat computeV(TriMat W) {
        Mat V = new Mat(W.getSize(), W.getSize());
        for (int i = 0; i < W.getSize(); i++) {
            for (int j = 0; j < W.getSize(); j++) {
                if (i != j) {
                    V.setElement(i, j, -W.getElement(i, j));
                }
            }
        }
        // Caution: diagonal entries have to be calculated at last!
        for (int i = 0; i < W.getSize(); i++) {
            double sumcol = 0;
            for (int j = 0; j < W.getSize(); j++) {
                sumcol += V.getElement(j, i);
            }
            V.setElement(i, i, -sumcol);
        }
        return V;
    }

    /**
     * Computes the weight matrix W for a given dissimilarity matrix - missing
     * values get a 0 entry, 1 otherwise.
     *
     * @param Delta
     * @return W
     */
    public TriMat computeW(TriMat Delta) {
        TriMat W = new TriMat(Delta.getSize(), 1);
        for (int i = 0; i < Delta.getSize(); i++) {
            for (int j = 0; j < i; j++) {
                if (Delta.getElement(i, j) == 0) {
                    W.setElement(i, j, 0);
                }
            }
        }
        return W;
    }

    /**
     * Computes the remaining error of a matrix according to a given metric.
     *
     * @param W
     * @param X
     * @param Delta
     * @param metric
     * @return
     */
    public double computeSigma(TriMat W, Mat X, TriMat Delta, int metric) {
        double sigma = 0.0f;
        TriMat D_X = Metrics.calcTriDistanceMatrix(X, metric);
        if (W == null) {
            for (int i = 0; i < Delta.getSize(); i++) {
                for (int j = 0; j < i; j++) {
                    sigma += Math.pow(Delta.getElement(i, j) - D_X.getElement(i, j), metric);
                }
            }
        } // if we have weights, we have to use a more advanced formula
        else {
            for (int i = 0; i < Delta.getSize(); i++) {
                for (int j = 0; j < i; j++) {
                    sigma += W.getElement(i, j) * Math.pow((Delta.getElement(i, j) - D_X.getElement(i, j)), metric);
                }
            }
        }
        return sigma;
    }

    /**
     * Computes the B function of a given matrix.
     *
     * @param W
     * @param Delta
     * @param D_Z
     * @return
     */
    public TriMat computeB_Z(TriMat W, TriMat Delta, TriMat D_Z) {
        int size = Delta.getSize();
        TriMat res = new TriMat(size);
        if (W == null) {
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    if (i != j) {
                        double value = (double) -(Delta.getElement(i, j) / D_Z.getElement(i, j));
                        res.setElement(i, j, value);
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    if (i != j) {
                        double value = (double) -((W.getElement(i, j) * Delta.getElement(i, j)) / D_Z.getElement(i, j));
                        res.setElement(i, j, value);
                    }
                }
            }
        }
        // Caution: diagonal entries have to be calculated at last!
        for (int i = 0; i < size; i++) {
            double sumcol = 0;
            for (int j = 0; j < size; j++) {
                sumcol += res.getElement(j, i);
            }
            res.setElement(i, i, -sumcol);
        }
        return res;
    }

    /**
     * Returns a 4x4 matrix containing the test values from the text book.
     *
     * @return a matrix
     */
    private Mat getTestZ() {
        Mat m = new Mat(4, 2);
        double[][] mt = m.getLonleyMatrix();
        double[] a = {-0.266, -0.539};
        double[] b = {0.451, 0.252};
        double[] c = {0.016, -0.238};
        double[] d = {-0.200, 0.524};
        mt[0] = a;
        mt[1] = b;
        mt[2] = c;
        mt[3] = d;
        return m;
    }

}

