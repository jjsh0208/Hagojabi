package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Service.ProjectStudyPostServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projectStudyPost")
public class ProjectStudyPostRestController {

    private final ProjectStudyPostServiceImpl projectStudyPostServiceImpl;

    public ProjectStudyPostRestController(ProjectStudyPostServiceImpl projectStudyPostServiceImpl) {
        this.projectStudyPostServiceImpl = projectStudyPostServiceImpl;
    }

    @GetMapping()
    public ResponseEntity<?>  getPosts(Pageable pageable){
        try{
            Pageable sortedByIdPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id") // Change to ASC for ascending order
            );
            //문제점 : 전체에 대한 내림차순을 설정하기떄문에 테이블 전체를 스캔함
            Page<ProjectStudyPost> posts = projectStudyPostServiceImpl.getPosts(sortedByIdPageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", posts.getContent().stream()
                    .map(post -> {
                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("id", post.getId());
                        postMap.put("title", post.getTitle());
                        postMap.put("description", post.getDescription());
                        postMap.put("create_at", post.getCreate_at());
                        postMap.put("update_at", post.getUpdate_at());
                        postMap.put("position", post.getPosition());
                        postMap.put("peopleCount", post.getPeopleCount());
                        postMap.put("duration", post.getDuration());
                        postMap.put("projectMode", post.getProjectMode());
                        postMap.put("recruitmentDeadline", post.getRecruitmentDeadline());
                        postMap.put("techStack", post.getTechStack());
                        postMap.put("recruitmentType", post.getRecruitmentType());
                        postMap.put("contactEmail", post.getContactEmail());
                        postMap.put("viewCount", post.getViewCount());

                        // Adding author information in the desired format
                        postMap.put("author", Map.of("name", post.getUser().getUsername())); // Assuming 'getUsername' gives the author's name

                        return postMap;
                    })
                    .collect(Collectors.toList()));

            response.put("totalElements", posts.getTotalElements()); // Total number of elements
            response.put("number", posts.getNumber());  // Current page
            response.put("totalPages", posts.getTotalPages()); // Total number of pages
            response.put("currentPage", posts.getNumber()); // Current page number
            response.put("pageSize", posts.getSize()); // Size of each page

            return ResponseEntity.ok(response);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching posts");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //게시글 상세보기 , 게시글 수정에서 사용
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id ){
        try{
            ProjectStudyPostDetailDTO projectStudyPost = projectStudyPostServiceImpl.getDetail(id);

            if (projectStudyPost != null){
                return ResponseEntity.ok(projectStudyPost);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post not found with id : " + id);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching post detail: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerProject(@RequestBody ProjectStudyPostDTO projectStudyPostDTO){
        try{
            Long projectStudyPostId =  projectStudyPostServiceImpl.register(projectStudyPostDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "게시글 작성 완료");
            response.put("id", projectStudyPostId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating post : " + e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id , @RequestBody ProjectStudyPostDTO projectStudyPostDTO){
        try{
            Long projectPostId = projectStudyPostServiceImpl.update(projectStudyPostDTO, id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "게시글 작성 완료");
            response.put("id", projectPostId);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating post: " + e.getMessage());
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        try {
            projectStudyPostServiceImpl.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting post", "details", e.getMessage()));
        }

    }

}