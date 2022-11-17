package com.egis.xdserver.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.Data;

@Data
public class ServerState {
	
	//톰캣위치
	//데이터 디렉토리 실제 파일이 위치한 경로
	//public String dataDirectory;	
	public String osVersion; //OS 버전
	public String osArch; //OS 아키텍쳐
	public String osName; //OS 이름
	//MEMORY 관련 저장 변수
	public long heapInit;
	public String usageHeap;
	public String usageNonHeap;
	//private double freePhysicalMemory; //사용 가능ㅎ
	//private double usagePhysicalMemory;
	//private double usageMemoryPercent;
	//private double idleMemoryPercent;
	 
	static Session session = null;
	
	public ServerState() {

		// OS 정보
		OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		this.osVersion = osBean.getVersion();
		this.osArch = osBean.getArch();
		this.osName = osBean.getName();

		// JVM 상태
		RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
		// 메모리 상태
		MemoryMXBean membean = ManagementFactory.getMemoryMXBean();

		this.usageHeap = String.valueOf(membean.getHeapMemoryUsage());
		this.usageNonHeap = String.valueOf(membean.getNonHeapMemoryUsage());

		// long heapInit = heap.getInit();
		// long heapCommit = heap.getCommitted();
		// long heapMax = heap.getMax();

		// long nonheapUsed = nonheap.getUsed();
		//HashMap<String, String> map = new HashMap<>();
		//map.put("초기 상태의 메모리", String.valueOf(heapInit / (1024 * 1024) / 1000.0));
		// map.put("사용 중인 heep메모리", String.valueOf(heapUsed / (1024*1024) / 1000.0));
		// map.put("사용 중인 nonheep메모리", String.valueOf(nonheapUsed / (1024*1024) /
		// 1000.0));
		// map.put("JVM에 할당된 메모리", String.valueOf(heapCommit / (1024*1024) / 1000.0));
		// map.put("총 메모리 양", String.valueOf(heapMax / (1024*1024) / 1000.0));
	}
			
	//디스크 용량
	public String[] getDiskSpace() {
		
		File[] roots = File.listRoots();
		for(File root: roots) {
	    	double hddTotal = Math.round( root.getTotalSpace()/(1024*1024) / 1000.0 );
	    	double hddUsage = Math.round( (root.getTotalSpace() - root.getUsableSpace())/(1024*1024)/ 1000.0) ;
	    	double hddIdle = Math.round( root.getUsableSpace()/(1024*1024) / 1000.0 );
	    	double hddUsagePercent = Math.round( Double.valueOf(root.getTotalSpace() - root.getUsableSpace())/ Double.valueOf(root.getTotalSpace()) * 100);
	    	double hddIdlePercent = Math.round( Double.valueOf(root.getUsableSpace()) / Double.valueOf(root.getTotalSpace()) * 100);
	 	
	    }
		return null;
	}
	
	public String toMB(long size) {
		return String.valueOf((int) (size / (1024 * 1024)));
	}
	
	//톰캣로그 (OS 별로 window, linux(SSH)
	public String getXDserverLog(Long lines) throws JSchException, IOException{
		
		String tomcatLog = "";
		if (System.getProperty("os.name").contains("Windows")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\apache-tomcat-9.0.64\\logs\\localhost.2022-08-18.log"), "euc-kr"));
			String text = "";
			int i = 0;
			while ((tomcatLog = reader.readLine()) != null) {
				text += "\r\n" + tomcatLog;
				i++;
				if(i == lines) break;
			}

			reader.close();
					
			return text;
		} else {
			//운영체제가 리눅스 일 때 
			JSch jsch = new JSch();
			session = jsch.getSession("egis", "localhost", 22);
			session.setPassword("1234");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			//
			Channel channel = session.openChannel("exec"); // 채널접속
			ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
			channelExec.setPty(true);
			channelExec.setCommand("whoami");
			// 콜백을 받을 준비.
			InputStream in = channel.getInputStream();

			((ChannelExec) channel).setErrStream(System.err);
			channel.connect(); // 실행

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					tomcatLog = new String(tmp, 0, i);
					//System.out.println(tomcatLog.toString());
				}
				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
					// logger.error(ee.getMessage());
				}
			}
			channel.disconnect();
			// }else {
			// 리눅스 서버 일 때
			// System.out.println("linux");

			return tomcatLog;
		}
	}
	
}
