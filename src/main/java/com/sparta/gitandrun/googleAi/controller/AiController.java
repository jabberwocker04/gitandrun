package com.sparta.gitandrun.googleAi.controller;

import com.sparta.gitandrun.googleAi.dto.AiDto;
import com.sparta.gitandrun.googleAi.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    //Question CREATE
    @PostMapping
    public String createQuestion(@RequestBody String text) { // json 받아오게
        return aiService.createQuestion(text);
    }

    @GetMapping
    public List<AiDto> ReadQuestions(){
        return aiService.getAllQuestions();
    }

}
