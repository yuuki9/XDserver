package com.egis.xdserver.object;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Type {
	
	private String name="";
	private String ext="";
	private String path="";		
	private boolean direct = false;
	private boolean flag = false;
	
	public Type(String name, String ext, String path,boolean direct, boolean flag){
		this.name = name;
		this.path = path;
		this.ext = ext;
		this.direct = direct;
		this.flag = flag;
	}

}
