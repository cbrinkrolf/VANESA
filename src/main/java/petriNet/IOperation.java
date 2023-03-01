package petriNet;

/**
 * An operation calculates a part of a formula such as a simple addition.
 */
public interface IOperation {
    /**
     * Get the priority of operation calculation. The higher, the earlier the operation is calculated.
     */
    int getPriority();

    double calculate(double a, double b);
}