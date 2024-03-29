package com.example.sbb.answer;

import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService; //답변을 작성하기 위해 사용자가 누른
    //게시물에 해당하는 아이디를 가진 객체를 찾기위해 선언됨
    private final AnswerService answerService;
    //생성한 답변의 값을 저장하기 위해 선언됨.
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid AnswerForm answerForm, BindingResult bindingResult,
                               Principal principal) {

        // Get the question
        Question question = this.questionService.getQuestion(id);

        // Validate the form input
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        // Create the answer (either parent or child)
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Answer answer;
        if (answerForm.getParentId() != null) {
            // If parentId is present in the form, create a child answer
            Answer parentAnswer = this.answerService.getAnswer(answerForm.getParentId());
            answer = this.answerService.createChild(parentAnswer, answerForm.getContent(), siteUser);
        } else {
            // If parentId is not present, create a regular answer
            answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        }

        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(),
                answer.getId());
    }

    // child answer
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/child/{parentId}")
    public String createChildAnswer(Model model,
                                    @PathVariable("parentId") Integer parentId,
                                    @Valid AnswerForm answerForm, BindingResult bindingResult,
                                    Principal principal) {
        // Get the parent answer
        Answer parentAnswer = this.answerService.getAnswer(parentId);

        // Validate the form input
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", parentAnswer.getQuestion());
            return "question_detail";
        }

        // Create the child answer
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Answer childAnswer = this.answerService.createChild(parentAnswer, answerForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s#answer_%s", parentAnswer.getQuestion().getId(),
                childAnswer.getId());
    }


    //수정
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm,@PathVariable("id") Integer id,
                               Principal principal){
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal){
        if(bindingResult.hasErrors()){
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    //댓글삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal,@PathVariable("id") Integer id){
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    //대댓글 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/child/{id}/{childId}")
    public String deleteChildAnswer(Principal principal,
                                    @PathVariable("childId") Integer childId){
        Answer childAnswer = answerService.getAnswer(childId);// get a certain child answer

        //get the child's parent
        Answer parentAnswer = childAnswer.getParent();

        //check if the current user is the author of the child answer
        if(!childAnswer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }
        //fetch questionId before deletion
        Integer questionId = parentAnswer.getQuestion().getId();

        //Use service method to delete the child answer
        answerService.deleteChild(parentAnswer, childAnswer);
        return String.format("redirect:/question/detail/%s", questionId);
    }

    //추천인등록
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id){
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        this.answerService.vote(answer,siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(), answer.getId());
    }
}
