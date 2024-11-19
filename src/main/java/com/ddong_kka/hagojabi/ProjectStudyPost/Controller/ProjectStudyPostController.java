package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/ProjectStudyPost")
public class ProjectStudyPostController {
    private final ProjectStudyPostService projectStudyPostService;

    public ProjectStudyPostController(ProjectStudyPostService projectStudyPostService) {
        this.projectStudyPostService = projectStudyPostService;
    }

    @GetMapping({"/new"})
    public String ProjectStudyPostForm(){
        return "ProjectStudyPost/ProjectStudyPostForm";
    }

    @GetMapping
    public String ProjectStudyPost(Model model){
        return "ProjectStudyPost/ProjectStudyPost";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id ,Model model){

        ProjectStudyPost projectStudyPost = projectStudyPostService.detail(id);

        if(projectStudyPost != null){
            model.addAttribute(projectStudyPost);
            return "ProjectStudyPost/ProjectStudyPostDetail";
        }else {
            model.addAttribute("error", "Post not found with id: " + id);
            return "error";  // 오류 페이지 이름 (예: error.html)
        }
    }
}
