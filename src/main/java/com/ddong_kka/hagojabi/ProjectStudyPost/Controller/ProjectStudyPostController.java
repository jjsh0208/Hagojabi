package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/projectStudyPost")
public class ProjectStudyPostController {

    @GetMapping({"/new"})
    public String ProjectStudyPostForm(){
        return "ProjectStudyPost/ProjectStudyPostForm";
    }

    @GetMapping
    public String ProjectStudyPost(Model model){
        return "ProjectStudyPost/ProjectStudyPost";
    }


    @GetMapping("/")
    public String ProjectStudyPost(){
        return "ProjectStudyPost/ProjectStudyPost";
    }

    @GetMapping("/{id}")
    public String detailView(@PathVariable Long id , Model model){
        model.addAttribute("postId",id);
        return "ProjectStudyPost/ProjectStudyPostDetail";
    }

    @GetMapping("/edit/{id}")
    public String editView(@PathVariable Long id){
        return  "ProjectStudyPost/ProjectStudyPostForm";
    }
}
