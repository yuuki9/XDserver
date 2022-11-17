package com.egis.xdserver.manager;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egis.xdserver.object.Config;
import com.egis.xdserver.object.Layer;
import com.egis.xdserver.object.Node;
import com.egis.xdserver.object.Object;
import com.egis.xdserver.util.Com;
import com.egis.xdserver.util.FormatConvert;


/**
 * @author 강민아
 * @date 2022. 7. 25.
 */
public class ResponseProc {

	private static  int BUFFER_SIZE = 1024;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Com com = new Com();
	
	public String errmsg = "";
	
	public ResponseProc() {
	}
	
	// 응답 포맷 설정
	public boolean responseString(HttpServletResponse resp, String str, Object obj) {
		
		try {
			
			Element eleError = new Element("Error");        // root
	        Document myDocument = new Document(eleError);        // 문서를 생성하고 엘리먼트를 추가..
	         
	        eleError.setAttribute(new Attribute("code",str));
	        
	        eleError.addContent(new Element("Version").addContent(Com.version));      
	        
	        Element eleExist = new Element("Exist");     
	        eleExist.setAttribute("LEVEL", ""+obj.m_level+"");
	        eleExist.setAttribute("IDX", ""+obj.m_idx+"");
	        eleExist.setAttribute("IDY", ""+obj.m_idy+"");
	        eleExist.setAttribute("DATAFILE", ""+obj.m_dataFile+"");
	        
	        eleError.addContent(eleExist);
	        
	        // Format을 맞추자.
	        Format f = Format.getCompactFormat();
	        f.setEncoding("utf-8");
	        f.setIndent("    ");
	        XMLOutputter outputter = new XMLOutputter(f);		
	         
	        resp.setContentType("application/xml; charset=UTF-8");	    
	        resp.setCharacterEncoding("utf-8");
	        resp.getWriter().write(outputter.outputString(myDocument));	
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	// 응답 포맷 설정(xml형식으로 에러 반환) 
	public boolean responseStringXML(HttpServletResponse resp, String str, Node node) {

		try {
			if(resp==null) return false;
			
			Element eleError = new Element("Error");        // root
	        Document myDocument = new Document(eleError);        // 문서를 생성하고 엘리먼트를 추가..
	         
	        eleError.setAttribute(new Attribute("code",str));
	        
	        eleError.addContent(new Element("Version").addContent(Com.version));      
	        
	        Element eleExist = new Element("Exist");     
	        eleExist.setAttribute("LEVEL", ""+node.m_level+"");
	        eleExist.setAttribute("IDX", ""+node.m_idx+"");
	        eleExist.setAttribute("IDY", ""+node.m_idy+"");
	       // eleExist.setAttribute("CODE", str);
	        
	        eleError.addContent(eleExist);
	        
	        // Format을 맞추자.
	        Format f = Format.getCompactFormat();
	        f.setEncoding("utf-8");
	        f.setIndent("    ");
	        XMLOutputter outputter = new XMLOutputter(f);		
	         
	        resp.setContentType("application/xml; charset=UTF-8");	    
	        resp.setCharacterEncoding("utf-8");
	        resp.getWriter().write(outputter.outputString(myDocument));	
			
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 응답 포맷 설정
	public boolean responseBinary(HttpServletResponse resp, byte[] bs) throws ServletException {
		//System.out.println("check:");
		if(resp == null ) {
			errmsg = "ServletException : response is null";
			logger.info("responseBinary : 0097 : resp is null"); return false;
		}
		if(bs==null) {
			errmsg = "IOException : file is null";
			logger.info("responseBinary : 0100 : file is null.");return false;
		}
		Com.fc.debugFormat("Byte length : %d", bs.length);

		resp.setContentLength(bs.length);	// 파일크기설정
		return sendFileBytes( resp, bs);
	}
	
	public boolean sendFileBytes(HttpServletResponse resp , byte[]filebuf) {
		OutputStream o = null;
		try {
			o = resp.getOutputStream();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	   	ByteArrayInputStream in = new ByteArrayInputStream(filebuf);
		BufferedInputStream f = new BufferedInputStream(in);
    	 
        int n = 0;
        
        boolean res = false;
        try{
        	byte[] buffer = new byte[BUFFER_SIZE];
	        while((n=f.read(buffer, 0, BUFFER_SIZE)) != -1) {
	            o.write(buffer, 0, n);
	            o.flush();
	        }
	        o.flush();
	        o.close();
	        in.close();
	        res = true;
	        buffer = null;
        } catch(IOException e){
        	errmsg = "IOException : ByteArrayInputStream";
        }
        in = null; f = null;o = null;
        return res;
	}
	//요청 파일 레이어 조회 및 레벨 체크
	public boolean loadingAndResponseObject(HttpServletResponse resp, Object obj) throws ServletException, IOException {
		Layer layer = Com.conf.findByLayerName(obj.m_layer);
		boolean result = false;
		if(layer == null )	return result;
		
		if(layer.chkServiceLevel(obj.m_level)) {
			
			//System.out.println("OBJ PATH CHECK:"+layer.getFilePath(obj.m_level, obj.m_idx, obj.m_idy, obj.m_dataFile));
			
			byte [] res = null;
			//String[] arrtest = {"test_0.xdo","test_1.xdo"};
			//for(int i=0;i<arrtest.length;i++) {
			//	res = layer.getFileBinaries(layer.getFilePath(obj.m_level, obj.m_idx, obj.m_idy, arrtest[i]));
			//	System.out.println("OBJ 확인2:"+arrtest[i]);
			
			res = layer.getFileBinaries(layer.getFilePath(obj.m_level, obj.m_idx, obj.m_idy, obj.m_dataFile));
				
			if(res==null) {
				logger.info("[NG] result is NULL at get file binaries.");
				return false;
			}
			
			if(responseBinary(resp, res)) {
				Com.fc.debugFormat("Send data : %d",res.length);
				result = true;
			}else {
				logger.info("NG");
			}
			//}
			res = null;
		}
		layer = null;
		return result;
	}
	//실제 요청된 파일 읽어서 반환
	public boolean loadingAndResponseNode(HttpServletResponse resp, Node node) throws ServletException, IOException{
		Layer layer = Com.conf.findByLayerName(node.getLayerName()); 
		if(layer == null ) 	return false;
		
		if(layer.chkServiceLevel(node.getLevel())) { // 서비스범위확인
			//node.getNodePath() : 0\0003\0003_0001
			//layer.getTilePathEX(str) : D:\XDServer\tile\0\0001\0001_0007.dds
			//System.out.println("Node 확인1:"+node.getNodePath());
			//System.out.println("Node 확인2:"+layer.getTilePathEX(node.getNodePath()));
			
			byte [] res = layer.getFileBinaries(layer.getTilePathEX(node.getNodePath())); 
			//System.out.println("Node 확인3:"+res);
			if(res==null) return false;
			if(this.responseBinary(resp, res)) {
				Com.fc.debugFormat("Send data : %d",res.length);
				res = null;
			}
			else {
				logger.info("NG:302:Can't make a response binaries.");
				res = null; layer = null;
				return false;
			}
		}else {
			layer = null;
			return false;
		}
		layer = null;
		return true;
	}
	
	public boolean loadingAndResponseImage(HttpServletResponse resp, Node node) throws ServletException, IOException{
		Layer layer = Com.conf.findByLayerName(node.getLayerName());
		if(layer == null ) 	return false;
		
		if(layer.chkServiceLevel(node.getLevel())) {
			byte [] res = layer.getFileBinaries(layer.getTilePathEXT(node.getImagePath(),"jpg"));
			if(res==null) return false;
			if(this.responseBinary(resp, res)) {
				Com.fc.debugFormat("Send data : %d",res.length);
				res = null;
			}
			else {
				logger.info("NG:303:Can't make a response binaries.");
				res = null; layer = null;
				return false;
			}
		}else {
			layer = null;
			return false;
		}
		layer = null;
		return true;
	}
	
	/**
	 * 프록시 
	 */
	public void proxy(HttpServletRequest request,
			HttpServletResponse response, String urlParam) throws ServletException, IOException {
		
		logger.info(urlParam); 
		if (urlParam == null || urlParam.trim().length() == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		boolean doPost = request.getMethod().equalsIgnoreCase("POST");
		URL url = new URL(urlParam);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			if (!key.equalsIgnoreCase("Host")) {
				http.setRequestProperty(key, request.getHeader(key));
			}
		}
			
		http.setDoInput(true);
		http.setDoOutput(doPost);
	
		byte[] buffer = new byte[8192];
		int read = -1;
	
		if (doPost) {
			OutputStream os = http.getOutputStream();
			ServletInputStream sis = request.getInputStream();
			while ((read = sis.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.close();
		}
	
		InputStream is = http.getInputStream();
		response.setStatus(http.getResponseCode());
		response.setContentType("charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		Map headerKeys = http.getHeaderFields();
		Set keySet = headerKeys.keySet();
		Iterator iter = keySet.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = http.getHeaderField(key);
			if (key != null && value != null) {
				response.setHeader(key, value);
				//System.out.println(key +":"+ value);
			}
		}
		
		ServletOutputStream sos = response.getOutputStream();
		response.resetBuffer();
		while ((read = is.read(buffer)) != -1) {
			sos.write(buffer, 0, read);
		}
		response.flushBuffer();
		sos.close();
	}
	
}
