package com.ddong_kka.hagojabi.Config;

import com.ddong_kka.hagojabi.Config.JWT.JwtCreationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class CustomJsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtCreationHandler jwtCreationHandler;

    // 생성자: AuthenticationManager와 JwtCreationHandler를 주입받음
    public CustomJsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtCreationHandler jwtCreationHandler) {
        super(authenticationManager); // 부모 클래스에 AuthenticationManager 주입
        this.jwtCreationHandler = jwtCreationHandler; // jwt 생성 핸들러 
        setFilterProcessesUrl("/login"); // /login 요청을 처리하는 필터로 설정
    }

    // 1. 로그인 시도 : /login 요청이 들어오면 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 1-0. 요청 방식이 POST인지 확인
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            throw new RuntimeException("Only POST requests are allowed");
        }

        try{
            // 1-1. 요청에서 JSON 데이터를 읽어 MAP 형태로 반환
            Map<String,String> loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String email = loginData.get("email"); // 이메일 추출
            String password = loginData.get("password"); // 비밀번호 추출

            System.out.println("email : " + email +"\n password : " + password);
            // 1-2. email 과 password 를 기반으로 인증 token 생성
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email,password);

            // 1-3. AuthenticationManager 에게 인증 요청
            // 요청 과정
            // UsernamePasswordAuthenticationToken 을 반환하여 AuthenticationManager에게 인증을 요청
            // UserDetailsService를 상속받은 PrincipalDetailsService 안의 loadUserByUsername(String email)이 자동으로 호출되어
            // email이 DB에 존재하는 지 유무 확인 후 시큐리티 세션에 UserDetails 타입의 PrincipalDetails 를 반환한다.
            return getAuthenticationManager().authenticate(authRequest);
            
        }catch (IOException e){
            throw new RuntimeException(e); // 예외처리
        }catch (AuthenticationException e) {
            // 1-4. 로그인 실패 시 응답 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
            try {
                response.getWriter().write("이메일 또는 비밀번호가 일치하지 않습니다. 다시 확인해주세요."); // 클라이언트에 전송할 메시지
                response.getWriter().flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
    }

    // 2. 로그인 성공 : 인증에 성공하면 호출되는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 2-1. JWT 발급 핸들러 호출
        jwtCreationHandler.onAuthenticationSuccess(request,response,authResult);

    }
}
