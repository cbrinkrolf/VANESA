package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Matrix extends BiologicalNodeAbstract {
    public Matrix(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.matrix);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}
