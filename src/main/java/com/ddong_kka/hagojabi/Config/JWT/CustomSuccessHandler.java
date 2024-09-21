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

    // 생성자: JWTUtil을 주입받아 초기화
    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자의 정보를 PrincipalDetails에서 가져옴
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String email = principalDetails.getUsername(); // 사용자의 이메일

        // 사용자의 권한 정보를 가져옴
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next(); // 첫 번째 권한을 가져옴
        String role = auth.getAuthority(); // 사용자의 역할

        // JWT 토큰 생성 (유효 기간: 1일)
        String token = jwtUtil.createJwt(email, role, 60 * 60 * 24L);

        // 토큰 발급 성공 시 메인 화면으로 리다이렉트
        response.addCookie(createaCookie("Authorization", token)); // JWT 토큰을 쿠키에 저장
        response.sendRedirect("http://localhost:8080/"); // 리다이렉트할 URL
    }

    // 쿠키 생성 메소드
    private Cookie createaCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value); // 쿠키 객체 생성
        cookie.setMaxAge(60 * 60 * 60); // 쿠키의 유효 기간 설정 (60시간)
        // cookie.setSecure(true); // https 환경에서만 쿠키 사용
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        cookie.setHttpOnly(true); // 자바스크립트에서 쿠키 접근 불가

        return cookie; // 생성한 쿠키 반환
    }
}