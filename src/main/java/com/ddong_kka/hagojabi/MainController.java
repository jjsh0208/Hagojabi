package com.ddong_kka.hagojabi;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("")
    public String index(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // User is authenticated
            Object principal = authentication.getPrincipal(); // Get the authenticated user details
            System.out.println("로그인이 정상적으로 되었는지 확인  user: " + principal);
        } else {
            // User is not authenticated
            System.out.println("User is not authenticated");
        }

        return "index";
    }

}
