package org.pmf.plugin.service;

import net.sf.json.JSONObject;
import org.deckfour.xes.model.XLog;

import java.util.Map;

public abstract interface PluginService {
    public abstract JSONObject doPluginService(XLog paramXLog, Map<String, String> paramMap);
}
