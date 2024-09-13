package com.ddong_kka.hagojabi.Config.Oauth.Provider;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {

    private Map<String, Object> attributes; // getAttributes()를 받아온다

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes; // 전체 attributes를 직접 저장
    }

    @Override
    public String getProviderId() {
        // 'id' 필드가 카카오 사용자의 고유 식별자
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }

    @Override
    public String getProvider() {
        return "Kakao";
    }

    @Override
    public String getEmail() {
        // 카카오 앱 비즈니스 신청을 해야 이메일 반환이 가능
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        //provider + "_" +  providerId + "_" +  oauth2UserInfo.getUserName();
        return "kakao_"+ getProviderId() + "_" + getUserName() ;
    }

    @Override
    public String getUserName() {
        // 카카오에서 사용자 이름은 'nickname' 필드에 위치
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return profile != null ? (String) profile.get("nickname") : null;
    }
}