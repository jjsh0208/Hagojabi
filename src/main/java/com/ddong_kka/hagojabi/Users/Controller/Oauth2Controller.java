package com.ddong_kka.hagojabi.Users.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Oauth2Controller {

    @GetMapping("/oauth2-success")
    public String oauth2Success(@RequestParam("accessToken") String accessToken, Model model){

        model.addAttribute("accessToken", accessToken);

        return "oauth2/oauth2-success";
    }
}
