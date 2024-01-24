package com.example.sbb.question;

import com.example.sbb.DataNotFoundException;
import com.example.sbb.answer.Answer;
import com.example.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor //final로 지정된 객체를 자동생성
@Service
public class QuestionService {//서비스는 비즈니스 로직을 처리한다.

    private final QuestionRepository questionRepository;

    //질문 목록 조회메소드
    public Page<Question> getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
        //검색
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec,pageable);
    }

    //question_detail
    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);

        if (question.isPresent()){
            return question.get();
        }else{
            throw new DataNotFoundException("question not found");
        }
    }

    //질문 등록하기 버튼을 눌러서 질문생성
    //작성자를 추가하지만 유저 리포지토리를 변수선언하진 않음 누굴 찾아서 넣는건 아니라서
    //저장할 때 저 칼럼도 저장된다 정도로만 생각하고 누군지 찾는 작업은 컨트롤러에서 할거임
    public void create(String subject, String content, SiteUser user){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(user);
        q.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q);
    }

    //질문을 수정하는 로직
    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    //질문을 삭제한다
    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    //추천인을 저장한다
    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    //검색을 위헤 JPA의 Specification 인터페이스를 사용
    private Specification<Question> search(String kw){
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); //중복을 제거
                //Question, SiteUser를 조인하여 만든 SiteUser타입의 u1 - 질문작성자 검색
                Join<Question, SiteUser> u1 = q.join("author",JoinType.LEFT);
                //Question, Answer를 조인하여 만든 Answer타입의 a - 답변 내용 검색
                Join<Question, Answer> a = q.join("answerList",JoinType.LEFT);
                //Answer, SiteUser를 조인하여 만든 SiteUser타입의 u2 - 답변 작성자 검색
                Join<Answer, SiteUser> u2 = a.join("author",JoinType.LEFT);

                return  cb.or(cb.like(q.get("subject"), "%" + kw + "%"), //질문제목
                        cb.like(q.get("content"), "%" + kw + "%"),//질문내용
                        cb.like(u1.get("username"), "%" + kw + "%"),//질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),//답변내용
                        cb.like(u2.get("username"), "%" + kw + "%"));//답변작성자
            }
        };
    }
}
