package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class LNCRNA extends RNA {
    public LNCRNA(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.lncRNA);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}
