package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Users.Service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/loginForm")
    public String loginFrom(){
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "user/joinForm";
    }
    @GetMapping({"/projects/new"})
    public String user(){
        return "projects/projectForm";
    }

}
