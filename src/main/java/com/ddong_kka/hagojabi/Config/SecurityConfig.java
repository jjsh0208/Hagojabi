package com.ddong_kka.hagojabi.Config;

import com.ddong_kka.hagojabi.Config.JWT.JWTLogoutFilter;
import com.ddong_kka.hagojabi.Config.JWT.JWTCreationHandler;
import com.ddong_kka.hagojabi.Config.JWT.JWTUtil;
import com.ddong_kka.hagojabi.Config.JWT.JWTFilter;
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
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTCreationHandler jwtCreationHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(JWTCreationHandler jwtCreationHandler, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtCreationHandler = jwtCreationHandler;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, PrincipalOauth2UserService principalOauth2UserService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomJsonUsernamePasswordAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtCreationHandler), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())) // cors 설정 메서드 적용
                .authorizeHttpRequests(authorizationHttpRequest ->
                        authorizationHttpRequest
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/projects/**").authenticated()
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .requestMatchers("/reissue").permitAll()
                                .anyRequest().permitAll()
                )
                .oauth2Login(login ->
                        login.loginPage("/loginForm")
                                .userInfoEndpoint(userinfo -> userinfo.userService(principalOauth2UserService))
                                .successHandler(jwtCreationHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // CORS 설정을 한 곳에서 처리하는 메서드
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "access"));

        return request -> configuration;
    }

}

