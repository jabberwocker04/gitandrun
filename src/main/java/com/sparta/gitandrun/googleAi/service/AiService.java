package com.sparta.gitandrun.googleAi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gitandrun.googleAi.dto.AiDto;
import com.sparta.gitandrun.googleAi.entity.Ai;
import com.sparta.gitandrun.googleAi.repository.AiRepository;
import com.sparta.gitandrun.menu.dto.MenuResponseDto;
import com.sparta.gitandrun.menu.entity.Menu;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${google-api-key}")
    private String SECRET_KEY;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final AiRepository aiRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //CREATE
    public String createQuestion(String text) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + SECRET_KEY;

        JSONObject jsonObject0 = new JSONObject(); // contents Field 생성
        JSONArray contents = new JSONArray();

        JSONObject jsonObject1 = new JSONObject(); // parts Field 생성
        JSONArray parts = new JSONArray();

        JSONObject jsonObject2 = new JSONObject(); // text Field 생성

        jsonObject2.put("text",text + " 답변을 최대한 간결하게 50자 이하로");
        parts.put(jsonObject2);
        jsonObject1.put("parts",parts);
        contents.put(jsonObject1);
        jsonObject0.put("contents",contents);

        RequestBody body = RequestBody.create(
                jsonObject0.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .post(body)
                .build();

        JsonNode jsonNode; // Response 객체를 반환받을 Json 객체

        try {
            Response response = httpClient.newCall(request).execute();
            jsonNode = objectMapper.readTree(response.body().string());
             }
        catch (IOException e) {
            throw new RuntimeException("잘못된 Response 입니다.");
        }

        String answer = jsonNode.path("candidates")
                .get(0) // candidate 객체
                .path("content")
                .path("parts")
                .get(0) // parts 객체
                .path("text")
                .asText(); // "text" 필드를 문자열로 반환
        AiDto aiDto = new AiDto(text, answer);
        aiRepository.save(new Ai(aiDto));

        return answer;

    }

    //Read ( 전체 조회 )
    public List<AiDto> getAllQuestions() {
        List<Ai> aiList = aiRepository.findAll();
        List<AiDto> responseDtoList = new ArrayList<>();
        for (Ai ai : aiList) {
            responseDtoList.add(new AiDto(ai));
        }
        return responseDtoList;

    }
}