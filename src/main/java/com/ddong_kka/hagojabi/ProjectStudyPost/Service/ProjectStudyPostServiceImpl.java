package com.ddong_kka.hagojabi.ProjectStudyPost.Service;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Exception.DataNotFoundException;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Interface.ProjectStudyPostService;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
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
public class ProjectStudyPostServiceImpl implements ProjectStudyPostService {

    private final ProjectStudyPostRepository projectStudyPostRepository;
    private final UsersRepository usersRepository;

    public ProjectStudyPostServiceImpl(ProjectStudyPostRepository projectStudyPostRepository, UsersRepository usersRepository) {
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

    @Override
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

    @Override
    public ProjectStudyPostDetailDTO getDetail(Long id) {
        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);

        if (projectStudyPostOptional.isEmpty()) {
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


    @Override
    public Page<ProjectStudyPost> getPosts(Pageable pageable){

        return  projectStudyPostRepository.findAll(pageable);
    }

    @Override
    public Long update(ProjectStudyPostDTO projectStudyPostDTO, Long id) {

        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);


        if (projectStudyPostOptional.isPresent()){

            ProjectStudyPost projectStudyPost = projectStudyPostOptional.get();

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
        throw new IllegalArgumentException("Post not found with id : " + id);
    }

    @Override
    public void deletePost(Long id)  {
        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);
        if (projectStudyPostOptional.isPresent()){
            projectStudyPostRepository.deleteById(id);
        }else{
            throw new IllegalArgumentException("Post not found with id : " + id);
        }
    }
}
