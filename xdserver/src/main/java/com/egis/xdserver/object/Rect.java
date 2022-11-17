package com.egis.xdserver.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rect {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public double ix,iy,ax,ay;
	public Rect(){}
	public Rect(String val){
		this.getBoundFromString(val);
	}
	public Rect(double ix,double iy,double ax,double ay){
		this.ix = ix;
		this.iy = iy;
		this.ax = ax;
		this.ay = ay;
	}

	// 경계범위 취득
	public void getBoundFromString(String val) {
		logger.info(String.format("bound : %s",val));
		String [] splitStr;
		try{
			logger.info("split!");
			splitStr = val.split(",");
			if(!(splitStr.length==4)){
				logger.info("split! faild");
				this.ix=this.iy=this.ax=this.ay=0; return;
			}
			try{
			this.ix = Double.parseDouble(splitStr[0]);
			this.iy = Double.parseDouble(splitStr[1]);
			this.ax = Double.parseDouble(splitStr[2]);
			this.ay = Double.parseDouble(splitStr[3]);
			}catch(NumberFormatException e){
				logger.info("Exception !!! :getBoundFromString");
				this.ix=this.iy=this.ax=this.ay=0;
				logger.error(e.getStackTrace().toString());				
			}
		}catch(Exception e){
			logger.info("Exception !!! :getBoundFromString");
			this.ix=this.iy=this.ax=this.ay=0;
			logger.error(e.getStackTrace().toString());
		}
	}
	
	// 경계범위 취득(배열)
	public void getBoundFromString(String[] splitStr) {
		try{
		this.ix = Double.parseDouble(splitStr[0]);
		this.iy = Double.parseDouble(splitStr[1]);
		this.ax = Double.parseDouble(splitStr[2]);
		this.ay = Double.parseDouble(splitStr[3]);
		}catch(Exception e){
			this.ix=this.iy=this.ax=this.ay=0;
		}
	}
	
	// 경계 설정
	public void setBound(double ix, double iy, double ax, double ay){
		this.ix = ix;
		this.iy = iy;
		this.ax = ax;
		this.ay = ay;
	}
	
	// 경계 설정
	public void setBound(Rect rect){
		this.ix = rect.ix;
		this.iy = rect.iy;
		this.ax = rect.ax;
		this.ay = rect.ay;
	}
	
}
