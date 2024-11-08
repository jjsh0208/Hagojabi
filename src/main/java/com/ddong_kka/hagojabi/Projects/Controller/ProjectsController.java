package com.ddong_kka.hagojabi.Projects.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ProjectsController {

    @GetMapping({"/projects/new"})
    public String projectForm(){
        return "projects/projectForm";
    }

    @GetMapping("/projects")
    public String projects(){
        return "projects/projects";
    }
}
