package com.neutral.newspaper.news.map.controller;

import com.neutral.newspaper.news.map.domain.SearchRegionNewsResponseDto;
import com.neutral.newspaper.news.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @PostMapping
    public ResponseEntity<SearchRegionNewsResponseDto> getRegionNews(
            @RequestParam String region, @RequestParam String category) {
        SearchRegionNewsResponseDto response = mapService.findRegionNews(region, category);
        return ResponseEntity.ok(response);
    }
}
