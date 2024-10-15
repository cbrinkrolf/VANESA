package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;

public class DiscreteTransition extends Transition {
    private String delay = "1";

    public DiscreteTransition(String label, String name) {
        super(label, name);
        setBiologicalElement(Elementdeclerations.discreteTransition);
        setColor(Color.WHITE);
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    @Override
    public List<String> getTransformationParameters() {
        List<String> list = super.getTransformationParameters();
        list.add("delay");
        return list;
    }
}
