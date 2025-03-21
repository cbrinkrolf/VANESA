package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import graph.rendering.nodes.TransitionShape;

public class DiscreteTransition extends Transition {
    private String delay = "1";

    public DiscreteTransition(final String label, final String name, final Pathway pathway) {
        super(label, name, ElementDeclarations.discreteTransition, pathway);
        setDefaultNodeShape(new TransitionShape());
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
