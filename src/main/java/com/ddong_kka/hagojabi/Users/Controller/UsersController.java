package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Users.Service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UsersController {

    @GetMapping("/loginForm")
    public String loginFrom(){
        System.out.println("동작");
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "user/joinForm";
    }

    @GetMapping("/myPage")
    public String myPage() { return "user/myPage";}

}
