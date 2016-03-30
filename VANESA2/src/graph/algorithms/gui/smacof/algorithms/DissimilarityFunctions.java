/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.algorithms;

/**
 * This class provides the functions for computing the distances between two
 * data vectors.
 * @author philipp
 */
public class DissimilarityFunctions {

    /**
     * Not ready yet!.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return mahalanobis distance
     */
    public static double mahalanobis(double[] a, double[] b) {
        if (a.length == b.length) {
            throw new UnsupportedOperationException("not implemented.");
        } else {
            throw new UnsupportedOperationException("not implemented.");
        }
    }

    /**
     * Calculate the canberra distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return canberra distance
     */
    public static double canberra(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum = 0;
            double den = 0;
            for (int i = 0; i < a.length; i++) {
                den = Math.abs(a[i]) + Math.abs(b[i]);
                sum += (den != 0.0) ? (Math.abs(a[i] - b[i])) / den : 0.0;
            }
            return sum;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the divergence distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the divergence
     */
    public static double divergence(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum = 0;
            double den = 0;
            for (int i = 0; i < a.length; i++) {
                den = (a[i] + b[i]) * (a[i] + b[i]);
                sum += (den != 0.0) ? ((a[i] - b[i]) * (a[i] - b[i])) / den : 0.0;
            }
            return sum;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Bray-Curtis distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the bray-curtis distance
     */
    public static double brayCurtis(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum_num = 0;
            double sum_den = 2;
            for (int i = 0; i < a.length; i++) {
                sum_num += Math.abs(a[i] - b[i]);
                sum_den += a[i] + b[i];
            }
            return sum_num / sum_den;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Soergel distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the soergel distance
     */
    public static double soergel(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum_num = 0;
            double sum_den = 0;
            for (int i = 0; i < a.length; i++) {
                sum_num += Math.abs(a[i] - b[i]);
                sum_den += Math.max(a[i], b[i]);
            }
            return (sum_den != 0) ? sum_num / sum_den : 0.0;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Bahattacharyya distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the bahattacharyya distance
     */
    public static double bahattacharyya(double[] a, double[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] < 0 || b[i] < 0) {
                    throw new IllegalArgumentException("bahattacharyya only availible for positive numbers!");
                }
            }
            double sum = 0;
            for (int i = 0; i < a.length; i++) {
                sum += (Math.sqrt(a[i]) - Math.sqrt(b[i])) * (Math.sqrt(a[i]) - Math.sqrt(b[i]));
            }
            return Math.sqrt(sum);
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Wave-Hedges distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the wave-hedges distance
     */
    public static double waveHedges(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum = 0;
            double den = 0;
            for (int i = 0; i < a.length; i++) {
                den = Math.max(a[i], b[i]);
                sum += (den != 0.0) ? 1 - (Math.min(a[i], b[i]) / den) : 1;
            }
            return sum;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Angular Seperation distance between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the angular-seperation
     */
    public static double angularSeperation(double[] a, double[] b) {
        if (a.length == b.length) {
            double sum_num = 0;
            double sum_den_a = 0;
            double sum_den_b = 0;
            double sum_den = 0;
            for (int i = 0; i < a.length; i++) {
                sum_num += a[i] * b[i];
                sum_den_a += a[i] * a[i];
                sum_den_b += b[i] * b[i];
            }
            sum_den = Math.sqrt(sum_den_a * sum_den_b);
            return (sum_den != 0.0) ? 1 - (sum_num / sum_den) : 1;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }

    /**
     * Calculate the Correlation between two vectors.
     *
     * @param a array of doubles
     * @param b array of doubles
     * @return the correlation
     */
    public static double correlation(double[] a, double[] b) {
        if (a.length == b.length) {
            double mean_a = 0;
            double mean_b = 0;
            for (int i = 0; i < a.length; i++) {
                mean_a += a[i];
                mean_b += b[i];
            }
            mean_a /= a.length;
            mean_b /= b.length;
            double sum_num = 0;
            double sum_den = 0;
            for (int i = 0; i < a.length; i++) {
                sum_num += (a[i] - mean_a) * (b[i] - mean_b);
                sum_den += (a[i] - mean_a) * (a[i] - mean_a) * (b[i] - mean_b) * (b[i] - mean_b);
            }
            return (sum_den != 0.0) ? 1 - (sum_num / Math.sqrt(sum_den)) : 1;
        } else {
            throw new IllegalArgumentException("vector double[] a and double[] b differ in length");
        }
    }
}
