package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MIRNA extends RNA {
    public MIRNA(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.miRNA);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}
