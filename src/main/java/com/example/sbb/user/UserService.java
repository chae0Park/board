package com.example.sbb.user;

import com.example.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //생성 create(가입 시 입력되는 정보들)
    public SiteUser create(String username, String email, String password){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    //user를 조회하는 메소드
    public SiteUser getUser(String username){
        //SiteUser 타입의 객체를 username으로 찾아냄, 즉 SiteUser객체 하나가 들어가있음
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
