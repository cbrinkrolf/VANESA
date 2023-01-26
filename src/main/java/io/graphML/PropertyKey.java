package io.graphML;

public class PropertyKey {
    public final String id;
    public final String forType;
    public final String attributeName;
    public final String attributeType;
    public final String attributeList;

    PropertyKey(String id, String forType, String attributeName, String attributeType, String attributeList) {
        this.id = id;
        this.forType = forType;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeList = attributeList;
    }
}
