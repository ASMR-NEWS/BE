package com.neutral.newspaper.news.map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.neutral.newspaper.news.map.domain.NewsInfoDto;
import com.neutral.newspaper.news.map.domain.SearchRegionNewsResponseDto;
import com.neutral.newspaper.news.map.service.MapService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

    @InjectMocks
    private MapService mapService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("지역 뉴스 검색 성공 케이스")
    void successFindingRegionNews() {
        // given
        NewsInfoDto newsInfo = new NewsInfoDto(
                "축제 뉴스 제목", "http://examplenews.com", "축제 뉴스 내용"
        );

        SearchRegionNewsResponseDto response = new SearchRegionNewsResponseDto(
                "서울", "축제", List.of(newsInfo)
        );

        when(restTemplate.getForEntity(
                anyString(), eq(SearchRegionNewsResponseDto.class), anyString(), anyString()
        )).thenReturn(ResponseEntity.ok(response));

        // when
        SearchRegionNewsResponseDto result = mapService.findRegionNews("서울", "경제");

        // then
        assertThat(result).isNotNull();
    }
}
