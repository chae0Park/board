package com.example.sbb.answer;

import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Answer parent;

    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    private List<Answer> children = new ArrayList<>();

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    // Additional methods for managing bidirectional relationship

    public void addChild(Answer child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Answer child) {
        children.remove(child);
        child.setParent(null);
    }
}
