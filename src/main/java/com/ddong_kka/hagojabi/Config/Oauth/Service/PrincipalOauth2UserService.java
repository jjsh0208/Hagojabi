package com.ddong_kka.hagojabi.Config.Oauth.Service;

import com.ddong_kka.hagojabi.Config.Oauth.Provider.GoogleUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.KakaoUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.NaverUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.Oauth2UserInfo;
import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.User.Model.Users;
import com.ddong_kka.hagojabi.User.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    //oauth2 로그인 후 처리 함수
    //구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    //  함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Oauth2UserInfo oauth2UserInfo = null;

        if (registrationId.equals("google")){
            System.out.println("구글 로그인 요청");
            oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(registrationId.equals("naver")){
            System.out.println("네이버 로그인 요청");
            oauth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }else if (registrationId.equals("kakao")){
            System.out.println("카카오 로그인 요청");
            oauth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }
        else{
            System.out.println("지원 불가 서비스");
        }

        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String email = oauth2UserInfo.getEmail();
        String username = provider + "_" +  providerId + "_" +  oauth2UserInfo.getUserName();
        String password = bCryptPasswordEncoder.encode("password");
        String role = "USER_ROLE";

        Users usersEntity =  usersRepository.findByEmail(email);


        if (usersEntity == null){
            usersEntity = Users.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            usersRepository.save(usersEntity);

        }

        return new PrincipalDetails(usersEntity, oAuth2User.getAttributes());
    }
}
