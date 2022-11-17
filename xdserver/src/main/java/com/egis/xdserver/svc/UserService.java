package com.egis.xdserver.svc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egis.xdserver.object.UserInfo;

public interface UserService {
	
	List<UserInfo> getUserList() throws Exception, IOException;

	void createAccount(Map map);
}
