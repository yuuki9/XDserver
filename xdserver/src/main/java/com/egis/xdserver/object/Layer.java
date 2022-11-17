package com.egis.xdserver.object;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egis.xdserver.util.Com;
import com.egis.xdserver.util.FormatConvert;

public class Layer {
	private String type="";
	private String name="";
	private String ext="";
	private String path="";
	private Rect bound = new Rect();
	private int min = 0;
	private int max = 0;
	private String text = null;
	public boolean disable = false;
	private boolean direct = false;
	private boolean flag = false;
	private static final String BINARY ="application/octet-stream";
	private static final String JPG = "image/jpeg";
	private static final String PNG = "image/png";
	private String mType=BINARY;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static int BUFFER_SIZE = 1024;	//for loading file
	
	public Layer(String name, String type, String path, String ext,boolean direct, boolean flag){
		logger.info("CLayer init : ");
		this.type = type;
		this.name = name;
		this.path = path;
		this.ext = ext;
		this.direct = direct;
		this.flag = flag;
		this.bound = new Rect(0, 0, 0, 0);
		logger.info("bound");
		setMimeType(ext);
		Com.fc.debugFormat("Create Layer\n\tTypeName : %s\n\tLayerName : %s\n\tPath: %s\n\tExt: %s\n\tDirect Service: %s\n\tFlag: %s", type,name,path,ext,Boolean.toString(direct),Boolean.toString(flag));
	}
	public Layer(){	
		type = name = ext = path = "";
	}
	
