package com.egis.xdserver.manager;

import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egis.xdserver.object.Node;
import com.egis.xdserver.object.Object;

public class RequestProc {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public RequestProc() {
	}
	
	// node 요청
	public Node getRequestNode(HttpServletRequest req){
		String layer = getParamString(req,"Layer");
		int level = getParamInt(req,"Level");
		int idx = getParamInt(req,"IDX");
		int idy = getParamInt(req,"IDY");
		int idz = getParamInt(req,"IDZ");
		//logger.info("IDZ:"+idz);

		if(layer==null||level<0||idx<0||idy<0) {
			return null;
		}
	
		return new Node(layer,level,idx,idy,idz);

	}
	
	// object 요청
	public Object getRequestObject(HttpServletRequest req) {
		String layer = getParamString(req,"Layer");
		int level = getParamInt(req,"Level");
		int idx = getParamInt(req, "IDX");
		int idy = getParamInt(req,"IDY");
		String dataFile = getParamString(req,"DataFile");
		
		if(layer==null||level<0||idx<0||idy<0||dataFile==null) {
			return null;
		}

		return new Object(layer,level,idx,idy,dataFile);			
	}

	// string형 param 추출
	public static String getParamString(HttpServletRequest req,String valName){
		String res = req.getParameter(valName);	//대소문자 구분이 필요하고 모두 소문자일 경우만 허용
		//인자값을 소문자로 변경하여 읽는다
		if(res==null) res = req.getParameter(valName.toLowerCase());
		return res;
	}
	
	// int형 param 추출
	public static int getParamInt(HttpServletRequest req,String valName){
		String res = req.getParameter(valName);	//대소문자 구분이 필요하고 모두 소문자일 경우만 허용
		//인자값을 소문자로 변경하여 읽는다
		if(res==null) {
			res = req.getParameter(valName.toLowerCase());
		}
		if(res==null) return -1;
		return Integer.parseInt(res);
	}
	
}
