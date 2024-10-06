package com.ddong_kka.hagojabi.Config;

import com.ddong_kka.hagojabi.Config.JWT.JWTLogoutFilter;
import com.ddong_kka.hagojabi.Config.JWT.JwtCreationHandler;
import com.ddong_kka.hagojabi.Config.JWT.JWTUtil;
import com.ddong_kka.hagojabi.Config.JWT.JwtFilter;
import com.ddong_kka.hagojabi.Config.Oauth.Service.PrincipalOauth2UserService;
import com.ddong_kka.hagojabi.Users.Repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@EnableMethodSecurity // 메소드 보안 활성화
public class SecurityConfig {

    // 로그인 성공 시 처리할 핸들러 (customSuccessHandler)
    // JWT 관련 유틸리티  (jwtUtil)
    private final JwtCreationHandler jwtCreationHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // 생성자 : CustomSuccessHandler 와 JWTUtil 를 주입받아 초기화
    public SecurityConfig(JwtCreationHandler jwtCreationHandler, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtCreationHandler = jwtCreationHandler;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    // 비밀번호 (Password) 인코더 빈 등록
     @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder(); // 비밀번호를 암호화하는 인코더
    }

    // AuthenticationManager  빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); //스프링 시큐리티의 인증 관리자
    }


    // 보안 필터 체인 설정
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, PrincipalOauth2UserService principalOauth2UserService) throws Exception {
        http
                .csrf((auth) -> auth.disable()) // CSRF 보호 비활성화
                .httpBasic((auth) -> auth.disable()) // HTTP Basic 인증 비활성화
                .formLogin((auth)-> auth.disable()) // JSON 으로 일반 로그인를 처리 하기 때문에 formLogin 비활성화
                .addFilterBefore(new JwtFilter(jwtUtil) , UsernamePasswordAuthenticationFilter.class) //JWT 필터 추카
                .addFilterBefore(new CustomJsonUsernamePasswordAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtCreationHandler)
                        , UsernamePasswordAuthenticationFilter.class) // JSON 로그인 처리 필터 추가
                .addFilterBefore(new JWTLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class) // 로그아웃 필터 추가
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration(); // CORS 설정
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080")); // 허용할 출처
                        configuration.setAllowedMethods(Collections.singletonList("*")); //모든 HTTP 메소드 허용
                        configuration.setAllowCredentials(true); // 인증 정보를 포함할 수 있도록 허용
                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                        configuration.setMaxAge(3600L); // Preflight 요청의 캐시 유효 시간 설정
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "access")); // 노출할 헤더 설정 (Set-Cookie 및 access)

                        return configuration; // 설정 반환
                    }
                }))
                .authorizeHttpRequests(authorizationHttpRequest -> // 요청에 대한 권한 설정
                        authorizationHttpRequest
                                .requestMatchers("/user/**").authenticated() // /user/** 경로는 인증 필요
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // /manager/** 경로는 MANAGER 또는 ADMIN 역할 필요
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN") // /admin/** 경로는 ADMIN 역할 필요
                                .requestMatchers("/reissue").permitAll() // jwt access 재발급 경로 (모두 허용)
                                .anyRequest().permitAll() // 그 외 모든 요청은 허용
                )
                .oauth2Login(login -> // Oauth2 로그인 설정
                        login.loginPage("/loginForm") // Oauth2 로그인 페이지 설정
                                .userInfoEndpoint(userinfo ->
                                        userinfo.userService(principalOauth2UserService) // 사용자 정보 서비스 설정
                                )
                                .successHandler(jwtCreationHandler) // Oauth2 로그인 성공 시 핸들러 설정
                ).sessionManagement(session -> // 세션 관리
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 상태 없는 세션 관리 설정

        return http.build(); // 보인 필터 체인 빌드 및 반환
    }
}

