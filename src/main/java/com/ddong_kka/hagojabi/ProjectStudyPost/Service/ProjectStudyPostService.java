package com.ddong_kka.hagojabi.ProjectStudyPost.Service;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Exception.DataNotFoundException;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectStudyPostService {

    private final ProjectStudyPostRepository projectStudyPostRepository;
    private final UsersRepository usersRepository;

    public ProjectStudyPostService(ProjectStudyPostRepository projectStudyPostRepository, UsersRepository usersRepository) {
        this.projectStudyPostRepository = projectStudyPostRepository;
        this.usersRepository = usersRepository;
    }

    public String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof PrincipalDetails) {
            return ((PrincipalDetails) principal).getUsername(); // JWT에 포함된 이메일 또는 사용자 이름
        } else {
            return principal.toString(); // JWT의 토큰 문자열 (사용자 이름 대신 전체 토큰을 반환할 수도 있음)
        }
    }

    public Long register(ProjectStudyPostDTO projectStudyPostDTO) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        // Find the User by username in the database
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        ProjectStudyPost projects = ProjectStudyPost.builder()
                .title(projectStudyPostDTO.getTitle())
                .description(projectStudyPostDTO.getDescription())
                .position(projectStudyPostDTO.getPosition())
                .peopleCount(projectStudyPostDTO.getPeopleCount())
                .duration(projectStudyPostDTO.getDuration())
                .projectMode(projectStudyPostDTO.getProjectMode())
                .recruitmentDeadline(projectStudyPostDTO.getRecruitmentDeadline())
                .techStack(projectStudyPostDTO.getTechStack())
                .recruitmentType(projectStudyPostDTO.getRecruitmentType())
                .contactEmail(projectStudyPostDTO.getContactEmail())
                .user(user)
                .build();



       ProjectStudyPost projectStudyPost = projectStudyPostRepository.save(projects);

       return projectStudyPost.getId();
    }

    public ProjectStudyPostDetailDTO getDetail(Long id) {
        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);

        if (!projectStudyPostOptional.isPresent()) {
            throw new DataNotFoundException("게시글을 찾을 수 없습니다.");
        }

        // 현재 접속자의 이메일 가져오기
        String currentUserEmail = getAuthenticatedUserEmail();

        // 조회수 증가
        ProjectStudyPost projectStudyPost = projectStudyPostOptional.get();
        projectStudyPost.setViewCount(projectStudyPost.getViewCount() + 1);
        this.projectStudyPostRepository.save(projectStudyPost);

        // DTO 생성 시 현재 사용자 이메일 전달
        return new ProjectStudyPostDetailDTO(projectStudyPost, currentUserEmail);
    }


    public Map<String,Object> getPosts(Pageable pageable){

        Pageable sortedByIdPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id") // Change to ASC for ascending order
        );
        //문제점 : 전체에 대한 내림차순을 설정하기떄문에 테이블 전체를 스캔함

        Page<ProjectStudyPost> posts = projectStudyPostRepository.findAll(sortedByIdPageable);

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

        return response;
    }

    public Long update(ProjectStudyPostDTO projectStudyPostDTO, Long id) {

        ProjectStudyPost projectStudyPost = projectStudyPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        projectStudyPost.setTitle(projectStudyPostDTO.getTitle());
        projectStudyPost.setDescription(projectStudyPostDTO.getDescription());
        projectStudyPost.setPosition(projectStudyPostDTO.getPosition());
        projectStudyPost.setPeopleCount(projectStudyPostDTO.getPeopleCount());
        projectStudyPost.setDuration(projectStudyPostDTO.getDuration());
        projectStudyPost.setProjectMode(projectStudyPostDTO.getProjectMode());
        projectStudyPost.setRecruitmentDeadline(projectStudyPostDTO.getRecruitmentDeadline());
        projectStudyPost.setTechStack(projectStudyPostDTO.getTechStack());
        projectStudyPost.setRecruitmentType(projectStudyPostDTO.getRecruitmentType());
        projectStudyPost.setContactEmail(projectStudyPostDTO.getContactEmail());

        projectStudyPostRepository.save(projectStudyPost);

        return  projectStudyPost.getId();
    }
}
