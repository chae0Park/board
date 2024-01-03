package com.example.sbb;

import com.example.sbb.answer.Answer;
import com.example.sbb.answer.AnswerRepository;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionRepository;
import com.example.sbb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest //해당 클래스가 테스트임을 의미
class SbbApplicationTests {

    @Autowired //옵시디언 참조, 객체 주입을 위해 사용
    private QuestionService questionService;

    @Test
    void testJpa() {
        //for조건식 사용해서 300개의 테스트 데이터를 생성한다
        //이 때 제목의 숫자도 함께 증가할 수 있도록 한다.
        for (int i=1; i <= 300; i++){
            String subject = String.format("[%03d]번째 테스트 데이터 입니다",i);
            String content = "내용없음";
            this.questionService.create(subject,content);
        }

    }

}
