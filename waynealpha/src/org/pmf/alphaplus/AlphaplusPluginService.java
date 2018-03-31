package org.pmf.alphaplus;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.pmf.alphaplus.model.PetriNet;
import org.pmf.plugin.service.PluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlphaplusPluginService implements PluginService {
	@Override
public JSONObject doPluginService(XLog paramXLog,Map<String,String> paramMap){
	AlphaP alphaP=new AlphaP(paramXLog);
	PetriNet petriNet=alphaP.getPetriNet();
	JSONObject jsn=new JSONObject();
	if(petriNet==null){
		jsn.element("status", "ERROR");
		return jsn;
	}
	jsn.element("status", "OK");
	jsn.element("result",petriNet.buildJsn());
	
	XLogInfo logInfo=XLogInfoFactory.createLogInfo(paramXLog);
	XEventClasses classes=logInfo.getEventClasses();
	List<XEventClass> eventClasses=new ArrayList(classes.size());
	eventClasses.addAll(classes.getClasses());
	JSONArray logarray=new JSONArray();
	for(XEventClass ec:eventClasses){
		JSONObject logitem=new JSONObject();
		logitem.element("EventClass",ec.toString());
		logitem.element("Frequency",ec.size());
		logarray.element(logitem);
		}
	jsn.element("log",logarray);
	
	return jsn;
}

}
