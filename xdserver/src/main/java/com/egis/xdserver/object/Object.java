package com.egis.xdserver.object;

public class Object {
	public String m_layer = "";
	public int m_level = -1;
	public int m_idx = -1;
	public int m_idy = -1;
	public String m_dataFile = "";
	public boolean m_check = false;
	
	public Object(String layer, int level, int idx, int idy, String dataFile) {
		if(layer==null||level<0||idx<0||idy<0||dataFile==null) return;
		m_layer = layer;
		m_level = level;
		m_idx = idx;
		m_idy = idy;
		m_dataFile = dataFile;
		m_check=true;
	}
	
}
