package com.ddong_kka.hagojabi.Users.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UsersController {

    @GetMapping("/loginForm")
    public String loginFrom(){
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "user/userForm";
    }

    @GetMapping("/userProfile")
    public String myPage() { return "user/userProfile";}

    @GetMapping("/userProfile/edit")
    public String userProfileEditForm(){ return "user/userForm"; }


    @GetMapping("/passwordChange")
    public String passwordChange(){ return "user/passwordChange"; }
}
