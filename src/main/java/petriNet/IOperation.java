package petriNet;
/**
* Eine Operation berechnet einen Teil einer Formel.
* Eine typische Operation ist z.B. die Addition "+", sie zählt zwei
* Zahlen zusammen.
*/
public interface IOperation{
   /**
    * Je höhe die Priorität einer Operation ist, desto früher wird
    * sie berechnet.
    * @return Die Priorität
    */
   public int getPriority();
   
   /**
    * Führt die Berechnung durch, welche von dieser Operation dargestellt
    * wird.
    * @param a Das erste Argument
    * @param b Das zweite Argument
    * @return Das Ergebnis dieser Operation
    */
   public double calculate( double a, double b );
}