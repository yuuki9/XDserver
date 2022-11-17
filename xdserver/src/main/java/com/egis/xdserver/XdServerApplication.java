package com.egis.xdserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.egis.xdserver.util.Com;


@SpringBootApplication
public class XdServerApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(XdServerApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(XdServerApplication.class, args);
	}
	

}
