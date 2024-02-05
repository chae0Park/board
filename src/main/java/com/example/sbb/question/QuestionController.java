package com.example.sbb.question;


import com.example.sbb.answer.AnswerForm;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor //questionRepository속성 포함하는 생성자 생성
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    //전체 질문 목록을 가져옴
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "searchType", defaultValue = "all") String searchType) {
        Page<Question> paging = this.questionService.getList(page,kw,searchType);
        model.addAttribute("paging", paging); //템플릿에 ${paging}이 "paging"임
        //added code - maxPage and consider calculating this based on the actyual numer or pages
        model.addAttribute("maxPage", 10);
        model.addAttribute("kw",kw);
        //To remember the selected seatch Type in the view
        model.addAttribute("searchType", searchType);
        return "question_list";
    }

    //상세 질문을 보여줌
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,
                         AnswerForm answerForm){
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question",question);
        return "question_detail";
    }

    //질문 등록하기-요청을 받아 폼으로 넘겨줌
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    //질문을 post하면 저장하고 redirect 해줄 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,
                     BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            return "question_form";
        }
        //글쓴이가 누군지 찾아야함
        SiteUser siteUser = userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(),siteUser);
        return "redirect:/question/list"; //질문 저장 후 질문목록으로 이동
    }

    //질문을 수정한다
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal){
        Question question = this.questionService.getQuestion(id);
        //만약 질문을 등록한 username이 로그인한 이용자의 username과 같지 않으면
        //ResponseStatusException()메소드를 던진다 오류와 메세지의 내용은 HttpStatus.BAD_REQUEST, "수정권한이 없습니다."
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        //id값으로 조회한 특정 질문의 제목과 내용이 나온다
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";

    }

    //질문 수정 등록 컨트롤러
    @PreAuthorize("isAuthenticated()")
    @PostMapping ("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm,
          BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id){
        if (bindingResult.hasErrors()){
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question,questionForm.getSubject(),
                questionForm.getContent());
        return  String.format("redirect:/question/detail/%s",id);
    }

    //서비스에서 삭제 메소드를 가져와서 삭제 / url과 매핑
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    //추천인
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
