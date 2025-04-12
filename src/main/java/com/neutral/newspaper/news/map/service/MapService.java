package com.neutral.newspaper.news.map.service;

import com.neutral.newspaper.news.map.domain.SearchRegionNewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RestTemplate restTemplate;

    public SearchRegionNewsResponseDto findRegionNews(String region, String category) {
        String url = "http://localhost:5000/search_news?region={region}&category={category}";

        ResponseEntity<SearchRegionNewsResponseDto> response = restTemplate.getForEntity(
                url,
                SearchRegionNewsResponseDto.class,
                region,
                category
        );

        return response.getBody();
    }
}
