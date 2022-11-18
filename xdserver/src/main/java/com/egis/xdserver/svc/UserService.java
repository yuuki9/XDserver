package com.egis.xdserver.svc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egis.xdserver.object.UserInfo;

public interface UserService {
	
	List<UserInfo> getUserList() throws Exception, IOException;

	HashMap<String,String> createAccount(Map<?,?> map) throws Exception;

	HashMap<String, String> updateAccount(Map<?,?> map);

}
