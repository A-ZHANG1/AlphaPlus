package org.pmf.alphaplus;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.pmf.alphaplus.model.EasyEventLog;
import org.pmf.alphaplus.model.PetriNet;
import org.pmf.alphaplus.model.Place;
import org.pmf.alphaplus.model.Trace;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Alpha {

    public static int FOOTPRINT_FRONT = 0;
    public static int FOOTPRINT_BEHIND = 1;
    public static int FOOTPRINT_NONE = 2;
    public static int FOOTPRINT_CONCURRENT = 3;

    private EasyEventLog eventLog;
    public Alpha(EasyEventLog log) {
       this.eventLog=log;
    }

    public PetriNet getPetriNet() {
        int[][] footprint = getFootPrint(eventLog);

        PetriNet petriNet = createPetriNet(eventLog, footprint);
        petriNet.showPlaces();
        return petriNet;
    }

    private int[][] getFootPrint(EasyEventLog eventLog) {
        int[][] footprint = new int[eventLog.eventName.size()][eventLog.eventName.size()];
        //初始化
        for (int i = 0; i < eventLog.eventName.size(); i++) {
            for (int j = 0; j < eventLog.eventName.size(); j++) {
                footprint[i][j] = FOOTPRINT_NONE;
            }
        }

        for (Trace trace : eventLog.traces) {
            for (int i = 0; i < trace.events.size() - 1; i++) {
                int a = trace.events.get(i);
                int b = trace.events.get(i + 1);
                if (footprint[b][a] == FOOTPRINT_FRONT) {
                    footprint[a][b] = FOOTPRINT_CONCURRENT;
                    footprint[b][a] = FOOTPRINT_CONCURRENT;
                } else {
                    footprint[a][b] = FOOTPRINT_FRONT;
                    footprint[b][a] = FOOTPRINT_BEHIND;
                }
            }
        }
        return footprint;
    }

    private PetriNet createPetriNet(EasyEventLog eventLog, int[][] footprint) {
        PetriNet petriNet = new PetriNet(eventLog.eventName);
        ArrayList<Place> tmpPlaces = new ArrayList<>();

        // 找出所有符合要求的库所
        for (int i = 0; i < footprint.length; i++) {
            for (int j = 0; j < footprint[i].length; j++) {
                if (footprint[i][j] == FOOTPRINT_FRONT) {
                    tmpPlaces.add(new Place(i, j, petriNet.transitions));
                    int m = tmpPlaces.size();
                    for (int k = 0; k < m; k++) {
                        Place place = tmpPlaces.get(k);
                        if (place.canMerge(i, j, footprint)) {
                            // 复制这个库所，把i j计入库所
                            Place newPlace = new Place(petriNet.transitions);
                            newPlace.leftTransition.addAll(place.leftTransition);
                            newPlace.rightTransition.addAll(place.rightTransition);

                            if (!newPlace.leftTransition.contains(i)) {
                                newPlace.leftTransition.add(i);
                            }
                            if (!newPlace.rightTransition.contains(j)) {
                                newPlace.rightTransition.add(j);
                            }
                            tmpPlaces.add(newPlace);
                        }
                    }
                }
            }
        }

        // 删除多余库所，只保留最大
        for (Place tmpPlace : tmpPlaces) {
            boolean shouldInsert = true;
            ArrayList<Place> removePlace = new ArrayList<>();
            for (int i = 0; i < petriNet.places.size(); i++) {
                Place place = petriNet.places.get(i);
                if (tmpPlace.canContain(place)) {
                    removePlace.add(place);
                    // 复制这个库所，并把i j加入库所
                    for (int j = i + 1; j < petriNet.places.size(); j++) {
                        place = petriNet.places.get(j);
                        if (tmpPlace.canContain(place)) {
                            removePlace.add(place);
                        }
                    }
                    break;
                } else if (place.canContain(tmpPlace)) {
                    shouldInsert = false;
                    break;
                }
            }
            if (shouldInsert) {
                petriNet.places.removeAll(removePlace);
                petriNet.addPlace(tmpPlace);
            }
        }
        
        //adding a start node
        System.out.println("its adding sth");
        Place startPlace=new Place(petriNet.transitions);
        startPlace.rightTransition.add(tmpPlaces.get(0).leftTransition.get(0));
		petriNet.addPlace(startPlace);
		
		//adding an end node
		Place endPlace=new Place(petriNet.transitions);
		endPlace.leftTransition.add(tmpPlaces.get(tmpPlaces.size()-1).rightTransition.get(0));
		petriNet.addPlace(endPlace);
		
        return petriNet;
    }

}
