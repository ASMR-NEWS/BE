package com.neutral.newspaper.news.keyword;

import com.neutral.newspaper.news.keyword.domain.SearchNewsRequestDto;
import com.neutral.newspaper.news.keyword.domain.SearchResponseDto;
import com.neutral.newspaper.news.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping
    public ResponseEntity<SearchResponseDto> getKeywordSummary(@RequestBody SearchNewsRequestDto searchNewsRequest) {
        SearchResponseDto response = keywordService.summarizeKeywordNews(searchNewsRequest);
        return ResponseEntity.ok(response);
    }
}
