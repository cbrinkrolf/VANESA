/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.datastructures;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Mat is a matrix container with all functions needed to perform the necessary
 * mathematical operations from linear algebra to compute smacof.
 *
 * @author philipp
 */
public class Mat {

    private String[] labels;
    private double[][] matrix;

    /**
     * Initialize a new dim1 * dim2 matrix with given size.
     *
     * @param dim1 size of first dimension
     * @param dim2 size of second dimension
     */
    public Mat(int dim1, int dim2) {
        this.matrix = new double[dim1][dim2];
    }

    /**
     * Initialize a new dim1 * dim2 matrix with given size and given value.
     *
     * @param dim1 size of first dimension
     * @param dim2 size of second dimension
     * @param value the initial value
     */
    public Mat(int dim1, int dim2, double value) {
        this.matrix = new double[dim1][dim2];
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                this.matrix[i][j] = value;
            }
        }
    }

    /**
     * Initialize a matrix with content of a given TriMat, the result is a
     * symmetric matrix.
     *
     * @param trimat the triangularmatrix
     */
    public Mat(TriMat trimat) {
        this.matrix = new double[trimat.getSize()][trimat.getSize()];
        this.labels = trimat.getLabels();
        for (int i = 0; i < trimat.getSize(); i++) {
            for (int j = 0; j <= i; j++) {
                this.matrix[i][j] = trimat.getElement(i, j);
            }
        }
        // use symmetrie to complete the matrix, and complete it efficient!!!
        // dont iterate over all elements, only the missing ones
        for (int i = trimat.getSize() - 1; i >= 0; i--) {
            for (int j = trimat.getSize() - 1; j >= i; j--) {
                this.matrix[i][j] = this.matrix[j][i];
            }
        }
    }

    /**
     * Initialize a matrix with an array of vectors.
     *
     * @param vecbundle the array of vectors
     */
    public Mat(Vector[] vecbundle) {
        this.matrix = new double[vecbundle.length][vecbundle[0].getSize()];
        this.labels = new String[vecbundle.length];
        for (int i = 0; i < vecbundle.length; i++) {
            this.matrix[i] = vecbundle[i].getLonleyVector();
            this.labels[i] = vecbundle[i].getLabel();
        }
    }

    /**
     * Initialize a matrix with an 2 dimensional array.
     *
     * @param matrix
     */
    public Mat(double[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Returns the size of the first dimension of the matrix.
     *
     * @return the size of first dimension
     */
    public int getFirstDimSize() {
        return this.matrix.length;
    }

    /**
     * Returns the size of the second dimension of the matrix.
     *
     * @return the size of second dimension
     */
    public int getSecondDimSize() {
        return this.matrix[0].length;
    }

    /**
     * Sets the given element to the specified matrix position.
     *
     * @param i index
     * @param j index
     * @param e element
     */
    public void setElement(int i, int j, double e) {
        this.matrix[i][j] = e;
    }

    /**
     * Returns the element specified by the indices.
     *
     * @param i index
     * @param j index
     * @return element
     */
    public double getElement(int i, int j) {
        return this.matrix[i][j];
    }

    /**
     * Returns a single row from the triangular matrix.
     *
     * @param i row
     * @return the row
     */
    public double[] getRow(int i) {
        double[] tmp = new double[this.matrix.length];
        for (int j = 0; j < this.matrix.length; j++) {
            tmp[j] = this.matrix[j][i];
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
        return this.matrix[i];
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
     * Set the label array to a given array of strings.
     *
     * @param labels an array of labels
     */
    public void setLabel(String[] labels) {
        this.labels = labels;
    }

    /**
     * Returns the labels of this matrix as an array of strings.
     *
     * @return labels as string array
     */
    public String[] getLabels() {
        return this.labels;
    }

    /**
     * Returns the plain data matrix as an two dimensional array of doubles.
     *
     * @return a 2D array of doubles
     */
    public double[][] getLonleyMatrix() {
        return this.matrix;
    }

    /**
     * Computes the trace (sum of diagonal elements) of this matrix.
     *
     * @return double the trace
     */
    public double trace() {
        if (getFirstDimSize() == getSecondDimSize()) {
            double trace = 0.0;
            for (int i = 0; i < getFirstDimSize(); i++) {
                trace += this.matrix[i][i];
            }
            return trace;
        } else {
            throw new UnsupportedOperationException("Invalid operation for non quadratic matrices.");
        }
    }

    /**
     * Take this matrix and multily it, according to mathematical matrix
     * multiplicatin, with another matrix.
     *
     * @param mat the matrix to multiply with
     * @return the resulting matrix
     */
    public Mat matrixMultiplication(Mat mat) {
        // TODO check if matrix dimensions are right!!!
        Mat result_mat = new Mat(getFirstDimSize(), mat.getSecondDimSize());
        double[][] result = result_mat.getLonleyMatrix();
        for (int i = 0; i < this.getFirstDimSize(); i++) {
            for (int j = 0; j < mat.getSecondDimSize(); j++) {
                for (int k = 0; k < this.getSecondDimSize(); k++) {
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
    public Mat scalarMultiplication(double scalar) {
        Mat result_mat = new Mat(getFirstDimSize(), getSecondDimSize());
        double[][] result = result_mat.getLonleyMatrix();
        for (int i = 0; i < getFirstDimSize(); i++) {
            for (int j = 0; j < getSecondDimSize(); j++) {
                result[i][j] = getElement(i, j) * scalar;
            }
        }
        //System.out.println(Arrays.deepToString(result));
        return result_mat;
    }

    /**
     * Adds a scalar to every matrix entry.
     *
     * @param scalar to add
     * @return the resulting matrix
     */
    public Mat scalarAddition(double scalar) {
        Mat result_mat = new Mat(getFirstDimSize(), getSecondDimSize());
        double[][] result = result_mat.getLonleyMatrix();
        for (int i = 0; i < getFirstDimSize(); i++) {
            for (int j = 0; j < getSecondDimSize(); j++) {
                result[i][j] = getElement(i, j) + scalar;
            }
        }
        System.out.println(Arrays.deepToString(result));
        return result_mat;
    }

    /**
     * Compute the Moore-Penrose-Inverse (pseudo inverse) of a this matrix.
     *
     * @return matrix representing moore-penrose-inverse
     */
    public Mat moorePenroseInverse() {
        if (this.getFirstDimSize() != this.getSecondDimSize()) {
            throw new IllegalArgumentException("Watch moorePenroseInverse() function!");
        }
       // RealMatrix m = MatrixUtils.createRealMatrix(this.getLonleyMatrix());
       // RealMatrix pim = MatrixUtils.inverse(m); // TODO MF: correct? not returned anyway
        //Jama.Matrix pim = PseudoInverse.pseudoInverse(m, this.getFirstDimSize() - 1);
        // inverse() returns the inverse of a matrix if possible, pseudo-inverse otherwise!
        //Mat mat = new Mat(pim.getData());
//        return mat;
        return null;
    }

    /**
     * Compute the inverse (pseudo inverse) of a this matrix.
     *
     * @return matrix representing moore-penrose-inverse
     */
    public Mat inverse() {
        RealMatrix m = MatrixUtils.createRealMatrix(this.getLonleyMatrix());
        // inverse() returns the inverse of a matrix if possible, pseudo-inverse otherwise!
        Mat mat = new Mat(MatrixUtils.inverse(m).getData());
        return mat;
    }

    /**
     * Initializes the matrix with random numbers between 0 and 1.
     */
    public void initRand() {
        for (int i = 0; i < this.getFirstDimSize(); i++) {
            for (int j = 0; j < this.getSecondDimSize(); j++) {
                this.matrix[i][j] = Math.random();
            }
        }
    }

    /**
     * Returns the transposed matrix.
     *
     * @return transposed matrix.
     */
    public Mat transpose() {
        Mat result = new Mat(this.getSecondDimSize(), this.getFirstDimSize());
        for (int i = 0; i < this.getFirstDimSize(); i++) {
            for (int j = 0; j < this.getSecondDimSize(); j++) {
                result.matrix[j][i] = this.matrix[i][j];
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.matrix.length; i++) {
            if (labels != null) {
                str.append(this.labels[i]).append(":");
            }
            for (int j = 0; j < this.matrix[i].length; j++) {
                str.append(this.matrix[i][j]).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * Check if two matrices are equal regarding dimensions and matrix entries -
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
        if (!(other instanceof Mat)) {
            return false;
        }
        Mat othermat = (Mat) other;
        if (othermat.getFirstDimSize() != this.getFirstDimSize()
                || othermat.getSecondDimSize() != this.getSecondDimSize()) {
            return false;
        }
        for (int i = 0; i < othermat.getFirstDimSize(); i++) {
            for (int j = 0; j < othermat.getSecondDimSize(); j++) {
                if (othermat.matrix[i][j] != this.matrix[i][j]) {
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
        int hash = 7;
        hash = 59 * hash + Arrays.deepHashCode(this.matrix);
        return hash;
    }
}
