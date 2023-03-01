package petriNet;

/**
 * A function calculates some value from a list of values. For example the maximum of a list.
 */
public interface IFunction {
    /**
     * Checks whether the correct number of arguments are present.
     */
    boolean validNrOfArguments(int count);

    /**
     * Calculates the function result from the provided list.
     */
    double calculate(double[] values);
}