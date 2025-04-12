package com.neutral.newspaper.news.map.service;

import com.neutral.newspaper.news.map.domain.SearchRegionNewsRequestDto;
import com.neutral.newspaper.news.map.domain.SearchRegionNewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RestTemplate restTemplate;

    public SearchRegionNewsResponseDto findRegionNews(SearchRegionNewsRequestDto searchRegionNewsRequest) {
        String url = "http://localhost:5000/search_news";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SearchRegionNewsRequestDto> request = new HttpEntity<>(searchRegionNewsRequest, headers);

        ResponseEntity<SearchRegionNewsResponseDto> response =
                restTemplate.exchange(url, HttpMethod.GET, request, SearchRegionNewsResponseDto.class);

        return response.getBody();
    }
}
