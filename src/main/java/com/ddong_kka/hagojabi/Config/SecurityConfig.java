package com.ddong_kka.hagojabi.Config;


import com.ddong_kka.hagojabi.Config.Oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationHttpRequest ->
                        authorizationHttpRequest.requestMatchers("/user/**").authenticated()
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .anyRequest().permitAll()
                )
                .formLogin(login ->
                        login.loginPage("/loginForm")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                                .usernameParameter("email")
                )
                .oauth2Login(login ->
                        login.loginPage("/loginForm")
                                .userInfoEndpoint(userinfo ->
                                        userinfo.userService(principalOauth2UserService)
                                )
                );

        return http.build();
    }
}

