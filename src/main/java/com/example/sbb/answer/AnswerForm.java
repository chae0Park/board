package com.example.sbb.answer;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerForm {
    @NotEmpty(message = "내용을 입력해주세요")
    private String content;

    // New field to capture the parent answer's ID
    private Integer parentId;
}
