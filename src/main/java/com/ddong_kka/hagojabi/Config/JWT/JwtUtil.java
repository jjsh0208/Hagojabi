package com.ddong_kka.hagojabi.Config.JWT;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    
    //생성자 : application.yml에서 JWT 비밀 키를 주입받아 secretKey 객체 초기화
    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 토큰에서 사용자 이메일을 추출하는 메소드
    public String getEmail(String token) {

        return Jwts.parser()
                .verifyWith(secretKey) //비밀 키로 서명을 검증한다.
                .build()
                .parseSignedClaims(token) // 서명된 클레이믈 파싱해 JWT를 해독한다.
                .getPayload()
                .get("email", String.class); // 이메일 클레임 반환 (인증을 할려는 사용자의 이메일)
    }

    // JWT 토큰에서 사용자 역할을 추출하는 메소드
    public String getRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey) //비밀 키로 서명을 검증한다.
                .build()
                .parseSignedClaims(token) // 서명된 클레임을 파싱해 JWT를 해독
                .getPayload()
                .get("role", String.class); // ROLE 클레임을 반환 (인증을 할려는 사용자의 역할)
    }

    
    // JWT 토큰의 만료 여부를 확인하는 메소드
    public Boolean isExpired(String token) { 

        return Jwts.parser()
                .verifyWith(secretKey) // 비밀 키로 서명을 검증한다.
                .build()
                .parseSignedClaims(token) // 서명된 클레이믈 파싱해 JWT를 해독
                .getPayload()
                .getExpiration()
                .before(new Date()); // 만료일이 현재 날짜 이전인지 확인
    }

    //토큰 판단용
    public String getCategory(String token){

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category",String.class);
    }



    // JWT 토큰을 생성하는 메소드
    public String createJwt(String category, String email, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category",category)
                .claim("email", email) //이메일 클레임 추가
                .claim("role", role) // ROLE 클레임 추가
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발급 시간 설정
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료료시간 설정
                .signWith(secretKey) // 비밀 키로 서명
                .compact(); //토큰 생성
    }
}