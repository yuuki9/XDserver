package com.egis.xdserver.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egis.xdserver.svc.BaseService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 강민아
 * @date 2022. 5. 30.
 * MainController 기존 
 */

@RestController
@Slf4j
public class BaseController {

	private BaseService svc;
	public BaseController(BaseService svc) { this.svc = svc;  }
	
	// Layer 
    // http://xdworld.vworld.kr:8080/XDServer/requestLayerNode?Layer=tile&Level=0&IDX=1&IDY=0&APIKey=42F6D36E-1A78-34B7-959F-37611794397B
    @GetMapping("/requestLayerNode")
    public void requestLayerNode(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	 
    	svc.requestLayerNode(request,response);
    	
    }
    
    // Object
    // https://geo2.dtwincloud.com/XDServer/requestLayerObject?Layer=bldg_ver2&Level=12&IDX=34926&IDY=14513&DataFile=VER_2_1.jpg&APIKey=42F6D36E-1A78-34B7-959F-37611794397B
    @GetMapping("/requestLayerObject")
    public void requestLayerObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	 
    	svc.requestLayerObject(request,response);
    	
    }
    
    
}
