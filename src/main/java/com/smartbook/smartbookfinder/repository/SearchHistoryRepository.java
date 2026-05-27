package com.smartbook.smartbookfinder.repository;

import com.smartbook.smartbookfinder.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findAllByOrderBySearchedAtDesc();
}