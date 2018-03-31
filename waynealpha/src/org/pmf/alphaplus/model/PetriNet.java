package org.pmf.alphaplus.model;
import java.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.pmf.alphaplus.model.Place;
public class PetriNet {
	public ArrayList<Transition> transitions;
	public ArrayList<Place> places;
//	public ArrayList<String> places;
	public PetriNet(ArrayList<String> transitions){
		this.transitions=new ArrayList<>();
		for(String transition:transitions){
			this.transitions.add(new Transition(transition));		
		}
		places=new ArrayList<>();
	}
	public PetriNet(ArrayList<String> transitions,ArrayList<Place> places){
		this.transitions=new ArrayList<>();
		for(String transition:transitions){
			this.transitions.add(new Transition(transition));		
		}
		this.places=new ArrayList<>();
		for(Place place:places){
			this.places.add(place);		
		}
	}
	public void addPlace(Place place) {
	        places.add(place);
	}
	public void showPlaces(){
		String str="{";
		for(Place place:places){
			str+=place.toString();
			str+=",";
		}
		str=str.substring(0, str.length()-1);
		str+="}";
		System.out.println(str);
	}
	
	public JSONObject buildJsn(){
		JSONObject jsn=new JSONObject();
		Map<PetrinetNode,Integer> nodeIndex=new HashMap<>();
		int index=0;
		
		JSONArray nodeJsns=new JSONArray();
		for(Transition transition:transitions){
			JSONObject transitionJsn=new JSONObject();
			transitionJsn.element("label", transition.name);
			transitionJsn.element("detail", transition.name);
			transitionJsn.element("type","PN_TRANSITION");
			nodeJsns.add(transitionJsn);
			nodeIndex.put(transition, index);
			index++;
		}
		
		int pIndex=0;
		
		for(Place place:places){
			JSONObject placeJsn=new JSONObject();
			
			//try give start and end node names
			if(pIndex==places.size()-2) {
				placeJsn.element("label", "start");
				placeJsn.element("detail", "start");
				placeJsn.element("type", "PN_PLACE");
				nodeJsns.add(placeJsn);
				nodeIndex.put(place, index);
				pIndex++;
				index++;
			}else if(pIndex==places.size()-1) {
				placeJsn.element("label", "end");
				placeJsn.element("detail", "end" + pIndex);
				placeJsn.element("type", "PN_PLACE");
				nodeJsns.add(placeJsn);
				nodeIndex.put(place, index);
				pIndex++;
				index++;
			}else{
		
//		jsn.element("nodes", nodeJsns);
			//modification ends 
		
			placeJsn.element("label","P"+pIndex);
			placeJsn.element("detail", "P"+pIndex);
			placeJsn.element("type","PN_Place");
			nodeJsns.add(placeJsn);
			nodeIndex.put(place, index);
			pIndex++;
			index++;
			}
		}
		
		jsn.element("nodes",nodeJsns);
		
		JSONArray links=new JSONArray();
		for(Place place:places){
			for(Integer leftIndex:place.leftTransition){
				JSONObject linkJsn=new JSONObject();
				linkJsn.element("source",nodeIndex.get(transitions.get(leftIndex)));
				linkJsn.element("target",nodeIndex.get(place));
				linkJsn.element("type", 1);
				links.add(linkJsn);
			}
			for(Integer rightIndex:place.rightTransition){
				JSONObject linkJsn=new JSONObject();
				linkJsn.element("source",nodeIndex.get(place));
				linkJsn.element("target",nodeIndex.get(transitions.get(rightIndex)));
				linkJsn.element("type", 1);
				links.add(linkJsn);
			}
		}
		jsn.element("links",links);
		
		return jsn;
	}
}
