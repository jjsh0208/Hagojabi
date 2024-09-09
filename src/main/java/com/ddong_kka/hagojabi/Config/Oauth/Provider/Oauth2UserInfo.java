package com.ddong_kka.hagojabi.Config.Oauth.Provider;

public interface Oauth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getUserName();
}
