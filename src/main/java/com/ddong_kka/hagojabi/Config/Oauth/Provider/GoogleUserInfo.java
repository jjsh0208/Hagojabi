package com.ddong_kka.hagojabi.Config.Oauth.Provider;

import java.util.Map;
import java.util.Objects;

public class GoogleUserInfo implements Oauth2UserInfo{

    private Map<String, Object> attributes; //getAttributes(); 를 받아온다

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "Google";
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getUserName() {
        return attributes.get("name").toString();
    }
}
