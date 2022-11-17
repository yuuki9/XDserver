package com.egis.xdserver.object;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.egis.xdserver.util.Com;

import lombok.Builder;
import lombok.Data;

@Data
public class UserInfo {
		
		public String path = "";
		private static boolean m_OS = false;			//0:AIX(UNIX), 1:Window kind
		public boolean LogDebug = false;
		private String pw;
		private String id;
		private String roles;
		private boolean using;
		private List<UserInfo> userList;
		//private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		private String mapinfo_data = "";
		

		public UserInfo(String path, String insertedId){
			
			
			if(path == null) {
				//logger.error("is not exist the Authentication file path : "+path);
				System.exit(1);
				
			}
			this.path = path;
			m_OS = setOS();
			this.loadUserFile(setOSdiv(this.path) ,insertedId);	
		}
		
		@Builder
		public UserInfo(String id, String pw, String roles, boolean using) {
			this.id = id;
			this.pw = pw;
			this.roles = roles;
			this.using = using;
			
		}

		public void reload() {
			this.loadUserFile(setOSdiv(this.path), id);
		}
		
	    private boolean setOS() {
	    	String osName = System.getProperty("os.name");
	    	//logger.info(String.format("This System OS : %s",osName));		// Windows 7 ; AIX
			return osName.contains("Windows");
	    }
		
		public static String setOSdiv(String str) {
			String res=str;
			if(m_OS){
				if(str.substring(0,1).equals("/"))
					res = str.substring(1);
				
				res = res.replace("/","\\");			
				res = res.replace("%20"," ");
				Com.sign = "\\";
			}
			else {
				res = str.replace("\\","/");
				Com.sign = "/";
			}
			return res;
		}
		
		public void loadUserFile(String xmlPath ,String insertedId){
			try{
				File file = new File(xmlPath);
				if(!file.exists()){
					//logger.error("[FAILD] Can't find : "+xmlPath);
					System.exit(1);
					return;
				}			
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    Document doc = builder.parse(file);
			    doc.getDocumentElement().normalize();
			    getUsersFromXML(doc.getElementsByTagName("users"), insertedId);	
			   
			}
			catch (Exception e){
				e.printStackTrace();
				//logger.error("[NG] Exception");
				System.exit(1);
				return;
			}
		}
		
		private void getUsersFromXML(NodeList users, String insertedId) {
			
			int len = users.getLength();
	
			if(len==0) {
				return;
			}
			Node node_Config = users.item(0);
			if(node_Config.getNodeType() == Node.ELEMENT_NODE){
				Element element_config = (Element)node_Config;				
				getUserFromXML(element_config.getElementsByTagName("info"), insertedId);						
			}	
		}	

		public String getParam(Element val, String name){
			String res = null;
			Attr load = val.getAttributeNode(name);
			
			if(load != null) {
				res = load.getValue();
				if(res==""||res==null) {
					res = null;
				}
			}
			
			return res;
		}
		
		private boolean getParamBool(Element elm,String tx){
			String get = getParam(elm,tx);
			if(get==null) return false; 
			return Boolean.valueOf(get);
		}
		
		
		private void getUserFromXML(NodeList userList , String insertedId) {
			// <User>목록으로부터 유저정보를 취득
		    int len= userList.getLength();
		    if(len > 0) {
			    for(int i=0;i<len;i++){
			    	Node info = userList.item(i);
			    	if(info.getNodeType() == Node.ELEMENT_NODE ){			 	    			
			    		getCheckUserPw( (Element) info, insertedId);
			    		
			    	}
			    }
		    }else {
		    	return;
		    }
		}			
		
		public UserInfo getCheckUserPw(Element userInfo, String insertedId) {			
			
			String id = getParam(userInfo, "id");
			String password = getParam(userInfo, "password");
			boolean using = getParamBool(userInfo, "using");
			String role = getParam(userInfo, "role");

			if (insertedId.equals(id)) {
				this.id = id;
				this.pw = password;
				this.using = using;
				this.roles = role;

				return this;

			}

			return null;
		}
			 
}
