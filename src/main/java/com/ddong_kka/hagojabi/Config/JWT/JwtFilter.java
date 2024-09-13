package com.ddong_kka.hagojabi.Config.JWT;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.User.Model.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JwtFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //cookie
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("Authorization")) {

                    authorization = cookie.getValue(); //jwt 토큰
                }
            }
        }
        //authorization 헤더 검증
        if (authorization == null){
            System.out.println("token null");

            filterChain.doFilter(request,response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰을 가져온다
        String token = authorization;

        //토큰의 소멸 시간 이 지났는지 확인
        if (jwtUtil.isExpired(token)){
            System.out.println("token expired");

            filterChain.doFilter(request,response);

        }
        

        //토큰에서 email 과 role 획득
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        Users users = new Users();
        users.setEmail(email);
        users.setRole(role);

        //회원 정보 가져오기
        PrincipalDetails principalDetails = new PrincipalDetails(users);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);
    }
}
