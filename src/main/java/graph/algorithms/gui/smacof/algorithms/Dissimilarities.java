/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.algorithms;

/**
 * Dissimilarities that can be used to compute Delta.
 *
 * @author philipp
 */
public enum Dissimilarities {

    /**
     * Do not compute dissimilarities, just use the high dimensional data
     * points, data has to be quadratic and symmetric.
     */
    NONE,
    /**
     * Use the standard euclidean distance to compute the dissimilarity data.
     */
    EUCLIDEAN,
    /**
     * Compute dissimilarity data according to the mahalanobis distance.
     */
    MAHALANOBIS,
    /**
     * Compute dissimilarity data according to the canberras formula.
     */
    CANBERRA,
    /**
     * Compute dissimilarity data according to divergence measure.
     */
    DIVERGENCE,
    /**
     * Compute dissimilarity data according to bray curtis.
     */
    BRAY_CURTIS,
    /**
     * Compute dissimilarity data according to soergel.
     */
    SOERGEL,
    /**
     * Compute dissimilarity data according to bahattacharryya.
     */
    BAHATTACHARYYA,
    /**
     * Compute dissimilarity data according to wave hedges.
     */
    WAVE_HEDGES,
    /**
     * Compute dissimilarity data according to angular seperation.
     */
    ANGULAR_SEPERATION,
    /**
     * Use correlation to compute dissimilarity data.
     */
    CORRELATION,
    /**
     * Compute dissimilarity data according to a minkowski distance.
     */
    MINKOWSKI
}
