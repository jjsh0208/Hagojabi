package com.ddong_kka.hagojabi.Config.Oauth;

import com.ddong_kka.hagojabi.Config.Oauth.Provider.GoogleUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.NaverUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.Oauth2UserInfo;
import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.User.Model.Users;
import com.ddong_kka.hagojabi.User.Prpository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

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

        //구글 로그인 버튼 클릭 -> 구글 로그인창 - > 로그인을완료 - > code 리턴 (Oauth2-client라이브러리) -> AccessToekn
        //userRequest 정보 - > 회원 프로필을 받아온다 그때 사용되는 함수가 loadUser 함수 -> 구글로 부터 받아온다. 회원 프로필
        System.out.println("getAttributes : " + oAuth2User.getAttributes());


        Oauth2UserInfo oauth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oauth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
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
            System.out.println("Oauth 로그인이 최초");
            usersEntity = Users.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            usersRepository.save(usersEntity);

        }else{
            System.out.println("로그인을 이미 한 적이 있습니다.");
        }

        return new PrincipalDetails(usersEntity, oAuth2User.getAttributes());
    }
}
