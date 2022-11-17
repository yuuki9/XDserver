package com.egis.xdserver.manager;

import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egis.xdserver.object.Layer;
import com.egis.xdserver.util.Com;

public class LayerManager {
	
	private HashMap<String,Layer>layerList = new HashMap<String,Layer>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String strMapInfo = "";
	public void add(String n, Layer e) {
		layerList.put(n,e);
	}
	
	//레이어정보를 조회할때 호출
	public String createMapInfo(){
		String strLayers="";
		
		for(Entry<String, Layer> elem : this.layerList.entrySet()){
			Layer item = elem.getValue();
			strLayers+=item.getToString();
		}
		
		return String.format("<?xml version=\"1.0\"?><%s><Version>%s</Version><Layers Count=\"%d\">%s</Layers></%s>", "MapInfo","1.0",layerList.size(),strLayers,"MapInfo");
	}
	
	public Layer getLayerByName(String name){
		return this.layerList.get(name);
	}

	public void clean() {
		int b = this.layerList.size();
		this.layerList.clear();
		int a = this.layerList.size();
		Com.fc.infoFormat("Clean Layers List : Before(%d) >> After(%d)",b,a);
		strMapInfo = "";
		logger.info("Cleared MapInfo");
	}
	
	public HashMap<String,Layer> getLayerList(){
		return this.layerList;
	}

}
