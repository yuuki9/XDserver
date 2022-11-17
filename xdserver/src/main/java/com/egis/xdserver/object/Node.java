package com.egis.xdserver.object;

import com.egis.xdserver.util.Com;

public class Node {
	public String m_layer="";
	public int m_level=-1;
	public int m_idx=-1;
	public int m_idy=-1;
	public int m_idz=-1;
	public int m_num=-1;
	public boolean m_check = false;
	
	public Node(String layer, int level, int idx, int idy) {
		setValues(layer,level,idx,idy,-1);
	}

	public Node(String layer, int level, int idx, int idy, int idz) {
		setValues(layer,level,idx,idy,idz);		
	}
	
	private void setValues(String layer, int level, int idx, int idy, int idz) {
		if(layer==null||level<0||idx<0||idy<0) return;
		m_layer = layer;
		m_level=level;
		m_idx=idx;
		m_idy=idy;
		if(idz>-1) m_idz=idz;
		m_check=true;
	}
	
	public void setValueNum(int num) {
		if(num<0) return;
		m_num=num;
	}

	public String getLayerName() {
		return m_layer;
	}

	public int getLevel() {
		return m_level;
	}
	
	// spatial tile index -> 폴더 형식 정형화
	public String getNodePath() {
		if(m_idx<0||m_idy<0) return null;
		String strx = Layer.setFormated(m_idx);
		String stry = Layer.setFormated(m_idy);
		String strz = Layer.setFormated(m_idz);
		if(strx==null||stry==null)return null;
	
		if(m_idz>-1)		return String.format("%d%s%s%s%s_%s_%s",m_level,Com.sign,stry,Com.sign,stry,strx,strz);
		else 				return String.format("%d%s%s%s%s_%s",m_level,Com.sign,stry,Com.sign,stry,strx);
	}
	
	
	public String getImagePath() {
		if(m_idx<0||m_idy<0) return null;
		String strx = Layer.setFormated(m_idx);
		String stry = Layer.setFormated(m_idy);
//		String snum = Layer.setFormated(m_num);
		if(strx==null||stry==null)return null;
	
		return String.format("%d%s%s%s%s_%s_%d",m_level,Com.sign,stry,Com.sign,stry,strx,m_num);
	}

}