package com.egis.xdserver.svc;

import java.util.List;
import java.util.Map;

import com.egis.xdserver.object.Layer;
import com.egis.xdserver.object.Type;

public interface ManageService {

	//레이어 그룹
	
	//레이어 그룹 가져오기_전체
	public List<Type> getTypeListALL() throws Exception;
	//레이어 그룹 가져오기
	public Type getTypeByName(String name) throws Exception;
	//레이어 그룹 추가()
	void addType(Map<?, ?> type) throws Exception;
	//레이어 그룹 중복체크_중복아닐때 0(name으로 개수 가져오기)
	int getTypeLength(String name);
	//레이어 그룹 수정
	void editType(Map<?, ?> type);
	//레이어 그룹 삭제
	void removeType(String name);
	
	//레이어
	
	//레이어 리스트 가져오기_전체
	public List<Layer> getLayerListALL() throws Exception;
	//레이어  가져오기
	public Layer getLayerByName(String name) throws Exception;
	//레이어 갯수_전체
	int getLayerListCnt() throws Exception;
	//레이어 추가()
	void addLayer(Map<?, ?> layer) throws Exception;
	//레이어 중복체크_중복아닐때 0(name으로 개수 가져오기)
	int getLayerLength(String name);
	//레이어 수정
	void editLayer(Map<?, ?> layer);
	//레이어 삭제
	void removeLayer(String name);
	
	void reload();
	
	String getPath() throws Exception;

}
