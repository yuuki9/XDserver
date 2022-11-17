package com.egis.xdserver.object;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.egis.xdserver.util.Com;

public class Config {
	
	public String path = "";
	private static boolean m_OS = false;			//0:AIX(UNIX), 1:Window kind
	public boolean LogDebug = false;
	private Layer[][] layers;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String mapinfo_data = "";
	

	public Config(String path){

		if(path == null) {
			logger.error("is not exist the config file path : "+path);
			System.exit(1);
		}
		this.path = path;
		m_OS = setOS();
		this.loadXmlFile(setOSdiv(this.path));	
	}
	
	public void reload() {
		this.loadXmlFile(setOSdiv(this.path));
	}
	
    private boolean setOS() {
    	String osName = System.getProperty("os.name");
    	logger.info(String.format("This System OS : %s",osName));		// Windows 7 ; AIX
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
		Com.fc.debugFormat("[requestProc::setOSdiv] : 0813 : trans : %s : %s",str,res);
		return res;
	}
	
	public void loadXmlFile(String xmlPath){
		Com.fc.infoFormat("Load Config file : %s",xmlPath);
		Com.lm.clean();
		Com.tm.clean();
		try{
			File file = new File(xmlPath);
			if(!file.exists()){
				logger.error("[FAILD] Can't find : "+xmlPath);
				System.exit(1);
				return;
			}
			logger.info("Loading .........");
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    //xml 파싱완료
		    Document doc = builder.parse(file);
		    doc.getDocumentElement().normalize();
		    getConfigSetting(doc.getElementsByTagName("Config"));	
		    getLayersFromXML(doc.getElementsByTagName("Layers"));
		}
		catch (Exception e){
			e.printStackTrace();
			logger.error("[NG] Exception");
			System.exit(1);
			return;
		}
	}
	
	private void getLayersFromXML(NodeList layers) {
		logger.info("Loading .........<Layers>");
		int len = layers.getLength();
		logger.info("Get Control settings START : "+Integer.toString(len));
		if(len==0) {
			logger.info("Loading .........<Layers> : NG : Empty layers");
			return;
		}
		Node node_Config = layers.item(0);
		if(node_Config.getNodeType() == Node.ELEMENT_NODE){
			Element element_config = (Element)node_Config;
			getTypeFromXML(element_config.getElementsByTagName("Type"));	
		}
		logger.info("Loading .........<Layers> : OK");
	}

	private void getConfigSetting(NodeList nList){
		logger.info("Loading .........<Config>");
		int len = nList.getLength();
		if(len==0)return;
		Node node_Config = nList.item(0);
		if(node_Config.getNodeType() == Node.ELEMENT_NODE){
			Element element_config = (Element)node_Config;
			getSetting(element_config.getElementsByTagName("SET"));
		}
		logger.info("Loading .........<Config> : OK");
	}
	
	private void getSetting(NodeList nList) {
		if(nList.getLength()==0) return;
		Node jdbc = nList.item(0);
    	if(jdbc.getNodeType() == Node.ELEMENT_NODE){
    		Element elmjdbc = (Element)jdbc;
    		boolean use = false;
    		use = getParamBool(elmjdbc,"debug");
    		//Main.log.setDebug(use);    		
    	}
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
	
	private boolean getParamBool(Element elm,String tx){
		String get = getParam(elm,tx);
		if(get==null) return false; 
		return Boolean.valueOf(get);
	}
	private int getParamInt(Element elm,String tx){
		String get = getParam(elm,tx);
		if(get==null)return 0; 
		return Integer.valueOf(get);
		
	}

	private void getTypeFromXML(NodeList typeList) {
		String name=null, path=null, ext=null;
		boolean direct = false , flag = false;
		// <Type>목록으로부터 레이어정보를 취득
		logger.info("Loading .........<Type>");
	    int len=typeList.getLength();
	    
	    if(len>0) {
		    this.layers = new Layer[len][];
		    logger.info("Found & process start : " + Integer.toString(len));
		    for(int i=0;i<len;i++){
		    	Node type = typeList.item(i);
		    	if(type.getNodeType() == Node.ELEMENT_NODE){

		    	    Element typeEl = (Element)type;
					name = getParam(typeEl,"name");
					path = getParam(typeEl,"path");
					ext = getParam(typeEl,"ext");
					direct = getParamBool(typeEl,"direct");
					flag = getParamBool(typeEl,"flag");	

					Type typeLayer = new Type(name,ext,path,direct,flag);
					Com.tm.add(name,typeLayer);
					
		    		Layer[] s = getLayersByType( (Element) type);
		    		this.layers[i] = s;
		    	}
		    }
	    }else {
	    	logger.info("Loading .........<Type> : NG : Empty Type"); return;
	    }
	    Com.fc.infoFormat("Loading .........<Type> : OK : Added %d layers.",len);
	}

	public static String Attr(String name, String val){
		return " "+name+"=\""+val+"\"";
	}		
	
	public Layer [] getLayersByType(Element typeItem){
		String type = null, path=null, ext=null;
		boolean direct = false , flag = false;
		try{
			type = getParam(typeItem,"name");
			path = getParam(typeItem,"path");
			ext = getParam(typeItem,"ext");
			direct = getParamBool(typeItem,"direct");
			flag = getParamBool(typeItem,"flag");	
		}catch(Exception e){
			logger.error(" : getLayersByType : "+e.getMessage());			
		}
		
	    NodeList layerList = ((Element) typeItem).getElementsByTagName("Layer");
	    
	    int len=layerList.getLength();
		Layer [] layers = new Layer[len];
	    
	    Layer got;
	    for(int i=0;i<len;i++){
	    	got=getLayerNode(layerList.item(i), type, path, ext, direct, flag);
	    	if(got!=null) {
	    		layers[i]=got;
		    	Com.lm.add(got.getName(), got);
	    	}
	    }
	    
		return layers;
	}
	
	
	private Layer getLayerNode(Node layer, String type, String path, String ext, boolean direct, boolean flag) {
    	if(layer.getNodeType() == Node.ELEMENT_NODE){
    		Element elm = (Element) layer;
    		String layerName = getValue(elm,"name");
    		Com.fc.debugFormat("[ELEMENT_NODE] layerName : %s",layerName);

			Layer resLayer = new Layer(layerName,type,path,ext,direct,flag);
			String tmp = getParam(elm,"ext");
			if(tmp!=null) resLayer.setExt(tmp);
    		String strBound = getValue(elm,"bound");
    		Rect rect = new Rect(0,0,0,0);
    		if(strBound != null)  		rect.getBoundFromString(strBound);
    		resLayer.setBound(rect);
    		resLayer.setLevelRange(getParamInt(elm,"min"), getParamInt(elm,"max"));
    		resLayer.disable = getParamBool(elm,"disable");

    		String layerText = getValue(elm,"text");
    		resLayer.setText(layerText);
    		
    		return resLayer;
    	}
    	return null;
	}
	
	private String getValue(Element elm, String val){
		Attr a = ((Element) elm).getAttributeNode(val);
		if(a != null) {
			return a.getValue();
		}
		return null;
	}
	
	// config.xml 파일 <Type> 내 <Layer> find
	public Layer findByLayerName(String name){
		for(int i=0,j=0,len = this.layers.length,len2=0;i<len;i++){
			for(j=0,len2=this.layers[i].length;j<len2;j++){
				if(this.layers[i][j].getName().equals(name)){
					return this.layers[i][j];
				}
			}
		}
		Com.fc.debugFormat("[Config::findByLayerName] : 0435 : layerName(%s) : layer can't found.", name);
		return null;
	}
}
