package com.egis.xdserver.svc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
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
	public HashMap<String,String> createAccount(Map<?,?> map) throws Exception{			
		
		HashMap<String,String> resultMap = new HashMap<>();
		
		
		if(userChecker((String) map.get("id"))) {
				resultMap.put("msg", "중복된 아이디 입니다");
				return resultMap;
		}
		
		String xmlPath = Com.authenticationPath;
			
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				resultMap.put("msg", "파일없음");
				System.exit(1);
				return resultMap;
			}
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
			
		    XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();	
			XPathExpression expr = xpath.compile("//users/user");

			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node typeNode = result.item(0);
			Element infoElement = (Element) typeNode;
			
			Element infoEle = doc.createElement("info");
			
			infoEle.setAttribute("id", (String) map.get("id"));
			infoEle.setAttribute("password", passwordEncoder.encode((String) map.get("pw")));
			infoEle.setAttribute("using", "true");
			infoEle.setAttribute("role",(String) map.get("roles"));

			infoElement.appendChild(infoEle);
			
			DOMSource source = new DOMSource(doc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult sr  =  new StreamResult(sw);
			
			transformer.transform(source, sr);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("/D:/xdservergit/xdserver/src/main/resources/user-authentication.xml",false));
		
			writer.write(sw.toString());
			writer.flush();
			writer.close();
			
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(xmlPath,false));
			
			writer2.write(sw.toString());
			writer2.flush();
			writer2.close();
			
			//reload();
		}catch (Exception e) {
			System.out.println(e);
		
		}
		
		resultMap.put("msg", "계정이 등록 되었습니다");
		return resultMap;
	}
	/**
	 * 계정 수정하기 
	 */
	
	@Override
	public HashMap<String, String> updateAccount(Map<?, ?> map) {
		HashMap<String,String> resultMap = new HashMap<>();
		if(!comparePassword((String) map.get("id"), (String) map.get("pw"))) {
			
			resultMap.put("msg", "기존 비밀번호가 다름니다");
			return resultMap;	
		}
		
		String xmlPath = Com.authenticationPath;
		
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				resultMap.put("msg", "파일없음");
				System.exit(1);
				return resultMap;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
			
		    XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			
			XPathExpression expr = xpath.compile("//info[@id='"+(String) map.get("id")+"']");
	
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node typeNode = result.item(0);
			Element infoElement = (Element) typeNode;
			
			infoElement.setAttribute("id", (String) map.get("id"));
			infoElement.setAttribute("password", passwordEncoder.encode((String) map.get("Nw")));
			//infoElement.setAttribute("using", "true");
			infoElement.setAttribute("role",(String) map.get("roles"));
	

			
			DOMSource source = new DOMSource(doc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult sr  =  new StreamResult(sw);
			
			transformer.transform(source, sr);
			
			//BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
			//writer.write(sw.toString());
			//writer.flush();
			//writer.close();
			
		}catch (Exception e) {
			System.out.println(e);
		
		}
		resultMap.put("msg", "비밀번호 변경 완료");
		
		return resultMap;
	}
	
	/**
	 * 중복 아이디 체커 
	 */
	public boolean userChecker(String id) throws Exception {
		
		List<UserInfo> list = this.getUserList();
		for(UserInfo info : list) {
			if(id.equals(info.getId())) return true;
		}
				
		return false;
	}

	/**
	 * 실제 xml 파일로 쓰기
	 */
	public void writeXML(String xmlPath, StringWriter sw) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("/D:/xdservergit/xdserver/src/main/resources/user-authentication.xml",false));
		
		writer.write(sw.toString());
		writer.flush();
		writer.close();
		
	}
	
	//기존 비밀번호 비교
	public boolean comparePassword(String id, String pw) {
		UserInfo auth = new UserInfo(Com.authenticationPath, id);
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		
		return bcrypt.matches(pw, auth.getPw());
	}

}

