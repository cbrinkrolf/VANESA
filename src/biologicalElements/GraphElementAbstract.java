package biologicalElements;

import graph.gui.Parameter;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;

public interface GraphElementAbstract{
	
	int getID();
	
	// should only be used when loading a file with a network
	void setID(int id) throws IDAlreadyExistException;

	void setID();

	public String getNetworklabel();

	public boolean hasFeatureEdge();

	public void hasFeatureEdge(boolean hasFeatureEdge);

	public boolean hasKEGGEdge();

	public void hasKEGGEdge(boolean hasKEGGEdge);

	public boolean hasReactionPairEdge();

	public void hasReactionPairEdge(boolean hasReactionPEdge);

	public boolean hasKEGGNode();

	public void hasKEGGNode(boolean hasKEGGNode);

	public String getName();

	public void setName(String name);

	public String getLabel();

	public void setLabel(String label);

	public String getDescription();

	public void setDescription(String description);

	public String getComments();

	public void setComments(String comments);

	public boolean isEdge();

	public boolean isVertex();

	public Color getColor();

	public void setColor(Color color);

	public String getBiologicalElement();

	public void setBiologicalElement(String biologicalElement);

	public Shape getShape();

	public void setShape(Shape shape);

	public boolean isReference();

	public void setReference(boolean isReference);

	public boolean isVisible();

	public void setVisible(boolean isVisible);

	public boolean hasBrendaNode();

	public void hasBrendaNode(boolean hasBrendaNode);

	public ArrayList<Parameter> getParameters();

	public void setParameters(ArrayList<Parameter> parameters);
	
	public HashSet<String> getLabelSet();

	public void setLabelSet(HashSet<String> labelSet);
	
	public void addLabel(String label);
	
	public void addLabel(HashSet<String> labels);
	
	public void resetAppearance();

}
