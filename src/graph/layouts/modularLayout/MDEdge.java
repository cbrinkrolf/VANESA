/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

/**
 *
 * @author dao
 */
public class MDEdge {

    private MDNode first,  last;
    private float springCoefficient;
    private float springLength;
    private Spring springItem;

    public MDEdge(MDNode first, MDNode last) {
        this.first = first;
        this.last = last;
    }

    /**
     * @return the springCoefficient
     */
    public float getSpringCoefficient() {
        return springCoefficient;
    }

    /**
     * @param springCoefficient the springCoefficient to set
     */
    public void setSpringCoefficient(float springCoefficient) {
        this.springCoefficient = springCoefficient;
    }

    /**
     * @return the springLength
     */
    public float getSpringLength() {
        return springLength;
    }

    /**
     * @param springLength the springLength to set
     */
    public void setSpringLength(float springLength) {
        this.springLength = springLength;
    }

    /**
     * @return the first
     */
    public MDNode getFirst() {
        return first;
    }

    /**
     * @param first the first to set
     */
    public void setFirst(MDNode first) {
        this.first = first;
    }

    /**
     * @return the last
     */
    public MDNode getLast() {
        return last;
    }

    /**
     * @param last the last to set
     */
    public void setLast(MDNode last) {
        this.last = last;
    }

    /**
     * @return the springItem
     */
    public Spring getSpringItem() {
        return springItem;
    }

    /**
     * @param springItem the springItem to set
     */
    public void setSpringItem(Spring springItem) {
        this.springItem = springItem;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            MDEdge edge = (MDEdge) obj;
            return first==edge.first&&last==edge.last||
                    first==edge.last&&last==edge.first;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 17253494+first.hashCode()*17+last.hashCode()*17;
    }
}
