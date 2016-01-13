/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.datastructures;

import java.util.Arrays;

/**
 * TriMat is a matrix container, which can be used to store symmetric (and
 * quadratic) matrices in O(n^2/2) space; it contains all functions needed to
 * perform the necessary mathematical operations from linear algebra to compute
 * smacof.
 *
 * @author philipp
 */
public class TriMat {

    private String[] labels;
    private double[][] matrix;

    /**
     * Initialize a new triangular matrix with given size.
     *
     * @param size size of triangular matrix
     */
    public TriMat(int size) {
        this.matrix = new double[size][];
        this.labels = new String[size];
        for (int i = 0; i < size; i++) {
            this.matrix[i] = new double[i + 1];
        }
    }

    /**
     * Initialize a new triangular matrix with given size and given value.
     *
     * @param size size of triangular matrix
     * @param value the initial value
     */
    public TriMat(int size, double value) {
        this.matrix = new double[size][];
        this.labels = new String[size];
        for (int i = 0; i < size; i++) {
            this.matrix[i] = new double[i + 1];
        }
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j <= i; j++) {
                this.matrix[i][j] = value;
            }
        }
    }

    /**
     * Initialize a new triangular matrix with a symmetric matrix.
     *
     * @param m
     */
    public TriMat(Mat m) {
        this.matrix = new double[m.getFirstDimSize()][];
        this.labels = m.getLabels();
        for (int i = 0; i < m.getFirstDimSize(); i++) {
            this.matrix[i] = new double[i + 1];
        }
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j <= i; j++) {
                this.matrix[i][j] = m.getElement(i, j);
            }
        }
    }

    /**
     * Returns the size of the triangular matrix.
     *
     * @return the matrix size
     */
    public int getSize() {
        return this.matrix.length;
    }

    /**
     * Sets the given element to the specified matrix position.
     *
     * @param i index
     * @param j index
     * @param e element
     */
    public void setElement(int i, int j, double e) {
        if (i > j) {
            this.matrix[i][j] = e;
        } else {
            this.matrix[j][i] = e;
        }
    }

    /**
     * Returns the element specified by the indices.
     *
     * @param i index
     * @param j index
     * @return element
     */
    public double getElement(int i, int j) {
        if (i > j) {
            return this.matrix[i][j];
        } else {
            return this.matrix[j][i];
        }
    }

    /**
     * Returns a single row from the triangular matrix.
     *
     * @param i row
     * @return the row
     */
    public double[] getRow(int i) {
        double[] tmp = new double[i + 1];
        for (int j = 0; j < i + 1; j++) {
            tmp[j] = this.getElement(i, j);
        }
        return tmp;
    }

    /**
     * Returns a single column from the triangular matrix.
     *
     * @param i column
     * @return the column
     */
    public double[] getColumn(int i) {
        double[] tmp = new double[this.getSize() - i];
        for (int j = 0; j < this.getSize() - i; j++) {
            tmp[j] = this.getElement(j, i);
        }
        return tmp;
    }

    /**
     * Returns the label specified by the index.
     *
     * @param i the index
     * @return the corresponding label
     */
    public String getLabel(int i) {
        return this.labels[i];
    }

    /**
     * Returns a pair of labels which is specified by the two indices
     *
     * @param i the first index
     * @param j the second index
     * @return a String array of size 2, containing the pair of labels
     */
    public String[] getLabelPair(int i, int j) {
        String[] tmp = new String[2];
        tmp[0] = this.labels[i];
        tmp[1] = this.labels[j];
        return tmp;
    }

    /**
     * Sets the label of the specified element.
     *
     * @param i the index
     * @param label the name of the label
     */
    public void setLabel(int i, String label) {
        this.labels[i] = label;
    }

    /**
     * Set the label array to a given array of strings.
     *
     * @param labels an array of labels
     */
    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    /**
     * Returns the labels in an array of strings.
     *
     * @return an array of strings
     */
    public String[] getLabels() {
        return this.labels;
    }

    /**
     *
     * The following code has to be rewritten for TriMat Class!
     *
     *
     */
    /**
     * Take this matrix and multily it, according to mathematical matrix
     * multiplicatin, with another matrix.
     *
     * @param mat the matrix to multiply with
     * @return the resulting matrix
     */
    public Mat matrixMultiplication(Mat mat) {
        Mat result_mat = new Mat(mat.getFirstDimSize(), mat.getSecondDimSize());
        double[][] result = result_mat.getLonleyMatrix();
        for (int i = 0; i < mat.getFirstDimSize(); i++) {
            for (int j = 0; j < mat.getSecondDimSize(); j++) {
                for (int k = 0; k < mat.getFirstDimSize(); k++) {
                    result[i][j] += getElement(i, k) * mat.getElement(k, j);
                }
            }
        }
        return result_mat;
    }

    /**
     * Multiplies every matrix entry with a scalar.
     *
     * @param scalar to multiply with
     * @return the resulting matrix
     */
    public TriMat scalarMultiplication(double scalar) {
        TriMat result = new TriMat(this.getSize());
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                result.setElement(i, j, scalar * this.getElement(i, j));
            }
        }
        System.out.println(Arrays.deepToString(result.matrix));
        return result;
    }

    /**
     * Adds a scalar to every matrix entry.
     *
     * @param scalar to add
     * @return the resulting matrix
     */
    public TriMat scalarAddition(double scalar) {
        TriMat result = new TriMat(this.getSize());
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                result.setElement(i, j, this.getElement(i, j) + scalar);
            }
        }
        System.out.println(Arrays.deepToString(result.matrix));
        return result;
    }

    /**
     * Returns the transposed matrix.
     *
     * @return transposed matrix
     */
    public TriMat transpose() {
        TriMat result = new TriMat(this.getSize());
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                result.setElement(j, i, this.getElement(i, j));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j <= i; j++) {
                str.append(this.matrix[i][j]).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * Check if two matrices are equal regarding dimension and matrix entries -
     * labels are ignored.
     *
     * @param other
     * @return wether matrix entries are equal or not
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof TriMat)) {
            return false;
        }
        TriMat othermat = (TriMat) other;
        if (othermat.getSize() != this.getSize()) {
            return false;
        }
        for (int i = 0; i < othermat.getSize(); i++) {
            for (int j = 0; j < othermat.getSize(); j++) {
                if (othermat.getElement(i, j) != this.getElement(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return auto-generated hashcode.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.deepHashCode(this.matrix);
        return hash;
    }

}
