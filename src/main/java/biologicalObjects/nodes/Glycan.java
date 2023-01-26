package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Glycan extends BiologicalNodeAbstract {
    public Glycan(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.glycan);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}
