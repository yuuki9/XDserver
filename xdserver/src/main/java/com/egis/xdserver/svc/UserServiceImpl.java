package com.egis.xdserver.svc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.egis.xdserver.object.UserInfo;
import com.egis.xdserver.util.Com;

@Service
public class UserServiceImpl implements UserService{
	

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<UserInfo> getUserList() throws SAXException, IOException, ParserConfigurationException {

		File file = new File(Com.authenticationPath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(file);
	    doc.getDocumentElement().normalize();  
		getUsersFromXML(doc.getElementsByTagName("users"));
			
		return getUsersFromXML(doc.getElementsByTagName("users"));
	}
	
	public List<UserInfo> getUsersFromXML(NodeList users) {
		int len = users.getLength();
		
		if(len==0) {
			return null;
		}
		Node node_Config = users.item(0);
		if(node_Config.getNodeType() == Node.ELEMENT_NODE){
			Element element_config = (Element)node_Config;				
			return getUserFromXML(element_config.getElementsByTagName("info"));						
		}
		
		return null;
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
	
	private List<UserInfo> getUserFromXML(NodeList userList) {
		// <User>목록으로부터 유저정보를 취득
	    List<UserInfo> list = new ArrayList<>();
		int len= userList.getLength();
	    if(len > 0) {
		    for(int i=0;i<len;i++){
		    	Node info = userList.item(i);
		    	if(info.getNodeType() == Node.ELEMENT_NODE ){			 	    			
		    		list.add(getUserInfo((Element) info));
		    		
		    	}
		    }
	    }else {
	    	return null;
	    }
	    return list;
	}
	
	public UserInfo getUserInfo(Element userInfo) {			
		
		String id = getParam(userInfo, "id");
		String password = getParam(userInfo, "password");
		String role = getParam(userInfo, "role");
		boolean using = getParamBool(userInfo, "using");
				
		UserInfo info = new UserInfo(id,password,role,using);
				
		return info;
	}
	
	/*
	 * 계정 생성
	 */
	@Override
	public void createAccount(Map map) {			
		String xmlPath = Com.authenticationPath;
		System.out.println(xmlPath);
		
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
			
		    XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();	
			XPathExpression expr = xpath.compile("//users/user[@name='userList']");

			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node typeNode = result.item(0);
			Element typeElement = (Element) typeNode;
			
			Element LayersEle = doc.createElement("info");
			System.out.println((String) map.get("id"));
			LayersEle.setAttribute("id", (String) map.get("id"));
			LayersEle.setAttribute("password", (String) map.get("pw"));
			LayersEle.setAttribute("using", "true");
			LayersEle.setAttribute("role",(String) map.get("roles"));

			typeElement.appendChild(LayersEle);
			
			DOMSource source = new DOMSource(doc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult sr  =  new StreamResult(sw);
			
			transformer.transform(source, sr);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
			writer.write(sw.toString());
			writer.flush();
			writer.close();
			
		}catch (Exception e) {
			System.out.println(e);
		}
	}
}