	public void setType(String type){
		this.type = type;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setExt(String ext){
		this.ext = ext;
	}

	public void setPath(String path){
		this.path = path;
	}

	public void setLevelRange(int min, int max){
		if(min>=max) min = max;
		this.min = min;
		this.max = max;
	}

	public void setBound(double ix, double iy, double ax, double ay){
		this.bound.setBound(ix, iy, ax, ay);
	}
	public void setBound(Rect rect){
		this.bound.setBound(rect);
	}
	
	public void setText(String val){
		this.text = val;
	}
	
	public void setDirect(boolean val){
		this.direct = val;
	}
	
	public void setFlag(boolean val){
		this.flag = val;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getExt(){
		return this.ext;
	}
	
	// type 경로
	public String getPath(){
		return this.path;
	}
	
	// 레이어 전체경로
	public String getBasePath(){
		return String.format("%s%s%s",this.path,Com.sign,this.name);
	}
	
	public int [] getLevelRange(){
		int [] res = new int [2];
		res[0] = this.min;
		res[1] = this.max;
		return res;
	}
	
	public Rect getBound(){
		return this.bound;
	}
	
	public String getText(){
		return this.text;
	}
	
	// 타일 파일 경로
	public String getTilePath(int lv, int x, int y, int z){
		String stry = setFormated(y);
		String strx = setFormated(x);
		String strz = setFormated(z);
		if(strx==null||stry==null)return null;
		if(z == -1)
			return String.format("%s%s%s%s%d%s%s%s%s_%s.%s", this.path,Com.sign,this.name,Com.sign,lv,Com.sign,stry,Com.sign,stry,strx,this.ext);
		else return String.format("%s%s%s%s%d%s%s%s%s_%s_%s.%s", this.path,Com.sign,this.name,Com.sign,lv,Com.sign,stry,Com.sign,stry,strx,strz,this.ext);
	}
	
	// 파일 경로
	public String getFilePath(int lv, int x, int y, String file){
		String stry = setFormated(y);
		String strx = setFormated(x);
		if(strx==null||stry==null)return null;
		String res =  String.format("%s%s%s%s%d%s%s%s%s_%s%s%s", this.path,Com.sign,this.name,Com.sign,lv,Com.sign,stry,Com.sign,stry,strx,Com.sign,file);
		return res;
	}

	// 타일이미지 파일 경로
	public String getTileImagePath(int lv, int x, int y,int num){
		String stry = setFormated(y);
		String strx = setFormated(x);
		
		if(strx==null||stry==null)return null;
		return String.format("%s%s%s%s%d%s%s%s%s_%s_%d.%s", this.path,Com.sign,this.name,Com.sign,lv,Com.sign,stry,Com.sign,stry,strx,num,"jpg");
	}	
	
	// xdo 파일 경로 >> x,y경위도 좌표 ->  타일구조에 맞게 변환
	public String getFilePathByLonLatXdo(int level, double lon, double lat, String file) {
		if(lon>360 ||lon<0|| 180<lat||lat<0) return null;
		double a = 180/(Math.pow(2, level)*5);
		int idx = (int) Math.floor((180+lon)/a);
		int idy = (int) Math.floor((90+lat)/a);
		return String.format("%s%d%s%s%s%s_%s%s%s", this.getBasePath(),level,Com.sign,setFormated(idy),Com.sign,setFormated(idy),setFormated(idx),Com.sign,file);
	}
	
	// xdo 파일 경로 >> x,y경위도 좌표 ->  타일구조에 맞게 변환
	public String getPathByLonLatXdo(int level, double lon, double lat) {
		if(lon>360 ||lon<0|| 180<lat||lat<0) return null;
		double a = 180/(Math.pow(2, level)*5);
		int idx = (int) Math.floor((180+lon)/a);
		int idy = (int) Math.floor((90+lat)/a);
		
		return String.format("%s%d%s%s%s%s_%s%s", this.getBasePath(),level,Com.sign,setFormated(idy),Com.sign,setFormated(idy),setFormated(idx),Com.sign);
	}
	
	
	public static String setFormated(int num){
		if(num<10000)
			return (String)String.format("%04d", num);
		else
			return (String)String.format("%08d", num);
	}
	
	
	public String getDirPath(int lv, int y) {
		return String.format("%s%s%s%s%d%s%s", this.path,Com.sign,this.name,Com.sign,lv,Com.sign,setFormated(y));
	}

	public String getToString(){
		return "<Layer"+
				Config.Attr("Name",this.name)+
				Config.Attr("Type",this.type)+
				Config.Attr("MinLevel",Integer.toString(this.min))+
				Config.Attr("MaxLevel",Integer.toString(this.max))+
				"/>";
	}
	
	public boolean getDirect(){return this.direct;}
	public boolean getFlag(){return this.flag;}
	
	private void setMimeType(String ext){

		try{
			if(ext.equals("") || ext==null||ext.equals(null)) {
				ext="";
				Com.fc.debugFormat("[CLayer::setMimeType] : 0163 : Null or kara : set ext kara");				
				return;
			}
			if(ext.equals("jpg"))this.mType= JPG;
			else if(ext.equals("png"))this.mType= PNG;
			else this.mType=BINARY;
		}catch(Exception e){
			//Com.fc.errorFormat("[CLayer::setMimeType] : 0166 : Exeption : ext[%s] : %s",ext,e.getMessage());
			logger.error("[CLayer::setMimeType] : 0166 : Exeption : ext[%s] : %s",ext,e.getMessage());
		}

	}
	public String getMimetype(){
		return this.mType;
	}

	//141226 KIAN : 서비스범위확인
	public boolean chkServiceLevel(int ilv) {

		if(this.min<=ilv&&ilv<=this.max) return true;
		return false;
	}
	
	// 폴더 경로 데이터 유뮤 판단
	public byte[] getFileBinaries(String path) throws IOException {

		Com.fc.debugFormat("REQ FILE : %s", path);
			File f = new File(path);
			if(f==null) logger.info("file is null");
			if(f.isFile()){
				return getFileBytes(f);
			}else		logger.info("File is not exist.");
		return null;
	}
		
	public byte[] getFileBytes(File file) throws IOException  {
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    try {
	        ous = new ByteArrayOutputStream();
	        try {
	        ios = new FileInputStream(file);
	        }catch(IOException e) {
	        	e.printStackTrace();
	        }
	        int read = 0;
	        try {
	        while ((read = ios.read(buffer)) != -1)
	            ous.write(buffer, 0, read);
	        }catch(IOException e) {
	        	e.printStackTrace();
	        }
	    } finally {
	        try { if (ous != null) ous.close();} catch (IOException e) {e.printStackTrace();

	        }
	        try { if (ios != null) ios.close();} catch (IOException e) {e.printStackTrace();

	        }
	    }
	    ios.close();ous.close();
	    
	    byte[] res =  ous.toByteArray();
	    buffer = null; ios = null; ous=null;
	    return res;
	}
	
	public String getTilePathEX(String nodePath) {
		return String.format("%s%s%s%s%s.%s", this.path,Com.sign,this.name,Com.sign,nodePath,this.ext);
	}

	public String getTilePathEXT(String nodePath, String setExt) {
		return String.format("%s%s%s%s%s.%s", this.path,Com.sign,this.name,Com.sign,nodePath,setExt);
	}
	
	public String getFilePathFast(String _path,int level, int idx, int idy) {
		return String.format("%s%s%d%s%s%s%s_%s", _path,Com.sign, level,Com.sign,setFormated(idy),Com.sign,setFormated(idy),setFormated(idx));
	}

}
