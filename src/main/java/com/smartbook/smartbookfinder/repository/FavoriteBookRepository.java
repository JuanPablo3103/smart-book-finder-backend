package com.smartbook.smartbookfinder.repository;

import com.smartbook.smartbookfinder.model.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {

    boolean existsByBookKey(String bookKey);
    Optional<FavoriteBook> findByBookKey(String bookKey);
}