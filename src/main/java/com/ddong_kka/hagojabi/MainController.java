package com.ddong_kka.hagojabi;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/")
    public String index(){
        return "index";
    }


    @GetMapping("/home")
    public String home(){
        return "pages/home";
    }

}
