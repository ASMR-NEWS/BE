package com.neutral.newspaper.news.map.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchRegionNewsRequestDto {
    private String region;
    private String category;
}
