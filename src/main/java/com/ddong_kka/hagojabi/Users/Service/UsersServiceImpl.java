package com.ddong_kka.hagojabi.Users.Service;

import com.ddong_kka.hagojabi.Config.JWT.JwtUtil;
import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Exception.EmailDuplicateException;
import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Interface.UsersService;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder encoder;

    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
    }

    public String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof PrincipalDetails) {
            return ((PrincipalDetails) principal).getUsername(); // JWT에 포함된 이메일 또는 사용자 이름
        } else if (principal != null) {
            return principal.toString();
        } else {
            throw new UserNotFoundException("Authentication object is invalid or null.");
        }
    }


    public boolean existsByEmail(String email){
        return usersRepository.existsByEmail(email);
    }

    @Override
    public void register(UsersDTO usersDTO){

        if (existsByEmail(usersDTO.getEmail())){
            throw new EmailDuplicateException("Email already exist : "+ usersDTO.getEmail());
        }

        String encPassword = encoder.encode(usersDTO.getPassword());

        Users users = Users.builder()
                .email(usersDTO.getEmail())
                .username(usersDTO.getUsername())
                .password(encPassword)
                .role("ROLE_USER")
                .build();

        usersRepository.save(users);

    }

    @Override
    public UserDetailDTO getUserInfo() {

        String userEmail = getAuthenticatedUserEmail();

        Users users = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        return new UserDetailDTO(users);
    }

    @Override
    public UserDetailDTO userNameUpdate(UserDetailDTO userDetailDTO) {

        if (userDetailDTO.getEmail() == null || userDetailDTO.getUsername() == null){
            throw new IllegalArgumentException("Email and username must not be null.");
        }

        Users users = usersRepository.findByEmail(userDetailDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userDetailDTO.getEmail()));

        users.setUsername(userDetailDTO.getUsername());

        // 변경 후 저장
        usersRepository.save(users);

        return new UserDetailDTO(users);
    }

    @Override
    public void passwordChange(UserDetailDTO userDetailDTO) {

        // 입력 데이터 검증
        if (userDetailDTO.getPassword() == null || userDetailDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }

        String userEmail = getAuthenticatedUserEmail();

        Users users = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 새로운 비밀번호 암호화 및 저장
        String encPassword = encoder.encode(userDetailDTO.getPassword());
        users.setPassword(encPassword);

        usersRepository.save(users);
    }

}
