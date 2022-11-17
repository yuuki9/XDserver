package com.egis.xdserver.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.egis.xdserver.server.ServerState;
import com.jcraft.jsch.JSchException;

@Controller
public class MoniteringController {
				
	@PostMapping("/serverState")
	@ResponseBody 
    public ServerState serverState() throws IOException {
		ServerState server = new ServerState();

		return server;
	}
	
	@PostMapping("/getTomcatLog")
	@ResponseBody
    public String getXDserverLog(HttpServletRequest request , HttpServletResponse response, Model model) throws IOException, JSchException {
		ServerState server = new ServerState();
		String lines = request.getParameter("lines");
		
		//model.addAttribute("lines", server.getXDserverLog(lines));
		return server.getXDserverLog(Long.valueOf(lines));
	}
}
