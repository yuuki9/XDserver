package com.egis.xdserver.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.egis.xdserver.object.UserInfo;
import com.egis.xdserver.util.Com;

@Service
public class LoginIdPwValidator implements UserDetailsService {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
		
	//@Bean
    // public PasswordEncoder passwordEncoder() {
    //    return new SHA512PasswordEncoder();
    //}
	
    @Override
    public UserDetails loadUserByUsername(String insertedId) throws UsernameNotFoundException {
        
    	Com.authenticationPath = this.getClass().getClassLoader().getResource("user-authentication.xml").getPath();
    	UserInfo auth = new UserInfo(Com.authenticationPath, insertedId);
    	if (auth.getId() == null) {
           return null;
        }
    	
       String pw = auth.getPw();
       String roles = auth.getRoles();
 
       return User.builder()
                .username(insertedId)
                .password(pw)
                .roles(roles)
                .build();
    }
}