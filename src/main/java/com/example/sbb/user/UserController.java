package com.example.sbb.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    //회원가입
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult){
        //입력값이 유효성 검사를 통과하지 못하면 다시 로그인 폼으로
        if(bindingResult.hasErrors()){
            return "signup_form";
        }
        //비밀번호와 비밀번호 확인 값이 다르면 다시 로그인 폼으로
        //Validation을 마친 결과를 담는 bindingResult에서 password1 과 password2가
        //다르다면 에러메세지를 띄운다. 이 때 사용하는 attribute 는 rejectValue(String field, String errorcode, String defaultMessage)
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2","passwordInCorrect",
                    "패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        userService.create(userCreateForm.getUsername(),
                userCreateForm.getEmail(), userCreateForm.getPassword1());
        return "redirect:/"; //로그인에 성공하고 나면 홈으로 돌아가도록
    }

    //로그인 컨트롤러
    @GetMapping("/login")
    public String login(){
        return "login_form";
    }
}
