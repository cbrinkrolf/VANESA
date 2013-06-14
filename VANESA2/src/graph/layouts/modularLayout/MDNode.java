/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import edu.uci.ics.jung.visualization.Coordinates;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Besitzer
 */
public class MDNode {

    public static final int PRIME = 0,  PARALLEL = 1,  SERIES = 2,  TREE = 4,  CUT = 8,  CLUSTER = 16,  TREE_ROOT = 32;
    private int nodeType;
    private MDNode parent;
    private Set<MDNode> children;
    private Set<MDNode> leftNeighbours;
    private Set<MDNode> rightNeighbours;
    public static int LEFT_MARK = 1,  RIGHT_MARK = 2;
    private int mark;
    private Component component;
    private Object id;
    private int order;
    private static int idCounter;
    private int partition;
    private Set<MDNode> connections;//only used for prime node
    private Coordinates center;
    private Rectangle boundary;
//    private boolean splitable;
    private Point paintOffset;
    private Coordinates coordinates;
    private String compartment;
    private boolean vertex;
//    private float treeRadius;
//    private float[] treeCenter;
//    private MDNode anchor;
    private ForceItem forceItem;
    private boolean locked;
    private Set<MDNode> subTrees;
    private Map<MDNode, Set<MDNode>> edges;

    public MDNode() {
        this(PRIME);
    }

    public MDNode(int type) {
        this.nodeType = type;
        this.id = idCounter++;
//        this.splitable = (this.nodeType != PRIME);
    }

    public MDNode(MDNode parent) {
        this(parent.getNodeType());
        this.setParent(parent);
        this.setMark(parent.getMark());
//        this.setSplitable(parent.splitable);
        this.partition = parent.getPartition();
    }

    public MDNode(Object _id) {
        this.id = _id;
        this.nodeType = PRIME;
    }

    public void getAllVertices(Collection<MDNode> vertices) {
        if (isVertex()) {
            vertices.add(this);
        }
        if (this.children != null) {
            for (MDNode node : children) {
                node.getAllVertices(vertices);
            }
        }
    }

    public void findAllVerticesInTree(Collection<MDNode> vertices) {
        if (this.getSubTrees() != null) {
            vertices.addAll(this.getSubTrees());
            for (MDNode n : this.getSubTrees()) {
                n.findAllVerticesInTree(vertices);
            }
        }
    }

