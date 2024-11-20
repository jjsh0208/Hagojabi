package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ProjectStudyPost")
public class ProjectStudyPostRestController {

    private final ProjectStudyPostService projectStudyPostService;

    public ProjectStudyPostRestController(ProjectStudyPostService projectStudyPostService) {
        this.projectStudyPostService = projectStudyPostService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerProject(@RequestBody ProjectStudyPostDTO projectStudyPostDTO){

        Long projectStudyPostId =  projectStudyPostService.register(projectStudyPostDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "게시글 작성 완료");
        response.put("id", projectStudyPostId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}