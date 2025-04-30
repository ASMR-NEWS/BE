package com.neutral.newspaper.news.map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neutral.newspaper.news.map.controller.MapController;
import com.neutral.newspaper.news.map.domain.NewsInfoDto;
import com.neutral.newspaper.news.map.domain.SearchRegionNewsResponseDto;
import com.neutral.newspaper.news.map.service.MapService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MapController.class)
public class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MapService mapService;

    @Autowired
    private ObjectMapper objectMapper;


    @WithMockUser
    @Test
    @DisplayName("지역 뉴스 검색 성공 시 200 반환")
    void successGettingRegionNews() throws Exception {
        // given
        NewsInfoDto newsInfo = new NewsInfoDto(
                "축제 뉴스 제목", "http://examplenews.com", "축제 뉴스 설명"
        );

        SearchRegionNewsResponseDto response = new SearchRegionNewsResponseDto(
                "서울", "축제", List.of(newsInfo)
        );

        when(mapService.findRegionNews(anyString(), anyString())).thenReturn(response);

        // when, then
        mockMvc.perform(post("/map")
                        .param("region", "서울")
                        .param("category", "축제")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
