package com.ddong_kka.hagojabi.Config.JWT;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.User.Model.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    //생성자 : jwtUtil 객체를 주입받아 초기화
    public JwtFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // JWT 토큰을 담고 있는 쿠키를 가져옴
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        // JWT 는 Authorization 란 이름의 쿠키안에 있기 때문에 Authorization 이름의 쿠키를 찾는다.
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue(); //JWT 토큰 저장
                }
            }
        }
        
        
        // JWT 토큰이 없는 경우 처리
        if (authorization == null){

            filterChain.doFilter(request,response); //다음 필터로 진행시킨다.

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // 토큰을 가져온다
        String token = authorization;

        // 토큰의 만료 여부 확인
        if (jwtUtil.isExpired(token)){

            filterChain.doFilter(request,response); // 다음 필터로 진행시킨다.
            
            return;
        }
        
        // 토큰에서 email 과 role 획득
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        // 사용자 정보를 담은 Users 객체 생성
        Users users = new Users();
        users.setEmail(email); //이메일 설정
        users.setRole(role);   //역할 설정

        // 회원 정보 가져오기
        // PrincipalDetails 객체 생성 (인증 사용자 정보)
        PrincipalDetails principalDetails = new PrincipalDetails(users);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // SecurityContext에 사용자 인증 정보 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //다음 필터로 진행
        filterChain.doFilter(request,response);
    }
}
