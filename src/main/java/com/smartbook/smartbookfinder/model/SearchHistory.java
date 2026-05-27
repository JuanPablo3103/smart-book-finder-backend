package com.smartbook.smartbookfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String language;
    private Integer publishedAfter;
    private LocalDateTime searchedAt;

    @PrePersist
    public void prePersist() {
        this.searchedAt = LocalDateTime.now();
    }
}