package com.ddong_kka.hagojabi.Config.Oauth.Service;

import com.ddong_kka.hagojabi.Config.Oauth.Provider.GoogleUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.KakaoUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.NaverUserInfo;
import com.ddong_kka.hagojabi.Config.Oauth.Provider.Oauth2UserInfo;
import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화 인코더

    @Autowired
    private UsersRepository usersRepository; // 사용자 정보를 저장하는 레포지토리

    // oauth2 로그인 후 사용자 정보를 처리하는 함수
    // Google , Naver , Kakao 에서 받은 userRequest 데이터에 대한 후처리 함수
    // 이 함수 종료 시 @AuthenticationPrincipal  어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // Oauth2User 객체를 기본 구현에서 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo oauth2UserInfo = null;

        // 제공자에 따라 적절한 UserInfo 객체를 생성
        if (registrationId.equals("google")){
            
            oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(registrationId.equals("naver")){
      
            oauth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }else if (registrationId.equals("kakao")){
      
            oauth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }
        else{
            // 지원하지않는 서비스일 경우 디버깅용 로그
            // 추후 예외처리 추가해야함
            System.out.println("지원 불가 서비스");
        }

        //사용자 정보를 가져옴
        String provider = oauth2UserInfo.getProvider(); // 제공자 정보
        String providerId = oauth2UserInfo.getProviderId(); // 제공자 ID
        String email = oauth2UserInfo.getEmail(); // 이메일
        String username = provider + "_" +  providerId + "_" +  oauth2UserInfo.getUserName(); // 사용자 이름
        String password = bCryptPasswordEncoder.encode("password"); // 기본 비밀번호 암호화
        String role = "USER_ROLE"; //사용자 역할 설정


        // 데이터베이스에서 사용자를 조회
        Users usersEntity =  usersRepository.findByEmail(email);

        // 사용자가 존재하지않을 시 새로 생성
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
        // PrincipalDetails 객체 반환 (사용자 정보와 OAuth2User 속성 포함)
        return new PrincipalDetails(usersEntity, oAuth2User.getAttributes());
    }
}
