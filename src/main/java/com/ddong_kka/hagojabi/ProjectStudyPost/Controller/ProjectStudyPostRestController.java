package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ProjectStudyPost")
public class ProjectStudyPostRestController {

    private final ProjectStudyPostService projectStudyPostService;

    public ProjectStudyPostRestController(ProjectStudyPostService projectStudyPostService) {
        this.projectStudyPostService = projectStudyPostService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerProject(@RequestBody ProjectStudyPostDTO projectStudyPostDTO){
        System.out.println(projectStudyPostDTO.toString());
        projectStudyPostService.register(projectStudyPostDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 작성 완료.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){

        ProjectStudyPost projectStudyPost = projectStudyPostService.detail(id);

        if(projectStudyPost != null){
            return ResponseEntity.ok(projectStudyPost);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Post not found with id: " + id);
        }




    }
}
