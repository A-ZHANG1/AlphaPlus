package org.pmf.alphaplus.model;

import java.util.ArrayList;

public class Trace {
    public ArrayList<Integer> events;

    public Trace() {
        events = new ArrayList<>();
    }
    public void remove(Integer event){
    	this.events.remove(event);
    }
//    @Override
//    public boolean equals(Object trace) {
//        if (!trace.getClass().equals(getClass()) || ((Trace) trace).events.size() != events.size()) {
//            return false;
//        }
//
//        for (int i = 0; i < events.size(); i++) {
//            if (((Trace) trace).events.get(i) != events.get(i)) {
//                return false;
//            }
//        }
//        return true;
//    }
}
