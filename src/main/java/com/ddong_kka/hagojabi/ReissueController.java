package com.ddong_kka.hagojabi;


import com.ddong_kka.hagojabi.Config.JWT.JWTUtil;
import com.ddong_kka.hagojabi.User.Model.RefreshEntity;
import com.ddong_kka.hagojabi.User.Repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ReissueController {

    private final JWTUtil jwtUtil;

    private RefreshRepository refreshRepository;

    // 생성자 : JWTUtile을 주입받아 초기화
    public ReissueController(JWTUtil jwtUtil , RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }


    // access 토큰 재발급 메서드
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 쿠키를 가져와 refresh 토큰을 확인
        String refresh = null;
        Cookie[] cookies = request.getCookies(); // 쿠키 배열을 가져옴
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) { // refresh 토큰 찾기

                refresh = cookie.getValue();
                break; // refresh 토큰을 찾으면 반복 종료
            }
        }

        // refresh 토큰이 없을 경우 BAD_REQUEST 상태 반환
        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // refresh 토큰 만료 여부 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) { // refresh 토큰도 만료된 경우
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰의 카테고리가 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // refresh 토큰이 DB에 저장되어있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist){
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }



        // 토큰에서 사용자 정보 추출 (email 및 role)
        String userEmail = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새로운 access 및 refresh 토큰 생성 (유효 기간 : access 10분 , refresh 24시간)
        String newAccess = jwtUtil.createJwt("access", userEmail, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userEmail,role,86400000L);

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰을 저장한다.
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(userEmail, newRefresh, 86400000L);


        // 엑세스 토큰을 헤더에 , 리프레쉬 토큰을 쿠키에 재설정
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 쿠키 생성 메소드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value); // 쿠키 객체 생성
        cookie.setMaxAge(24*60*60); // 쿠키의 유효 기간 설정 (24시간)
        //cookie.setSecure(true);   // HTTPS 환경에서만 쿠키 사용 (필요 시 활성화)
        //cookie.setPath("/");      // 모든 경로에서 쿠키 사용 가능 
        cookie.setHttpOnly(true);   // 자바스크립트에서 쿠키 접근 불가

        return cookie; // 생성한 쿠키 반환
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

}
