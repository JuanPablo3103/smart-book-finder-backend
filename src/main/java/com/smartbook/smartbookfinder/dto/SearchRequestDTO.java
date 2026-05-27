package com.smartbook.smartbookfinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDTO {

    private String title;
    private String author;
    private String language;
    private Integer publishedAfter;
}