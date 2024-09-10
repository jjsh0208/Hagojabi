package com.ddong_kka.hagojabi.Config.JWT;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("동작되는가 ? : " +principalDetails.getUsers());

        String email = principalDetails.getUsername(); //회원의 email

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(email,role , 60*60*60*60L);
        System.out.println("토큰 발급 되었는가? : " + token);
        response.addCookie(createaCookie("Authorization", token));
        response.sendRedirect("http://localhost:8080/");
    }

    private Cookie createaCookie(String key, String value) {

        Cookie cookie = new Cookie(key,value);

        cookie.setMaxAge(60*60*60);
//        cookie.setSecure(true); //https 환경에서만 동작
        cookie.setPath("/"); // 모든 경로 허용
        cookie.setHttpOnly(true); //js가 쿠키를 가져가지못하게한다.

        return cookie;
    }
}
