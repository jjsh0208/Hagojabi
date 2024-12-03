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
@RequestMapping("/api/projectStudyPost")
public class ProjectStudyPostRestController {

    private final ProjectStudyPostService projectStudyPostService;


    public ProjectStudyPostRestController(ProjectStudyPostService projectStudyPostService) {
        this.projectStudyPostService = projectStudyPostService;
    }

    @GetMapping()
    public Map<String, Object> getPosts(Pageable pageable){
        return projectStudyPostService.getPosts(pageable);
    }

    //게시글 상세보기 , 게시글 수정에서 사용
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id ){

        ProjectStudyPostDetailDTO projectStudyPost = projectStudyPostService.getDetail(id);

        if (projectStudyPost != null){
            return ResponseEntity.ok(projectStudyPost);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Post not found with id: " + id);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerProject(@RequestBody ProjectStudyPostDTO projectStudyPostDTO){

        Long projectStudyPostId =  projectStudyPostService.register(projectStudyPostDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "게시글 작성 완료");
        response.put("id", projectStudyPostId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id , @RequestBody ProjectStudyPostDTO projectStudyPostDTO){

        Long projectPostId = projectStudyPostService.update(projectStudyPostDTO, id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "게시글 작성 완료");
        response.put("id", projectPostId);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        try{
            projectStudyPostService.deletePost(id);
            return ResponseEntity.noContent().build();
        }catch(IllegalAccessException e){
            return ResponseEntity.badRequest().body(e.getMessage()); //메시지 반환
        }

    }

}