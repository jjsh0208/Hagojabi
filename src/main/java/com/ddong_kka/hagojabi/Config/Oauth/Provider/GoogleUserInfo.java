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
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "Google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUserName() {
        return (String) attributes.get("name");
    }
}
