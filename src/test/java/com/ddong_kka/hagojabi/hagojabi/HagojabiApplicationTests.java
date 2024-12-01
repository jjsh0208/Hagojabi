package com.ddong_kka.hagojabi.hagojabi;

import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.hibernate.annotations.SecondaryRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostCreationTest {

	@Autowired
	private ProjectStudyPostRepository projectStudyPostRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Test
	@Rollback(value = false)
	void createPostTest() {
		// Step 1: Retrieve a user from the database
		Users user = usersRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));

		System.out.println("find user : "  + user.getUsername());

// Step 2: Create 100 new posts using the ProjectStudyPost builder
		for (int i = 1; i < 150; i++) {
			ProjectStudyPost post = ProjectStudyPost.builder()
					.title("springboot3 스터디 같이 하시분! " + i) // Dynamic title with index
					.description("같이 대구에서 스터디 하실 분 구합니다!")
					.create_at(LocalDateTime.now())
					.update_at(LocalDateTime.now())
					.position(Arrays.asList("프론트엔드", "백엔드"))
					.peopleCount("인원미정")
					.duration("1개월")
					.projectMode("오프라인")
					.recruitmentDeadline(LocalDate.now().plusDays(30))
					.techStack(Arrays.asList("Java", "Spring", "React"))
					.recruitmentType("스터디")
					.contactEmail("testuser@example.com")
					.user(user)
					.build();

			// Step 3: Save the post to the repository
			ProjectStudyPost savedPost = projectStudyPostRepository.save(post);

			// Step 4: Assert that the post was successfully saved
			assertNotNull(savedPost.getId(), "The post ID should not be null after saving.");
			// Assert that the title matches the dynamically assigned value
			assertEquals("springboot3 스터디 같이 하시분! " + i, savedPost.getTitle(), "The post title should match.");
			assertEquals("testuser@example.com", savedPost.getContactEmail(), "The post contact email should match.");
		}





	}
}
