package com.neutral.newspaper.news.keyword.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDto {
    private List<NewsSummaryDto> news;
}
