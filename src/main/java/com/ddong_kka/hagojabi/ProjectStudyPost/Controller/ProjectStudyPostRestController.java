package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
