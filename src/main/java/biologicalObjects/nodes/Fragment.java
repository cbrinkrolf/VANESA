package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Fragment extends BiologicalNodeAbstract {
    public Fragment(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.fragment);
        attributeSetter(this.getClass().getSimpleName(), this);
    }
}