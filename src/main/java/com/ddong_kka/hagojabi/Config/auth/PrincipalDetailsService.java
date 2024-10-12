package com.ddong_kka.hagojabi.Config.auth;

import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 자동으로 실행
//  함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    //리턴은 시큐리티 session -> Authentication = UserDetails
    //리턴된값은 시큐리티 session(내부 Authentication (내부 UserDetails)) 안에 들어간다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users usersEntity = usersRepository.findByEmail(email);

        // Debugging statement
        System.out.println("User found: " + (usersEntity != null ? usersEntity.getEmail() : "No user found"));

        if (usersEntity != null){ //user가 디비에 존재할때만

            return new PrincipalDetails(usersEntity);
        }
        return null;
    }
}
