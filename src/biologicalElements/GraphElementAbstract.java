package biologicalElements;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;

import graph.gui.Parameter;

public interface GraphElementAbstract{
	
	int getID();
	
	// should only be used when loading a file with a network
	void setID(int id, Pathway pw) throws IDAlreadyExistException;

	void setID(Pathway pw);

	public String getNetworklabel();

	public boolean isHasKEGGNode();

	public void setHasKEGGNode(boolean hasKEGGNode);

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

	public boolean isVisible();

	public void setVisible(boolean isVisible);

	public boolean isHasBrendaNode();

	public void setHasBrendaNode(boolean hasBrendaNode);

	public ArrayList<Parameter> getParameters();

	public void setParameters(ArrayList<Parameter> parameters);
	
	public HashSet<String> getLabelSet();

	public void setLabelSet(HashSet<String> labelSet);
	
	public void addLabel(String label);
	
	public void addLabel(HashSet<String> labels);
	
	public void resetAppearance();

}
