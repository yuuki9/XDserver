package com.egis.xdserver.manager;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egis.xdserver.object.Type;
import com.egis.xdserver.util.Com;

public class TypeManager {
	private HashMap<String,Type>TypeList = new HashMap<String,Type>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String strMapInfo = "";
	
	public void add(String n, Type e) {
		TypeList.put(n,e);
	}
	
	public Type getTypeByName(String name){
		return this.TypeList.get(name);
	}

	public void clean() {
		int b = this.TypeList.size();
		this.TypeList.clear();
		int a = this.TypeList.size();
		Com.fc.infoFormat("Clean Layers List : Before(%d) >> After(%d)",b,a);
		strMapInfo = "";
		logger.info("Cleared MapInfo");
	}
	
	public HashMap<String,Type> getTypeList(){
		return this.TypeList;
	}
}
