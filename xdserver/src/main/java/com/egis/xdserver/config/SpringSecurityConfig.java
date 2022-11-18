package com.egis.xdserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	/* 로그인 실패 핸들러 의존성 주입 */
	@Bean
    public LoginFailHandler loginFailHandler(){
        return new LoginFailHandler();
    }
	@Autowired
	LoginIdPwValidator loginIdPwValidator;
	
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http	
				.csrf().disable()
                	.authorizeRequests()             
                	//.antMatchers("/chk").permitAll()    // LoadBalancer Chk
                	//ROLE_ADMIN의 role을 가지고 있어야만 /XDServer 이하의 uri에 접근 가능 하게 됩니다
                	.antMatchers("/login**", "/web-resources/**", "/actuator/**").permitAll()
                	.antMatchers("/admin/**").hasAnyRole("USER","ADMIN") 
                	.antMatchers("/superadmin/**").hasRole("ADMIN")                 	
                    .anyRequest().authenticated()                 
                .and()
	                .formLogin()
	                .loginPage("/login") //커스텀 페이지로 로그인 페이지를 변경
	                .failureHandler(loginFailHandler())// 로그인 실패 핸들러
	                .loginProcessingUrl("/loginProc")            
	                .defaultSuccessUrl("/admin/web", true)
	                // //action url
	                .usernameParameter("username")// 유저아이디 from name default는 username 
	                .passwordParameter("password")//            
	                .permitAll()               
                .and()
                    .logout()
					.logoutUrl("/logout") // 로그아웃 처리 URL, default: /logout, 원칙적으로 post 방식만 지원
					.logoutSuccessUrl("/login") // 로그아웃 성공 후 이동페이지
					.deleteCookies("JSESSIONID")
					.invalidateHttpSession(true)
					.clearAuthentication(true);
        			//.logoutRequestMatcher(new AntPathRequestMatcher("/logoutProc"));
    }	
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/js/**","/static/css/**","/static/img/**","/static/frontend/**");
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(loginIdPwValidator);
    }
    
}