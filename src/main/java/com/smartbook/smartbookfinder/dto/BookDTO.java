package com.smartbook.smartbookfinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private String key;
    private String title;
    private String author;
    private Integer firstPublishYear;
    private Integer editionCount;
    private String coverUrl;
}