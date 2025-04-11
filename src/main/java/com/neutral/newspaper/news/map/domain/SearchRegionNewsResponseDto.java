package com.neutral.newspaper.news.map.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchRegionNewsResponseDto {
    private String region;
    private String category;
    private List<NewsInfoDto> news;
}
