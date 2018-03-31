package org.pmf.alphaplus.ui;

public class NodeBean {
	//图的节点类型
	 public static final int TYPE_NODE_PLACE = 0;
	 public static final int TYPE_NODE_TRANSITION = 1;
	    
    private String name;
    private int type;

    public NodeBean(String name) {
        this.name = name;
        type =TYPE_NODE_TRANSITION;
    }

    public NodeBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public int getType() {
        return type;
    }

}
