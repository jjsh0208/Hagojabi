package com.ddong_kka.hagojabi.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    public BCryptPasswordEncoder encoderPwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                //CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                //권한 설정
                .authorizeHttpRequests(authorization ->
                        authorization
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .requestMatchers("/comment/**").authenticated()
                                .requestMatchers("/evaluation/**").authenticated()
                                .requestMatchers("/project/create/**", "/project/edit/**", "/project/delete/**").authenticated()
                                .requestMatchers("/project/**").permitAll()
                                // /portfolio/**는 누구나 접근 가능, /portfolio/create, edit, delete는 인증 필요
                                .requestMatchers("/portfolio/create/**", "/portfolio/edit/**", "/portfolio/delete/**").authenticated()
                                .requestMatchers("/portfolio/**").permitAll()
                                // /forum/**는 누구나 접근 가능, /forum/create, edit, delete는 인증 필요
                                .requestMatchers("/forum/create/**", "/forum/edit/**", "/forum/delete/**").authenticated()
                                .requestMatchers("/forum/**").permitAll()
                                .anyRequest().permitAll()
                )

//                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
//                                .loginPage("/loginForm")
//                                .userInfoEndpoint(userInfoEndpointConfig ->  userInfoEndpointConfig
//                                        .userService(principalOauth2UserService)
//                                )
//                        //구글 로그인이 완료된 뒤의 후처리가 필요하다
//                        //tip :  로그인이 완료되면 코드 x (엑세스토큰 + 사용자프로필정보 O)  바로 받을 수 있음
//                )


                // 세션 관리 (STATELESS 로 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // form 로그인 및 Http Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

        ;

        return http.build();
    }



}
