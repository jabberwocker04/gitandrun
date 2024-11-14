package com.sparta.gitandrun.googleAi.dto;

import com.sparta.gitandrun.googleAi.entity.Ai;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiDto {

    private String question;
    private String answer;

    private List<AiDto> aiList = new ArrayList<>();

    public AiDto(Ai ai) {
        this.question = ai.getQuestion();
        this.answer = ai.getAnswer();
    }

    public AiDto(String Question, String Answer) {
        this.question = Question;
        this.answer = Answer;
    }

}
