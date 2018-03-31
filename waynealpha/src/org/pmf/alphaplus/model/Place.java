package org.pmf.alphaplus.model;
import org.pmf.alphaplus.Alpha;

import java.util.ArrayList;

public class Place extends PetrinetNode {

    public ArrayList<Integer> leftTransition;
    public ArrayList<Integer> rightTransition;
    private ArrayList<Transition> transitions;//what for? --TO record transition names

    public Place(ArrayList<Transition> transitions) {
        leftTransition = new ArrayList<>();
        rightTransition = new ArrayList<>();
        this.transitions = transitions;
    }

    public Place(int a, int b, ArrayList<Transition> transitions) {
        leftTransition = new ArrayList<>();
        rightTransition = new ArrayList<>();
        leftTransition.add(a);
        rightTransition.add(b);
        this.transitions = transitions;
    }
    public Place(ArrayList<Integer> lefttransitions, ArrayList<Integer> righttransitions, ArrayList<Transition> transitions) {
        leftTransition = lefttransitions;
        rightTransition = righttransitions;
        this.transitions = transitions;
    }

    @Override
    public String toString() {
        String str = "({";
        for (Integer i : leftTransition) {
            str += transitions.get(i).name + ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "},{";
        for (Integer i : rightTransition) {
            str += transitions.get(i).name + ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "})";
        return str;
    }
    
    public boolean equals(Place p){
    	if(this.leftTransition.size()!=p.leftTransition.size()) return false;
    	if(this.rightTransition.size()!=p.rightTransition.size()) return false;

    	  for (Integer i : this.leftTransition) {
    	        if (!p.leftTransition.contains(i)) {
    	            return false;
    	        }
    	    }
    	    for (Integer i : this.rightTransition) {
    	        if (!p.rightTransition.contains(i)) {
    	            return false;
    	        }
    	    }
    	    return true;
    }
    public boolean canMerge(int a, int b, int[][] footprint) {
        // 如果完全一样则不算可合并
        if (leftTransition.size() == 1 && leftTransition.get(0) == a && rightTransition.size() == 1 &&
                rightTransition.get(0) == b) {
            return false;
        }

        for (Integer i : leftTransition) {
            if (footprint[a][i] != Alpha.FOOTPRINT_NONE || footprint[i][b] != Alpha.FOOTPRINT_FRONT) {
                return false;
            }
        }

        for (Integer i : rightTransition) {
            if (footprint[b][i] != Alpha.FOOTPRINT_NONE || footprint[a][i] != Alpha.FOOTPRINT_FRONT) {
                return false;
            }
        }

        return true;
    }
public boolean canContain(Place place) {
    for (Integer i : place.leftTransition) {
        if (!leftTransition.contains(i)) {
            return false;
        }
    }
    for (Integer i : place.rightTransition) {
        if (!rightTransition.contains(i)) {
            return false;
        }
    }
    return true;
}
}
