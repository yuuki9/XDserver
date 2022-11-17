package com.egis.xdserver.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.egis.xdserver.manager.RequestProc;
import com.egis.xdserver.manager.ResponseProc;
import com.egis.xdserver.object.Node;
import com.egis.xdserver.object.Object;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class BaseServiceImpl implements BaseService {

	@Value("${vworld.url}")
	private String vworldUrl;
	
	@Value("${vworld.key}")
	private String vworldKey;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String vwLayerName = "facility_build_vw"; // 가상세계 서울 전체건물 

	/**
	 * 레이어 호출
	 **/
	@Override
	public void requestLayerNode(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	//preparations processing request 
		//요청들어온 node 및 object 파라미터 추출 클래스 
    	RequestProc reqProc = new RequestProc();
    	
    	//preparations processing response
    	//응답 포맷 설정 클래스 
    	ResponseProc resProc = new ResponseProc();
    	
    	//node 파라미터 추출 //데이터 위치(layer, level, idx, idy)
    	Node node = reqProc.getRequestNode(request);

    	//추출된 파라미터 null값 체크
    	if(node == null||!node.m_check) {
    		
    		//response NG
			String errInfo = String.format("<Error>NG : error parameters. CHECK : %s</Error>",request.getLocalAddr());
			logger.info(errInfo);
			
			//응답 포맷 설정(xml형식으로 에러 반환)
			resProc.responseStringXML(response,"ERROR_SERVICE_PARAMETER_NG",node);
			reqProc = null; resProc = null; node=null;
			return;
    		
    	}
    	
		//SEND
		if (resProc.loadingAndResponseNode(response, node)) {
	
		} else {
			String errInfo = String.format("<Error>NG : error loading and response. CHECK : %s</Error>", request.getContextPath());
			logger.info(errInfo);
			
			//응답 포맷 설정(xml형식으로 에러 반환)
			resProc.responseStringXML(response, "ERROR_SERVICE_FILE_NOTTING",node);
			
		}
    	
		reqProc = null;
		resProc = null;
		node = null;
    	
	}

	/**
	 * 오브젝트 호출
	 **/
	@Override
	public void requestLayerObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//preparations processing request 
		//요청들어온 node 및 object 파라미터 추출 클래스
    	RequestProc reqProc = new RequestProc();
    	
    	//preparations processing response
    	//응답 포맷 설정 클래스 
    	ResponseProc resProc = new ResponseProc();
    	
    	//Object 파라미터 추출
    	Object obj = reqProc.getRequestObject(request);
    	
    	//추출된 파라미터 null값 체크
    	if(obj == null||!obj.m_check) {
    		
			//response NG
			String errInfo = String.format("<Error>NG : error parameters. CHECK : %s</Error>",request.getQueryString());
			logger.info(errInfo);
			
			//응답 포맷 설정(xml형식으로 에러 반환)
			resProc.responseString(response,"ERROR_SERVICE_PARAMETER_NG",obj);
			reqProc = null; resProc = null; obj = null;
			return;
		}
		
    	// 가상세계 서울 전체 건물과 이외 레이어 분기
    	String _layer = obj.m_layer.trim(); // 레이어명에 공백값이 포함되어 들어올 경우
    	
    	//가상세계 서울 전체건물은 프록시 사용하여 브이월드로 requestLayerObject 요청
    	if( _layer.equals(vwLayerName)){ 
    		 
			String errInfo = String.format("<Error>NG : error loading and response. CHECK : %s</Error>",request.getContextPath());
			logger.info(errInfo);
			
			Integer _level = obj.m_level;
			Integer _ldx = obj.m_idx;
			Integer _ldy = obj.m_idy;
			String _dataDile = obj.m_dataFile;
			
			// 프록시 태울 URL 주소
			String _url = vworldUrl + "/XDServer/requestLayerObject?Layer=facility_build&Level="+_level
					+"&IDX="+_ldx+"&IDY="+_ldy+"&DataFile="+_dataDile+"&APIKey="+vworldKey;
			
			// 프록시 
			resProc.proxy(request, response,_url);
			
    	}else{ // 이외 레이어는 기존로직대로
    		
    		//SEND
    		if(resProc.loadingAndResponseObject(response,obj)) {
    			
    		}else	{
    			
    			//응답 포맷 설정(xml형식으로 에러 반환)
    			String errInfo = String.format("<Error>NG : error loading and response. CHECK : %s</Error>",request.getContextPath());
    			logger.info(errInfo);
    			resProc.responseString(response,"ERROR_SERVICE_FILE_NOTTING",obj);
    			
    		}
    		
    	}
    	
		reqProc = null;
		resProc = null;
		obj=null;
		
	}
	
}
