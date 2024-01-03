package com.example.sbb.question;

import com.example.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor //final로 지정된 객체를 자동생성
@Service
public class QuestionService {//서비스는 비즈니스 로직을 처리한다.

    private final QuestionRepository questionRepository;

    //질문 목록 조회메소드
    public Page<Question> getList(int page){
        //Pageable 타입의 객체 생성시 PageRequest.of(조회페이지번호,페이지 당 게시물 갯수)
        //를 이용하고 리포지토리를 이용해서 조회할 때 이렇게 생성한 pageable객체를 findAll(pageable)로 넣어줌
        Pageable pageable = PageRequest.of(page,10);
        return this.questionRepository.findAll(pageable);
    }

    //특정 질문 조회메소드
    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);

        if (question.isPresent()){
            return question.get();
        }else{
            throw new DataNotFoundException("question not found");
        }
    }

    //질문 등록하기 버튼을 눌러서 질문생성
    public void create(String subject,String content){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q);
    }

}
