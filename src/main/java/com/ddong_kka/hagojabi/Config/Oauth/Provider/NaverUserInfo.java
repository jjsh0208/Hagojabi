package com.ddong_kka.hagojabi.Config.Oauth.Provider;

import java.util.Map;

public class NaverUserInfo implements Oauth2UserInfo{

    private Map<String, Object> attributes; //getAttributes(); 를 받아온다

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "Naver";
    }

    @Override
    public String getEmail() {
        return  attributes.get("email").toString();
    }

    @Override
    public String getUserName() {
        return  attributes.get("name").toString();
    }
}
