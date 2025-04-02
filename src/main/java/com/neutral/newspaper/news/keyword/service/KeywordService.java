package com.neutral.newspaper.news.keyword.service;

import com.neutral.newspaper.news.keyword.domain.SearchNewsRequestDto;
import com.neutral.newspaper.news.keyword.domain.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final RestTemplate restTemplate;

    @Transactional
    public SearchResponseDto summarizeKeywordNews(SearchNewsRequestDto searchNewsRequest) {
        String url = "http://localhost:5000/topic-search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SearchNewsRequestDto> request = new HttpEntity<>(searchNewsRequest, headers);

        ResponseEntity<SearchResponseDto> response =
                restTemplate.exchange(url, HttpMethod.POST, request, SearchResponseDto.class);

        return response.getBody();
    }
}
