package com.ddong_kka.hagojabi.ProjectStudyPost.Controller;

import com.ddong_kka.hagojabi.Exception.DataNotFoundException;
import com.ddong_kka.hagojabi.Exception.UnauthorizedAccessException;
import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
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

    // 게시글 목록 조회
    @GetMapping()
    public ResponseEntity<?>  getPosts(Pageable pageable){
        try{
            Pageable sortedByIdPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id") //내림차순 정렬
            );
    
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

                        // 작성자 정보 추가
                        postMap.put("author", Map.of("name", post.getUser().getUsername())); // Assuming 'getUsername' gives the author's name

                        return postMap;
                    })
                    .collect(Collectors.toList()));

            response.put("totalElements", posts.getTotalElements()); // 총 게시글 수
            response.put("number", posts.getNumber());  // 현재 페이지
            response.put("totalPages", posts.getTotalPages()); // 총 페이지 수
            response.put("currentPage", posts.getNumber()); // 현재 페이지 번호
            response.put("pageSize", posts.getSize()); // 페이지당 글 수

            return ResponseEntity.ok(response); // 상태 코드 200 OK
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","게시글 목록을 가져오는데 실패했습니다.","error",e.getMessage()));
                    // 상태 코드 500 INTERNAL_SERVER_ERROR
        }
    }

    //게시글 상세보기 , 게시글 수정에서 사용
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id ){
        try{
            ProjectStudyPostDetailDTO projectStudyPost = projectStudyPostServiceImpl.getDetail(id);

            if (projectStudyPost != null){
                return ResponseEntity.ok(projectStudyPost); // 상태 코드 200 OK
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 ID로 게시물을 찾을 수 없습니다" ,"id",id)); // 상태 코드 404 NOT_FOUND
            }
        } catch(DataNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message",e.getMessage() ,"id",id)); // 상태 코드 404 NOT_FOUND
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message","게시글 상세정보를 가져오는 중 오류 발생 : " + e.getMessage())); // 상태 코드 500 INTERNAL_SERVER_ERROR
        }
    }

    //게시글 등록
    @PostMapping("/create")
    public ResponseEntity<?> registerProject(@RequestBody ProjectStudyPostDTO projectStudyPostDTO){
        try{
            Long projectStudyPostId =  projectStudyPostServiceImpl.register(projectStudyPostDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message","게시글 작성이 성공적으로 완료되었습니다.", "id" , projectStudyPostId )); // 상태 코드 201 CREATED
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage() ,"error", "NOT_FOUND")); // 상태 코드 404 NOT_FOUND
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message",e.getMessage(), "error", "BAD_REQUEST")); // 상태 코드 400 BAD_REQUEST
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message","게시글 생성 중 오류 발생 : " + e.getMessage())); // 상태 코드 500 INTERNAL_SERVER_ERROR
        }
    }

    //게시글 수정
    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id , @RequestBody ProjectStudyPostDTO projectStudyPostDTO){
        try{
            Long projectPostId = projectStudyPostServiceImpl.update(projectStudyPostDTO, id);
            return ResponseEntity.ok()
                    .body(Map.of("message","게시글 수정 완료","id",projectPostId));// 상태 코드 200 OK
        } catch (DataNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage(), "error" , "NOT_FOUND")); // 상태 코드 404 NOT_FOUND
        } catch (UnauthorizedAccessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage() , "error","FORBIDDEN"));  // 상태 코드 403 FORBIDDEN
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message",e.getMessage() , "error", "BAD_REQUEST")); // 상태 코드 400 BAD_REQUEST
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "게시글 수정에 실패했습니다.", "error", e.getMessage())); // 상태 코드 500 INTERNAL_SERVER_ERROR
        }

    }

    //게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            projectStudyPostServiceImpl.deletePost(id);
            return ResponseEntity.noContent().build(); // 상태 코드 204 NO_CONTENT
        } catch (DataNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage() ,"error","NOT_FOUND")); // 상태 코드 404 NOT_FOUND
        } catch (UnauthorizedAccessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage() , "error", "FORBIDDEN")); // 상태 코드 403 FORBIDDEN
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "게시글 삭제에 실패했습니다.", "error", e.getMessage())); // 상태 코드 500 INTERNAL_SERVER_ERROR
        }

    }

}