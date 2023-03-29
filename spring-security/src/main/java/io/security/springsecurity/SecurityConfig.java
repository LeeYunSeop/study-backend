package io.security.springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated();
        http
                .formLogin()
//                .loginPage("/loginPage")
                .defaultSuccessUrl("/")
                .failureUrl("/login")
                .usernameParameter("userId")
                .passwordParameter("passwd")
                .loginProcessingUrl("/login_proc")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication" + authentication.getName());
                        response.sendRedirect("/");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("exception" + exception.getMessage());
                        response.sendRedirect("login");
                    }
                })
                .permitAll();
        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
                .deleteCookies("remember-me");
        http
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailsService);
        http
                .sessionManagement()
                // 세션 생성 방법
                // 1. changeSessionId 로그인시 매번 새로운 세션 ID를 발급하며 이전 세션설정값들은 그대로 유지, 서블릿 3.1이상 기본값
                // 2. 1번과 같음 다만 서블릿 3.1미만에서 기본값
                // 3. newSession 로그인시 매번 새로운 세션 ID를 발급하며 이전 세션설정값들도 유지하지 않는다.
                // 4. none 이전 세션아이디를 그대로 유지한다.(세센고정공격에 취약함)
                // 세션고정공격 -> 공격자가 가지고 있는 쿠키값을 사용자에게 그대로 복사해서 심어놓음
                //             그 후 사용자가 로그인을 했을 경우 공격자와 사용자의 쿠키값이 같기때문에 공격자도 로그인 된 상태가 됨.
                //             사용자의 정보를 탈취할 수 있다.
                //             그래서 changeSessionId(기본값)으로 설정했을 경우에는 로그인 했을 때 쿠키에 있는 세션아이디가 변경이 되기때문에 공격방어가됨
                .sessionFixation().changeSessionId()
                // 세션 정책 설정
                // 1. SessionCreationPolicy.IF_REQUIRED -> 스프링 시큐리티가 필요 시 생성(기본값)
                // 2. SessionCreationPolicy.ALWAYS -> 스프링 시큐리티가 항상 생성
                // 3. SessionCreationPolicy.NEVER -> 스프링 시큐리티가 생성은 안하지만 이미 존재하면 사용
                // 이미 존재라는 것은 스프링 시큐리티에서 생성한 세션이 아니라 톰캣이나 다른 모듈에서 이미 생성한 세션이 존재한다면 그것을 사용하겠다는 의미
                // 세션은 일반적으로 서버 즉 WAS 에서 생성한 Request 객체로부터 얻을 수 있기 때문에 스프링 시큐리티에서만 생성할 수 있는 것은 아니다.
                // 4. SessionCreationPolicy.STATELESS -> 스프링 시큐리티가 생성도 안하고 세션이 존재해도 사용안함(세션 사용안할 시 설정) Ex) JWT
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // 동시 접속자 수 설정(-1 무한)
                .maximumSessions(1)
                // 동시 접속자 수가 초과되었을 때 설정(true = 세션생성차단, false = 세션 생성 후 이전세션 만료)
                .maxSessionsPreventsLogin(true)
        ;
    }
}
