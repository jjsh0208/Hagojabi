package com.ddong_kka.hagojabi;


import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.User.Model.Users;
import com.ddong_kka.hagojabi.User.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails){ //Di(의존성 주입)
        // @AuthenticationPrincipal 세션 정보 접근가능
        System.out.println("/test/login ====================");
        System.out.println("authentication : " + authentication.getPrincipal());

        PrincipalDetails principalDetails =(PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : "+ principalDetails.getUsers());

        System.out.println("userDetails: " + userDetails.getUsers());

        return "세션 정보 확인하기";
    }


    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication,
                                               @AuthenticationPrincipal OAuth2User oauth){ //Di(의존성 주입)
        // @AuthenticationPrincipal 세션 정보 접근가능
        System.out.println("/test/oauth/login ====================");
        System.out.println("authentication : " + authentication.getPrincipal());

        OAuth2User oAuth2User =(OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : "+ oAuth2User.getAttributes());

        System.out.println("oauth2User: "+ oauth.getAttributes());

        return "세션 정보 확인하기";
    }

    @GetMapping({"","/"})
    public @ResponseBody String index(){
        return "index";
    }

    //oauth2 로그인을 하든 일반 로그인을 하든 PrincipalDetails 타입으로 받을 수 있다.
    @GetMapping({"/user"})
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println(principalDetails.getUsers());
        return "user";
    }
    @GetMapping({"/manager"})
    public @ResponseBody String manager(){
        return "manager";
    }
    @GetMapping({"/admin"})
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/loginForm")
    public String loginFrom(){
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "user/joinForm";
    }

    @PostMapping("/join")
    public String join(Users users){
        System.out.println(users);

        users.setRole("ROLE_USER");
        String rawPassword =users.getPassword();
        String encPassword = encoder.encode(rawPassword);


        users.setPassword(encPassword);

        usersRepository.save(users);
        return "redirect:/loginForm";
    }

    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }



}