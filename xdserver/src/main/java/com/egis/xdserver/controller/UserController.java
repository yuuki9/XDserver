package com.egis.xdserver.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.egis.xdserver.object.UserInfo;
import com.egis.xdserver.svc.UserService;
import com.egis.xdserver.util.Com;

@RestController
@RequestMapping("/admin")
public class UserController {

	@Autowired
	public UserService userService;
	
	@PostMapping("/getUserList")
	@ResponseBody 
    public List<UserInfo> getUserList() throws Exception {		
		return userService.getUserList();
	}
	
	@PostMapping("/create/account")
	@ResponseBody 
    public HashMap<String,String> createAccount(@RequestBody Map map) throws Exception {
		HashMap<String,String> result = new HashMap<>();
		System.out.println(map);
		userService.createAccount(map);
		return result;
	}
}