    public List<MDNode> getAllLeaves() {
        List<MDNode> leaves = new ArrayList();
        if (this.children != null) {
            for (MDNode node : children) {
                List<MDNode> subleaves = node.getAllLeaves();
                if (subleaves.size() > 0) {
                    leaves.addAll(subleaves);
                } else {
                    leaves.add(node);
                }
            }
        }
        return leaves;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public void addNodeType(int _nodeType) {
        this.nodeType |= _nodeType;
    }
 

    public MDNode getParent() {
        return parent;
    }

    public void setParent(MDNode parent) {
        this.parent = parent;
    }

    MDNode getRandeomLeaf() {
        if (children == null) {
            return this;
        } else {
            return children.iterator().next().getRandeomLeaf();
        }
    }

    public Set<MDNode> getChildren() {
        return children;
    }

    public void setChildren(Set<MDNode> children) {
        this.children = children;
        for (MDNode child : children) {
            child.setParent(this);
        }
    }

    public void addChildrenInTree(MDNode treeRoot) {
        if ((treeRoot.getNodeType() & MDNode.TREE) > 0) {
            Set<MDNode> nodes = new HashSet();
            treeRoot.findAllVerticesInTree(nodes);
            for (MDNode n : nodes) {
                n.setParent(this);
            }
            if (this.children == null) {
                this.children = new LinkedHashSet();
            }
            this.children.addAll(nodes);
        }
    }

    public void addChild(MDNode child) {
        child.setParent(this);
        if (children == null) {
            children = new LinkedHashSet<MDNode>();
        }
        if ((this.nodeType == MDNode.PARALLEL || this.nodeType == MDNode.SERIES) &&
                this.nodeType == child.getNodeType() &&
                child.getChildren() != null) {
            Set<MDNode> grandchildren = child.getChildren();
            for (MDNode grandchild : grandchildren) {
                addChild(grandchild);
            }
        } else {
            children.add(child);
        }
        if ((child.getNodeType() & MDNode.TREE) > 0) {
            addChildrenInTree(child);
        }
    }

    public void removeChild(MDNode child) {
        if (children.remove(child)) {
            child.setParent(null);
//            if (children.size() == 1) {
//                this.setChildren(children.iterator().next().getChildren());
//            }
        }
    }
//    public abstract boolean incident(V vertex);
    public Set<MDNode> getLeftNeighbours() {
        return leftNeighbours;
    }

    public boolean addLeftNeighbours(MDNode e) {
        if (leftNeighbours == null) {
            leftNeighbours = new LinkedHashSet();
        }
        return leftNeighbours.add(e);
    }

    public void setLeftNeighbours(Set<MDNode> incidentNodes) {
        this.leftNeighbours = incidentNodes;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark |= mark;
        if (parent != null && ((parent.getMark() & mark) == 0)) {
            parent.setMark(mark);
        }
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Object getId() {
        return id;
    }

    public void setId(Comparable id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (this.children == null) {
            return id.toString();
        } else {
            StringBuffer buffer = new StringBuffer();
            switch (this.nodeType) {
                case PRIME:
                    buffer.append("prime:");
                    break;
                case PARALLEL:
                    buffer.append("parallel:");
                    break;
                case SERIES:
                    buffer.append("series:");
                    break;
            }
            buffer.append(children);
//            if (this.nodeType == PRIME) {
//                buffer.append("\n--->connections:<");
//                for(MDNode child:children){
//                    buffer.append(child);
//                    buffer.append("->");
//                    buffer.append(child.getConnections());
//                }
//                buffer.append(">");
//            }
            return buffer.toString();
        }
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        Collection<MDNode> allLeaves = this.getAllLeaves();
        for (MDNode leaf : allLeaves) {
            leaf.setOrder(order);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MDNode other = (MDNode) obj;
        return other.getId().equals(id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
        if (this.children != null) {
            for (MDNode child : children) {
                child.setPartition(partition);
            }
        }
    }

    public Set<MDNode> getRightNeighbours() {
        return rightNeighbours;
    }

    public void setRightNeighbours(Set<MDNode> rightNeighbours) {
        this.rightNeighbours = rightNeighbours;
    }

    public boolean addRightNeighbours(MDNode e) {
        if (rightNeighbours == null) {
            rightNeighbours = new LinkedHashSet();
        }
        return rightNeighbours.add(e);
    }

    public Set<MDNode> getConnections() {
        return connections;
    }

    public void setConnections(Set<MDNode> connections) {
        this.connections = connections;
    }

    public void addConnection(MDNode n) {
        if (connections == null) {
            connections = new LinkedHashSet();
        }
        connections.add(n);
    }

    public Coordinates getCenter() {
        if (center == null) {
            center = new Coordinates(0, 0);
        }
        return center;
    }

    public void setCenter(Coordinates center) {
        this.center = center;
    }

//    public void translate(int dx, int dy) {
//        this.getCenter().add(dx, dy);
////        this.getBoundary().translate(dx, dy);
//    }
    public void translate(float dx, float dy) {
        this.getCenter().add(dx, dy);
//        this.getBoundary().translate(dx, dy);
    }

    public void setLocation(int x, int y) {
        this.getCenter().setLocation(x, y);
//        this.getBoundary().setLocation(x, y);
    }

    public void setLocation(double x, double y) {
        this.getCenter().setLocation(x, y);
//        this.getBoundary().setRect(x, y,
//                this.getBoundary().getWidth(),
//                this.getBoundary().getHeight());
    }

    public void setLocation(Point p) {
        this.getCenter().setLocation(p);
//        this.getBoundary().setLocation(p);
    }
//    public Dimension getSize() {
//        return this.getBoundary().getSize();
//    }
    public Rectangle getBoundary() {
        return boundary;
    }

    public int getWidth() {
        return boundary == null ? 0 : boundary.width;
    }

    public int getHeight() {
        return boundary == null ? 0 : boundary.height;
    }

    public void includeNode(MDNode child) {
        Rectangle rect = child.getBoundary();
        Coordinates coord = child.getCenter();
        int x = Math.round(coord.x);
        int y = Math.round(coord.y);
        if (rect == null) {
            rect = new Rectangle(x, y, 0, 0);
        } else {
            rect = new Rectangle(rect);
        }
        rect.setLocation(
                Math.round(x - rect.width / 2.0f),
                Math.round(y - rect.height / 2.0f));
        if (this.boundary == null) {
            boundary = new Rectangle(rect);
        } else {
            this.boundary.add(rect);
        }
    }

    public void setBoundary(Rectangle boundary) {
        this.boundary = boundary;
    }

//    public void moveBounds() {
//        if (this.boundary != null) {
//            this.boundary.translate(
//                    this.getCenter().x,
//                    this.getCenter().y);
//        }
//    }
    public void move(float dx, float dy) {
        this.translate(dx, dy);
        if (this.children != null) {
            for (MDNode n : this.children) {
                if (this.getNodeType() == MDNode.CLUSTER && (n.getNodeType() & MDNode.CUT) > 0) {
                    continue;
                }
                n.move(dx, dy);
            }
        }
    }

    public void reCalcBounds() {
        float x1 = Float.MAX_VALUE, y1 = Float.MAX_VALUE;
        float x2 = Float.MIN_VALUE, y2 = Float.MIN_VALUE;
        if (this.getChildren() != null) {
            for (MDNode node : children) {
                if (this.getNodeType() == MDNode.CLUSTER && (node.getNodeType() & MDNode.CUT) > 0) {
                    continue;
                }
                Rectangle rect = node.getBoundary();
                Coordinates coord = node.getCenter();
                float rw=0,rh=0;
                if(rect!=null){
                    rw=rect.width/2f;
                    rh=rect.height/2f;
                }
                x1 = Math.min(x1, coord.x - rw);
                y1 = Math.min(y1, coord.y - rh);
                x2 = Math.max(x2, coord.x + rw);
                y2 = Math.max(y2, coord.y + rh);
            }
            float cx = (x1 + x2) / 2f, cy = (y1 + y2) / 2f;
            if (this.boundary == null) {
                boundary = new Rectangle();
            }
            boundary.setBounds(
                    Math.round(x1),
                    Math.round(y1),
                    Math.round(x2 - x1),
                    Math.round(y2 - y1));
            setLocation(cx, cy);
        }
//        
//        if (this.boundary != null) {
//            float cx = this.getCenter().x,
//                    cy = this.getCenter().y;
//            float dx = this.boundary.x + this.boundary.width / 2 - cx;
//            float dy = this.boundary.y + this.boundary.height / 2 - cy;
//            this.boundary.setLocation(
//                    Math.round(cx - boundary.width / 2),
//                    Math.round(cx - boundary.height / 2));
//            if (this.children != null) {
//                for (MDNode n : children) {
//                    n.translate(-dx, -dy);
//                }
//            }
//        }
    }

    public Point getPaintOffset() {
        return paintOffset;
    }

    public void setPaintOffset(Point paintOffset) {
        this.paintOffset = paintOffset;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void updateCoordinatesAndBounds() {
        if (coordinates == null) {
            coordinates = new Coordinates();
        }
        coordinates.setLocation(this.getCenter().getX(), this.getCenter().getY());
        if (this.boundary != null) {
            this.boundary.setLocation(
                    Math.round(coordinates.x - boundary.width / 2.0f),
                    Math.round(coordinates.y - boundary.height / 2.0f));
        }
    }

    public void calcCoordinates() {
        if (coordinates == null) {
            coordinates = new Coordinates();
        }
        coordinates.setLocation(this.getCenter().getX(), this.getCenter().getY());
//        if (parent != null && parent.getCoordinates()!=null
//                //                && (parent.getNodeType()&MDNode.TREE)==0
//                ) {
//            coordinates.setLocation(parent.getCoordinates().x, parent.getCoordinates().y);
//        } else {
//            coordinates.setLocation(0, 0);
//        }
//        coordinates.add(this.getCenter().getX(), this.getCenter().getY());
        if (this.boundary != null) {
            this.boundary.setLocation(
                    Math.round(coordinates.x - boundary.width / 2.0f),
                    Math.round(coordinates.y - boundary.height / 2.0f));
            if (this.isVertex()) {
                coordinates.add(-boundary.getWidth() / 2.0, -boundary.getHeight() / 2.0);
                coordinates.add(getPaintOffset().getX(), getPaintOffset().getY());
            }

        }
//        System.out.println(this+":::"+this.coordinates);
        if (children != null) {
            for (MDNode n : children) {
                n.calcCoordinates();
            }
        }
    }

    /**
     * @return the compartment
     */
    public String getCompartment() {
        return compartment;
    }

    /**
     * @param compartment the compartment to set
     */
    public void setCompartment(String compartment) {
        this.compartment = compartment;
    }

    /**
     * @return the vertex
     */
    public boolean isVertex() {
        return vertex;
    }

    /**
     * @param vertex the vertex to set
     */
    public void setVertex(boolean vertex) {
        this.vertex = vertex;
    }

   
  
    /**
     * @return the forceItem
     */
    public ForceItem getForceItem() {
        return forceItem;
    }

    /**
     * @param forceItem the forceItem to set
     */
    public void setForceItem(ForceItem forceItem) {
        this.forceItem = forceItem;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Map<MDNode, Set<MDNode>> getEdges() {
        return edges;
    }

    public void setEdges(Map<MDNode, Set<MDNode>> edges) {
        this.edges = edges;
    }

    public Set<MDNode> getSubTrees() {
        return subTrees;
    }

    public void setSubTrees(Set<MDNode> subTrees) {
        this.subTrees = subTrees;
    }
}
