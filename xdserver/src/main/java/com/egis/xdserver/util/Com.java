package com.egis.xdserver.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.egis.xdserver.manager.LayerManager;
import com.egis.xdserver.manager.TypeManager;
import com.egis.xdserver.object.Config;

/**
 * @author 강민아
 * @date 2022. 6. 23.
 * 공통 클래스
 * 
 * 해당 클래스는 서버 구동시 바로 실행됨
 */
@Component
public class Com implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static Config conf;
	public static LayerManager lm;
	public static TypeManager tm;
	public static FormatConvert fc;
	public static String sign = "/";
	public static String version = "1.0";
	public static boolean OperatingSystemType = true;
	public static String authenticationPath;
	public static String strConfigPath;
	public static String oriFilePath;
	
	/** 
	 * 초기설정
	 **/
    @Override
    public void run(String... args) throws Exception {
		
		logger.info("=================================");
		logger.info("XDSERVER_V."+version);
		
		strConfigPath = this.getClass().getClassLoader().getResource("layer-config.xml").getPath();
		// os type
		OperatingSystemType = System.getProperty("os.name").contains("Windows")? true:false;
		strConfigPath = setOSdiv(strConfigPath);	
		System.out.println(strConfigPath);
		
		fc = new FormatConvert();
		lm = new LayerManager(); 
		tm = new TypeManager();
		
		conf = new Config(strConfigPath);
		
		lm.strMapInfo = lm.createMapInfo();
		
		logger.info("Server is running...");
    }
	
	public static String setOSdiv(String str) {
		String res=str;
		if(OperatingSystemType){
			if(str.substring(0,1).equals("/")) res = str.substring(1);
			res = res.replace("/","\\");
		}
		else res = str.replace("\\","/");
		return res;
	}
}
