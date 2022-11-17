package com.egis.xdserver.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egis.xdserver.svc.BaseService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 강민아
 * @date 2022. 5. 30.
 * MainController 기존 
 */

@Controller
public class ViewController {

	// map view
	@GetMapping("/map")
    public String map() throws Exception {
    	 return "map";
    }
	

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
						@RequestParam(value = "exception", required = false) String exception,
						Model model) {
		
		/* 에러와 예외를 모델에 담아 view resolve */
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
		return "login";
	}
    
	// web view
	@GetMapping("/admin/web")
    public String web(@AuthenticationPrincipal User user, Model model) throws Exception {
		
		model.addAttribute("id", user.getUsername()); 
		model.addAttribute("roles", user.getAuthorities()); 

		return "web";
    }
	
}
