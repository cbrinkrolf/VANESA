package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Complex extends BiologicalNodeAbstract {
    public Complex(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.complex);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}
