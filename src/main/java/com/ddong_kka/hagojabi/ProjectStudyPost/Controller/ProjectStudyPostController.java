package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/ProjectStudyPost")
public class ProjectStudyPostController {

    @GetMapping({"/new"})
    public String ProjectStudyPostForm(){
        return "ProjectStudyPost/ProjectStudyPostForm";
    }

    @GetMapping
    public String ProjectStudyPost(Model model){
        return "ProjectStudyPost/ProjectStudyPost";
    }

//    @GetMapping("/")
//    public Page<ProjectStudyPost> getPosts(
//            @PageableDefault(size=10 , sort ="id")Pageable pageable){
//        return projectStudyPostService.getPosts(pageable);
//    }


    //1. restController 와 일반 Controller 의 역활을 확실하게 나눠야함 ( 현재 애매모호 )
    //2. 변경하게되면 뷰를 반환받고 반환받은 뷰에서 js를 실행해 rest에 요청해 데이터를 받고 뷰에 갱신하는 로직으로 변경해야함
    //3. 규모가 더 커지기전에 수정하고 넘어갈것


    @GetMapping("/")
    public String ProjectStudyPost(){
        return "ProjectStudyPost/ProjectStudyPost";
    }

    @GetMapping("/{id}")

    public String detailView(@PathVariable Long id , Model model){
        model.addAttribute("postId",id);
        return "ProjectStudyPost/ProjectStudyPostDetail";
    }


}
