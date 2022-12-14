package com.egis.xdserver.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.egis.xdserver.object.Layer;
import com.egis.xdserver.object.Type;
import com.egis.xdserver.svc.ManageService;

@RestController
public class ManageController {

	private ManageService svc;
	public ManageController(ManageService svc) { this.svc = svc;  }

    // getTypeListALL
    @GetMapping("/getTypeListALL")
    public List<Type> getTypeListALL() throws Exception {
    	List<Type> result = svc.getTypeListALL();
    	return result;
    }

    // getTypeByName
    @GetMapping("/getTypeByName")
    public Type getTypeByName(String name) throws Exception {
    	Type result = svc.getTypeByName(name);
    	return result;
    }

    
    // getLayerListALL
    @GetMapping("/getLayerListALL")
    public List<Layer> getLayerList() throws Exception {
    	List<Layer> result = svc.getLayerListALL();
    	return result;
    }
    
    // getLayerByName
    @GetMapping("/getLayerByName")
    public Layer getLayerByName(String name) throws Exception {
    	Layer result = svc.getLayerByName(name);
    	return result;
    }
    
    // getLayerListCnt
    @GetMapping("/getLayerListCnt")
    public int getLayerListCnt() throws Exception {
    	int result = svc.getLayerListCnt();
    	return result;
    }

    
    // getPath
    @GetMapping("/getPath")
    public String getPath() throws Exception {
    	String result = svc.getPath();
    	return result;
    }

    // addType
    @PostMapping("/addType")
	@ResponseBody 
    public String addType(@RequestBody Map<?, ?> type) throws IOException {
    	int TypeLenth = svc.getTypeLength((String)type.get("name"));
    	System.out.println(TypeLenth);
    	if(TypeLenth==0) {
			try {
				System.out.println("??????");
				svc.addType(type);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else {
    		return "????????????";
      	}
		return "????????????";
	}

	// addLayer
    @PostMapping("/addLayer")
	@ResponseBody 
    public String addLayer(@RequestBody Map<?, ?> layer) {
    	int layerLenth = svc.getLayerLength((String)layer.get("name"));
    	System.out.println(layerLenth);
    	if(layerLenth==0) {
			try {
				System.out.println("??????");
				svc.addLayer(layer);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else {
			System.out.println("????????????");
    		return "????????????";
      	}
		System.out.println("????????????");
		return "????????????";
	}

    // editType
    @PostMapping("/editType")
	@ResponseBody 
    public String editType(@RequestBody Map<?, ?> type) throws IOException {
    	if((String)type.get("name")!=(String)type.get("beforeName")) {
        	int typeLength = svc.getTypeLength((String)type.get("name"));
        	System.out.println(typeLength);
	    	if(typeLength==0) {
				try {
					System.out.println("??????");
					svc.editType(type);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "????????????";
			}else {
				return "?????????????????? ??????";
			}
    	}else {
    		try {
				System.out.println("??????");
				svc.editType(type);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "????????????";
    	}
    	
	}

    // editLayer
    @PostMapping("/editLayer")
	@ResponseBody 
    public String editLayer(@RequestBody Map<?, ?> layer) throws IOException {
    	if((String)layer.get("name")!=(String)layer.get("beforeName")) {
    		int layerLength = svc.getLayerLength((String)layer.get("name"));
        	System.out.println(layerLength);
	    	if(layerLength==0) {
				try {
					System.out.println("??????");
					svc.editLayer(layer);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "????????????";
	    	}else {
					return "???????????? ??????";
			}
    	}else {
    		try {
				System.out.println("??????");
				svc.editLayer(layer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "????????????";
    	}
	}
    
    // reomveType
    @GetMapping("/removeType")
    public String removeType(@RequestParam String name){
    	System.out.println(name);
    	svc.removeType(name);
    	return "????????????";
	}
    
    // reomveLayer
    @GetMapping("/removeLayer")
    public String removeLayer(@RequestParam String name){
    	System.out.println(name);
    	svc.removeLayer(name);
    	return "????????????";
	}
    
}
