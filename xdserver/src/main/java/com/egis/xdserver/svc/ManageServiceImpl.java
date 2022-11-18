package com.egis.xdserver.svc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import com.egis.xdserver.util.Com;

import com.egis.xdserver.object.Config;
import com.egis.xdserver.object.Layer;
import com.egis.xdserver.object.Type;
@Service
public class ManageServiceImpl implements ManageService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String strConfigPath = null;

	public List<Type> getTypeListALL()throws Exception{

		reload();
		
		List<Type> typeList = new ArrayList<Type>();
		
		HashMap<String, Type> types = Com.tm.getTypeList();
		
		Iterator<String> it = types.keySet().iterator(); 
		
		while(it.hasNext()) {
			String key = it.next();
			Type getType = Com.tm.getTypeByName(key);
			typeList.add(getType);
		}
		return typeList;
	 }
	public Type getTypeByName(String name)throws Exception{

		reload();

		Type type = Com.tm.getTypeByName(name);
		
		return type;
	 }
	
	public List<Layer> getLayerListALL()throws Exception{

		reload();
		
		List<Layer> layerList = new ArrayList<Layer>();
		
		HashMap<String,Layer> layers = Com.lm.getLayerList();
		
		Iterator<String> it = layers.keySet().iterator(); 

		while(it.hasNext()) {
			String key = it.next();
			Layer getLayer = Com.lm.getLayerByName(key);
			layerList.add(getLayer);
		}
		return layerList;
	 }
	
	public Layer getLayerByName(String name)throws Exception{

		reload();

		Layer layer = Com.lm.getLayerByName(name);
		
		return layer;
	 }
	
	public int getLayerListCnt()throws Exception{

		reload();
		
		HashMap<String,Layer> layers = Com.lm.getLayerList();
		int layetCount = layers.size();
		return layetCount;
	 }
	
	public String getPath()throws Exception{

		reload();
		
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				logger.error("[FAILD] Can't find : "+xmlPath);
				System.exit(1);
				return "파일을 찾지 못했다";
			}
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
		    
		    System.out.println(getParam(((Element) doc.getElementsByTagName("Type").item(0)),"name"));
		    
		}
		catch (Exception e){
			e.printStackTrace();
			logger.error("[NG] Exception");
			System.exit(1);
			return "예외_"+e;
		}
		return xmlPath;
	 }
	
	public int getTypeLength(String name) {

		reload();
		
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return 0;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
	
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();

			XPathExpression expr = xpath.compile("//Type[@name='"+name+"']");

			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			return result.getLength();
		}catch (Exception e) {
			System.out.println(e);
		}
		return 0;
	}
	
	public void addType(Map<?, ?> type) {
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
			
			NodeList LayersNodeList =  doc.getElementsByTagName("Layers");
			System.out.println(LayersNodeList.getLength());
			Node LayersNode = LayersNodeList.item(0);
			Element LayersEle = (Element) LayersNode;
				
			Element typeElement = doc.createElement("Type");
			typeElement.setAttribute("name", (String) type.get("name"));
			typeElement.setAttribute("path", (String) type.get("path"));
			typeElement.setAttribute("ext", (String) type.get("ext"));
			typeElement.setAttribute("direct","false" );
			typeElement.setAttribute("flag","false" );
			LayersEle.appendChild(typeElement);

			DOMSource source = new DOMSource(doc);
	
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult sr  =  new StreamResult(sw);
			
			transformer.transform(source, sr);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
			writer.write(sw.toString());
			writer.flush();
			writer.close();
			
			reload();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void editType(Map<?, ?> type){
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();

		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			
			XPathExpression expr = xpath.compile("//Type[@name='"+(String) type.get("beforeName")+"']");
			
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node typeNode = result.item(0);
			Element typeElement = (Element) typeNode;
			
			typeElement.setAttribute("name", (String) type.get("name"));
			typeElement.setAttribute("path", (String) type.get("path"));
			typeElement.setAttribute("ext", (String) type.get("ext"));
			typeElement.setAttribute("direct",(String) type.get("direct") );
			typeElement.setAttribute("flag",(String) type.get("flag") );

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
			
			reload();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public int getLayerLength(String name) {

		reload();
		
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return 0;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
	
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();

			XPathExpression expr = xpath.compile("//Layer[@name='"+name+"']");
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			return result.getLength();
		}catch (Exception e) {
			System.out.println(e);
		}
		return 0;
	}
	
	
	public void addLayer(Map<?, ?> layer) {
		String xmlPath = Com.strConfigPath;
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
			
			XPathExpression expr = xpath.compile("//Type[@name='"+(String) layer.get("type")+"']");
			
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node typeNode = result.item(0);
			Element typeElement = (Element) typeNode;
			
			Element LayersEle = doc.createElement("Layer");
			LayersEle.setAttribute("name", (String) layer.get("name"));
			LayersEle.setAttribute("max", (String) layer.get("max"));
			LayersEle.setAttribute("min", (String) layer.get("min"));
			LayersEle.setAttribute("text",(String) layer.get("text") );

	        LocalDate now = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	        String formatedNow = now.format(formatter);
			LayersEle.setAttribute("date",formatedNow);
			
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
			
			reload();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void editLayer(Map<?, ?> layer){
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			
			XPathExpression expr = xpath.compile("//Layer[@name='"+(String) layer.get("beforeName")+"']");
			
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			Node layerNode = result.item(0);
			Element layerElement = (Element) layerNode;
			
			layerElement.setAttribute("name", (String) layer.get("name"));
			layerElement.setAttribute("max", (String) layer.get("max"));
			layerElement.setAttribute("min", (String) layer.get("min"));
			layerElement.setAttribute("text",(String) layer.get("text") );	        

	        LocalDate now = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	        String formatedNow = now.format(formatter);
			System.out.println(formatedNow);
			layerElement.setAttribute("date",formatedNow);
			
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
			
			reload();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	

	public void removeType(String name) {
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();

		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expr = xpath.compile("//Type[@name='"+name+"']");
			
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			System.out.println(result.getLength());
			Node typeNode = result.item(0);
			Element typeElement = (Element) typeNode;
			
			NodeList LayersNodeList =  doc.getElementsByTagName("Layers");
			System.out.println(LayersNodeList.getLength());
			Node LayersNode = LayersNodeList.item(0);
			Element LayersElement = (Element) LayersNode;
			LayersElement.removeChild(typeElement);

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

			reload();
			
		}catch (Exception e) {
			System.out.println(e);
		}
	}

	public void removeLayer(String name) {
		String xmlPath = Com.strConfigPath;
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				System.out.println("파일없음");
				System.exit(1);
				return;
			}
			System.out.println("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
		    
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expr = xpath.compile("//Layer[@name='"+name+"']");
	
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			System.out.println(result.getLength());
			Node layerNode = result.item(0);
			Element layerElement = (Element) layerNode;
			Element typeElement = (Element) layerElement.getParentNode();

			typeElement.removeChild(layerElement);
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

			reload();
			
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void reload(){
		new Config(Com.strConfigPath);
	}
	
	public String getParam(Element  val, String name){
		String res = null;
		Attr load = val.getAttributeNode(name);
		
		if(load != null) {
			res = load.getValue();
			if(res==""||res==null) {
				res = null;
			}
			if(res==null)
				logger.info("[NG] get Parameter : "+name+" : "+res);
			else logger.info("[OK] get Parameter : "+name+" : "+res);
		}
		
		return res;
	}
}
