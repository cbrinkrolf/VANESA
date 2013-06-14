/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Besitzer
 */
public class Component implements Comparable<Component>{

    private Set<MDNode> trees=new LinkedHashSet();
    private boolean coComponent;
    private Set<MDNode> leaves=new LinkedHashSet();
    private int order;
    private int partition = -1;
    private int id;
    private static int idCounter;

    public Component() {
        id = idCounter++;
    }

    public Component(MDNode tree, boolean isComponent) {
//        List<MDNode> all = tree.getAllLeaves();
//        if (all == null || all.size() == 0) {
//            this.addLeaf(tree);
//        } else {
//            this.setLeaves(all);
//        }
        this.trees.add(tree);
        tree.setComponent(this);
        this.coComponent = isComponent;
        id = idCounter++;
    }

//    public boolean removeLeaf(MDNode o) {
//        o.setComponent(null);
//        return leaves.remove(o);
//    }

//    public boolean addLeaf(MDNode e) {
//        e.setComponent(this);      
//        return leaves.add(e);
//    }

    public Set<MDNode> getTrees() {
        return trees;
    }

    public void setTrees(Set<MDNode> trees) {
        this.trees = trees;
    }

    public boolean removeTree(MDNode o) {
        return trees.remove(o);
    }

    public boolean addTree(MDNode e) {
        return trees.add(e);
    }

    public boolean isCoComponent() {
        return coComponent;
    }

    public void setCoComponent(boolean coComponent) {
        this.coComponent = coComponent;
    }

    public MDNode assemble() {
        if (trees.size() > 1) {
            MDNode node = new MDNode();
            if (this.isCoComponent()) {
                node.setNodeType(MDNode.PARALLEL);
            } else {
                node.setNodeType(MDNode.SERIES);
            }
            for (MDNode tree : trees) {
                node.addChild(tree);
            }
            node.setComponent(this);
            return node;
        } else {
            return trees.iterator().next();
        }
    }

    public Set<MDNode> getLeaves() {
        return leaves;
    }    
        public MDNode getCommonParent() {
        MDNode p = null;
        if (this.trees != null) {
            Set<MDNode> parents = new HashSet();
            for (MDNode n : trees) {
                parents.add(n.getParent());
            }
            if(parents.size()==1)return parents.iterator().next();
        }
        return p;
    }
    public void refresh(){
        leaves.clear();
        for(MDNode node:trees){
            Collection<MDNode> all= node.getAllLeaves();
            if(all==null||all.size()==0)leaves.add(node);
            else leaves.addAll(all);
        }
        for(MDNode leaf:leaves)leaf.setComponent(this);
    }
    
//    public void setLeaves(List<MDNode> leaves) {
//        this.leaves = leaves;
////        for (MDNode leaf : leaves) {
////            leaf.setComponent(this);
////        }
////        refresh();
//    }

//    public void findAllTrees(Map<MDNode, Set<MDNode>> treesMap, MDNode node) {
////        System.out.println("findAlltrees with node:"+node+"\ntreesMap+"+treesMap);
//        MDNode parent = node.getParent();
//        if (parent == null) {
//            treesMap.put(node, null);
//        } else {
//            Set<MDNode> subtrees = treesMap.get(parent);
//            if (subtrees == null) {
//                subtrees = new HashSet();
//                treesMap.put(parent, subtrees);
//            }
//            subtrees.add(node);
//            if (subtrees.size() == parent.getChildren().size()) {
//                treesMap.remove(parent);
//                if (parent.getParent() == null) {
//                    treesMap.put(parent, null);
//                } else {
//                    findAllTrees(treesMap, parent);
//                }
//            }
//        }
////        System.out.println("after findAlltrees with node:"+node+"\ntreesMap+"+treesMap);
//    }
//
//    public void refresh() {
////        System.out.println("refresh leaves:"+leaves);
//        if (this.leaves != null) {
//            Set<MDNode> roots = new HashSet();
//            Map<MDNode, Set<MDNode>> treesMap = new HashMap();
//            for (MDNode leaf : leaves) {
//                findAllTrees(treesMap, leaf);
//            }
//            for (Map.Entry<MDNode, Set<MDNode>> entry : treesMap.entrySet()) {
//                if (entry.getValue() == null) {
//                    roots.add(entry.getKey());
//                } else {
//                    roots.addAll(entry.getValue());
//                }
//            }
//            this.trees = new ArrayList(roots);
//        } else {
//            this.trees = null;
//        }
//    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(
                this.isCoComponent() ? "coComponent:" : "component:");
        buffer.append(order);
        buffer.append(this.leaves);
        return buffer.toString();
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Component other = (Component) obj;
        return this.getId()==other.getId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.getId();
        return hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int compareTo(Component o) {
        return this.order-o.order;
    }
    
}
