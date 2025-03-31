package com.neutral.newspaper.news.keyword.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsSummaryDto {
    private String title;
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate date;

    private String content;
    private String sentiment;
    private String contentSummarized;
}
