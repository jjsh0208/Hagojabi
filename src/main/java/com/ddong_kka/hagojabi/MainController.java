package com.ddong_kka.hagojabi;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/")
    public String index(){
        System.out.println("동작 테스트");

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        if (name != null) {
            System.out.println("현재 로그인된 사용자 : " + name);
        }

        return "index";
    }


    @GetMapping("/home")
    public String home(){
        return "pages/home";
    }

}
