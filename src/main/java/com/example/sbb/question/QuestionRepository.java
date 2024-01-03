package com.example.sbb.question;

import com.example.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*  QuestionRepository를 만드는 이유
* 라포지터리는 엔티티에 의해 생성된 db테이블에 접근하는 메서드들 i.e findAll, save
* 을 사용하기 위한 인터페이스이다. 데이터 처리를 위해 테이블에 어떤 값을 넣거나 값을
* 조회하는 등의 crud가 필요하고 이 리포지터리 계층에서 crud를 어떻게 처리할지 정의한다.*/
public interface QuestionRepository extends JpaRepository<Question,Integer> {
/*QuestionRepository인터페이스를 crud를 처리하는 리포지터리로 만들기 위해
* Jpa인터페이스를 상속했다.
* JpaRepository를 상속할 때는 제네릭스 타입으로 <Question, Integer>처럼
* 리포지터리의 대상이 되는 엔티티의 타입(Question)과 해당 엔티티의 pk속성 타입 (Integer)
* 을 지정해야한다. 이것은 JpaRepository를 생성하기 위한 규칙이다.*/

    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    //Pageable 객체를 입력받아 Page<Question> 타입 객체를 리턴하는 findAll()생성
    Page<Question> findAll(Pageable pageable);

}

