/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.datastructures;

/**
 * Vector offers a container to store a data vector and allows the basic
 * mathematical operations you can perform on a vector.
 *
 * @author philipp
 */
public class Vector {

    private String label;
    private double[] vec;

    /**
     * Initialize a vector with a given number of elements.
     *
     * @param size the number of elements the vector should hold
     */
    public Vector(int size) {
        this.vec = new double[size];
    }

    /**
     * Initialize a vector with given data array and label.
     *
     * @param data an array containing the data
     * @param label a string, the label
     */
    public Vector(double[] data, String label) {
        this.vec = data;
        this.label = label;
    }

    /**
     * Initialize a vector with a given number of elements.
     *
     * @param size the number of elements the vector should hold
     * @param init the initial value
     */
    public Vector(int size, double init) {
        this.vec = new double[size];
        for (int i = 0; i < size; i++) {
            this.vec[i] = init;
        }
    }

    /**
     * Inititalize a vector with a given number of elements and label it.
     *
     * @param size the number of elements the vector should hold
     * @param label the label from this vector
     */
    public Vector(int size, String label) {
        this.vec = new double[size];
        this.label = label;
    }

    /**
     * Returns the specified element.
     *
     * @param i the index
     * @return the according element
     */
    public double getElement(int i) {
        return this.vec[i];
    }

    /**
     * Set an element specified by an index.
     *
     * @param i the index
     * @param e the element which will be stored
     */
    public void setElement(int i, double e) {
        this.vec[i] = e;
    }

    /**
     * Determines the size of this vector.
     *
     * @return the size
     */
    public int getSize() {
        return this.vec.length;
    }

    /**
     * Returns the label of this vector.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Set label of this vector.
     *
     * @param label the label
     */
    void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the lonely vector, meaning only the array containing the data.
     *
     * @return the array of dara
     */
    public double[] getLonleyVector() {
        return this.vec;
    }

    /**
     * Calculate the euclidean norm (or euklidean length) of a vector.
     *
     * @return length of the vector
     */
    public double euclideanNorm() {
        double sum = 0.0;
        for (double v : this.vec) {
            sum += (v * v);
        }
        return Math.sqrt(sum);
    }

    /**
     * Calculates the general p norm (or the length of this vector in a p vector
     * space) of a vector.
     *
     * @param p the norm
     * @return length of the vector
     */
    public double pNorm(int p) {
        double sum = 0.0;
        for (double v : this.vec) {
            sum += Math.pow(Math.abs(v), p);
        }
        return Math.pow(sum, 1.0 / (double) p);
    }

    /**
     * Multiplies a vector with a scalar and returns a new vecotr with the
     * result.
     *
     * @param s the scalar
     * @return a new vector with the result
     */
    public Vector multiply(double s) {
        double[] v = new double[this.getSize()];
        for (int i = 0; i < this.getSize(); i++) {
            v[i] = this.vec[i] * s;
        }
        return new Vector(v, this.label);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (this.label != null) {
            str.append("[").append(this.label).append(":");
        } else {
            str.append("[");
        }
        for (double v : this.vec) {
            str.append(v).append(",");
        }
        str.deleteCharAt(str.lastIndexOf(","));
        str.append("]");
        return str.toString();
    }

}
