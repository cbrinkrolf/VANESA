package biologicalElements;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;
import java.util.Set;

import graph.gui.Parameter;

public interface GraphElementAbstract {
	int getID();

	// should only be used when loading a file with a network
	void setID(int id, Pathway pw) throws IDAlreadyExistException;

	void setID(Pathway pw);

	String getNetworklabel();

	boolean hasKEGGNode();

	void setHasKEGGNode(boolean hasKEGGNode);

	String getName();

	void setName(String name);

	String getLabel();

	void setLabel(String label);

	String getDescription();

	void setDescription(String description);

	String getComments();

	void setComments(String comments);

	boolean isEdge();

	boolean isVertex();

	Color getColor();

	void setColor(Color color);

	String getBiologicalElement();

	Shape getShape();

	void setShape(Shape shape);

	boolean isVisible();

	void setVisible(boolean isVisible);

	boolean hasBrendaNode();

	void setHasBrendaNode(boolean hasBrendaNode);

	List<Parameter> getParameters();

	List<Parameter> getParametersSortedAlphabetically();

	void setParameters(List<Parameter> parameters);

	Set<String> getLabelSet();

	void setLabelSet(Set<String> labelSet);

	void addLabel(String label);

	void addLabel(Set<String> labels);

	void resetAppearance();
}
