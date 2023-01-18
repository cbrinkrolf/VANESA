package petriNet;
/**
 * Eine Funktionen bekommt eine Liste von Argumenten und berechnet etwas
 * daraus, eine typische Funktion ist "max", welche das Maximum aus mehreren
 * Zahlen berechnet.
 */
public interface IFunction {
    /**
     * ßberprüft ob diese Funktion die angegebene Anzahl von Argumenten
     * verarbeiten kann.
     * @param count Die Anzahl Argumente
     * @return <code>true</code> falls <code>count</code> Argumente verarbeitet
     * werden können.
     */
    public boolean validNrOfArguments( int count );
    
    /**
     * Berechnet diese Funktion.
     * @param values die Argumente, die Länge dieses Arrays wurde mit 
     * {@link #validNrOfArguments(int)} getestet.
     * @return irgendeine Berechnung
     */
    public double calculate( double[] values );
}