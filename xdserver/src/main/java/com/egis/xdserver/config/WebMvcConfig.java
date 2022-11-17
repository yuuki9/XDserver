package com.egis.xdserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 강민아
 * @date 2022. 6. 3.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	/**
	 * CORS 정보 추가
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		 registry.addMapping("/**").allowedOrigins("*")
		 		.allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name());
	}
	

}