/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Besitzer
 */
public class Group {

    Set<MDNode> children;
    MDNode parent;
    
    public Group(MDNode parent) {
        this(parent, new LinkedHashSet());
    }

    public Group(MDNode parent, Set<MDNode> children) {
        this.parent = parent;
        this.children = children;
    }

    public Set<MDNode> getChildren() {
        return children;
    }

    public MDNode getParent() {
        return parent;
    }

    public void addChild(MDNode node) {
        children.add(node);
    }

    public boolean isAll() {
        return parent.getChildren().size() == children.size();
    }
    @Override
	public String toString(){
        return this.children.toString();
    }
}
