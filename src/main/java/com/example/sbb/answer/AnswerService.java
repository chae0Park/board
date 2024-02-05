package com.example.sbb.answer;

import com.example.sbb.DataNotFoundException;
import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author){
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setAuthor(author);
        answer.setQuestion(question);
        this.answerRepository.save(answer);
        return answer;
    }


    //child Answer create
    public Answer createChild(Answer parent, String content, SiteUser author) {
        Answer child = new Answer();
        child.setContent(content);
        child.setCreateDate(LocalDateTime.now());
        child.setAuthor(author);
        child.setParent(parent); // Set the parent-child relationship
        //save the child answer
        this.answerRepository.save(child);
        //update the child answer
        parent.addChild(child); // Add the child to the parent's collection
        this.answerRepository.save(parent); // Save the parent to update the collection
        return child;
    }


    @Transactional
    public Answer getAnswer(Integer id){
        Optional<Answer> answer = this.answerRepository.findById(id);

        if(answer.isPresent()){
            return answer.get();
        }else{
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content){
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }


    //댓글삭제
    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

    //대댓글 삭제
    @Transactional
    public void deleteChild(Answer parent,Answer child){
        parent.removeChild(child);
        // save the parent answer to update the change in the database
        answerRepository.save(parent);
        //delete the child answer from the database
        answerRepository.delete(child);
        //to refresh the parent > refresh method provided by your JPA repository or entity manager.
        //answerRepository.refresh(parent); // Refresh the state of the parent answer


    }


    public void vote(Answer answer, SiteUser siteUser){
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }

}
