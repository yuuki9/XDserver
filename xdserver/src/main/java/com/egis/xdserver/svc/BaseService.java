package com.egis.xdserver.svc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface BaseService {

	void requestLayerNode(HttpServletRequest request, HttpServletResponse response) throws Exception;
	void requestLayerObject(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
