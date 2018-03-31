package org.pmf.alphaplus.model;

import java.util.*;

public class EasyEventLog {

    public ArrayList<Trace> traces;
    public ArrayList<String> eventName;
//public Map event;
    public EasyEventLog() {
        traces = new ArrayList<>();
        eventName = new ArrayList<>();
//        event=new HashMap();
    }
    public void print(){
    	for(Trace trace:traces){
    	for(Integer event:trace.events){
    		System.out.println(event);
    	}
    }
    }
}
