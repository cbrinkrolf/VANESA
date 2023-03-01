package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Protein extends BiologicalNodeAbstract {
    private String aaSequence = "";

    public Protein(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.protein);
        attributeSetter(this.getClass().getSimpleName(), this);
    }

    public String getAaSequence() {
        return aaSequence;
    }

    public void setAaSequence(String aaSequence) {
        this.aaSequence = aaSequence;
    }
}
