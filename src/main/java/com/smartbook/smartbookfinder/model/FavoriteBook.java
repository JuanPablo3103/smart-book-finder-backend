package com.smartbook.smartbookfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String bookKey;

    private String title;
    private String author;
    private String coverUrl;
    private LocalDateTime savedAt;

    @PrePersist
    public void prePersist() {
        this.savedAt = LocalDateTime.now();
    }
}