package org.pmf.alphaplus;

//import org.deckfour.xes.*;
import org.deckfour.xes.model.*;
import org.pmf.alphaplus.model.*;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.classification.*;
import org.deckfour.xes.model.impl.*;
import org.deckfour.xes.model.XAttributeMap;
import org.pmf.alphaplus.*;

import java.io.File;
import java.util.*;

public class AlphaP {
	
	private ArrayList<Integer> L1L=new ArrayList<Integer>();
	private File xesFile;
	private XLog log;
	public AlphaP(String path){
		xesFile=new File(path);	
	}
	public AlphaP(XLog log){
		this.log=log;
	}
	private XLog parseFile(File xesFile){
		XesXmlParser parser=new XesXmlParser();
		log=null;
		if (!parser.canParse(xesFile)) {
            return null;
        }
		try{
		List<XLog> logs=parser.parse(xesFile);
		log=logs.get(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return log;
	}
	private EasyEventLog parseXLog(XLog log){
		EasyEventLog easyEventLog=new EasyEventLog();
		List<XEventClassifier> eventClassifiers=log.getClassifiers();
		XEventClassifier classifier=eventClassifiers.get(1);
		String attribute=classifier.getDefiningAttributeKeys()[0];
		//将eventName&index填入easyEventlog
		Iterator<XEventClass> iterator=XLogInfoFactory.createLogInfo(log).getNameClasses().getClasses().iterator();
		String tmpevename;
		while(iterator.hasNext()){
			tmpevename=iterator.next().getId();
			easyEventLog.eventName.add(tmpevename);
		}
		//将traces填入eventlog
		for(int i=0;i<log.size();i++){
			XTrace trace=log.get(i);
			Trace tmpTrace=new Trace();
			for(int j=0;j<trace.size();j++){
				XEventImpl event = (XEventImpl) trace.get(j);
				XAttributeMap map=event.getAttributes();
				tmpTrace.events.add(easyEventLog.eventName.indexOf(map.get(attribute).toString()));			
			}
			easyEventLog.traces.add(tmpTrace);
		}
		return easyEventLog;
	}
	private EasyEventLog eliminateL1L(EasyEventLog eventLog){
		Integer loopElement1;
		 for (Trace trace : eventLog.traces) {
	            for (int i = 0; i < trace.events.size() - 1; i++) {
	            	loopElement1=trace.events.get(i);
	            	if(trace.events.get(i).equals(trace.events.get(i+1)) && !L1L.contains(loopElement1)) {            		
	            		L1L.add(loopElement1);
	            	}
	            }
		 }
		 //remove from all traces
		 for(Integer loopele:L1L){
			 for (Trace trace : eventLog.traces) {	
				 Iterator<Integer> iterator=trace.events.iterator();
	            while(iterator.hasNext()) {
	            	Integer eve=iterator.next();
	            	if(eve.equals(loopele)){
	            		iterator.remove();
	            	}
	            }
			 }
	     }
		 return eventLog;
	}
	
	public ArrayList<Integer> getA_B(ArrayList<Integer> A,ArrayList<Integer> B){
		//取在A中不在B中的元素
		ArrayList<Integer> A_B=new ArrayList<Integer>();
		for(Integer a:A){
			if(B.indexOf(a)==-1) A_B.add(a);
		}
		return A_B;
	}
	public ArrayList<Integer> getB_A(ArrayList<Integer> A,ArrayList<Integer> B){
		//取在B中不在A中的元素
		ArrayList<Integer> B_A=new ArrayList<Integer>();
		for(Integer b:B){
			if(A.indexOf(b)==-1) B_A.add(b);
		}
		return B_A;
	}


	public PetriNet getPetriNet(){
		if(xesFile!=null){
			log=parseFile(xesFile);
		}
		EasyEventLog eventLogorigin=parseXLog(log); 
		
		EasyEventLog eventLog=parseXLog(log);
		
		//step3:消除length1loop。避免l1l带来的cdc
		eliminateL1L(eventLog);
		
		//step8:用alpha挖掘消除短循环后的日志
		Alpha alpha=new Alpha(eventLog);
		PetriNet alphaMinedPetri=alpha.getPetriNet();
		
		//step11:重新插入长度为1的循环结构
		//加入l1l
		ArrayList<Integer> A=new ArrayList<Integer>();
		ArrayList<Integer> B=new ArrayList<Integer>();
		ArrayList<Integer> A_B=new ArrayList<Integer>();
		ArrayList<Integer> B_A=new ArrayList<Integer>();
		
		 for(Integer loopele:L1L){
			 for (Trace trace : eventLogorigin.traces) {	
				 for (int i = 0; i < trace.events.size(); i++) {
					 if(trace.events.get(i).equals(loopele)){
						 if(i!=0&&!A.contains(trace.events.get(i-1)))A.add(trace.events.get(i-1));
						 if(i!=trace.events.size()&&!B.contains(trace.events.get(i+1)))B.add(trace.events.get(i+1));
					 }
				 }
			 }
			 A_B=getA_B(A,B);
			 B_A=getB_A(A,B);

			 //find place(A_B,B_A),add loopele to its left&right transition
			 Place tmpPlace=new Place(A_B,B_A,alphaMinedPetri.transitions);
			 for(Place p:alphaMinedPetri.places){
				 if(p.equals(tmpPlace)){
					 p.leftTransition.add(loopele);
					 p.rightTransition.add(loopele);
				 }
			 }
	     } 
		 //加入l2l
			Integer loopElement1,loopElement2;
			 for (Trace trace : eventLog.traces) {
		            for (int i = 0; i < trace.events.size() - 2; i++) {
		            	if(trace.events.get(i).equals(trace.events.get(i+2))) {
		            		loopElement1=trace.events.get(i);
		            		loopElement2=trace.events.get(i+1);
		            		
		            		 for(Place p :alphaMinedPetri.places){
		        				 if(p.leftTransition.contains(loopElement1)){
		        					 p.rightTransition.add(loopElement2);
		        				 }
		        				 if(p.rightTransition.contains(loopElement1)){
		        					 p.leftTransition.add(loopElement2);
		        				 }
		        			 }
		            	}
		            }
			 }
			 
			
//		alphaMinedPetri.showPlaces();
		return alphaMinedPetri;
	}
	
}
