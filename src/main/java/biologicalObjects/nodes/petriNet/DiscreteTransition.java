package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class DiscreteTransition extends Transition {
    private String delay = "1";

    public DiscreteTransition(String label, String name) {
        super(label, name);
		setDefaultShape(VertexShapes.getDiscreteTransitionShape());
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
