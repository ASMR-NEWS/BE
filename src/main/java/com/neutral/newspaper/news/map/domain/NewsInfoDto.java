package com.neutral.newspaper.news.map.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsInfoDto {
    private String title;
    private String link;
    private String description;
}
