package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ProjectStudyPost")
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id ){

        ProjectStudyPostDetailDTO projectStudyPost = projectStudyPostService.getDetail(id);

        if (projectStudyPost != null){

            System.out.println("호출됨 : " +  projectStudyPost.toString());

            return ResponseEntity.ok(projectStudyPost);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Post not found with id: " + id);
        }
    }


    @GetMapping()
    public Map<String, Object> getPosts(Pageable pageable){
        return projectStudyPostService.getPosts(pageable);
    }

}