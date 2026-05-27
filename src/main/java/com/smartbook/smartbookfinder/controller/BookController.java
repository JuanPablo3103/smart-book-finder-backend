package com.smartbook.smartbookfinder.controller;

import com.smartbook.smartbookfinder.dto.BookDTO;
import com.smartbook.smartbookfinder.dto.SearchRequestDTO;
import com.smartbook.smartbookfinder.model.FavoriteBook;
import com.smartbook.smartbookfinder.model.SearchHistory;
import com.smartbook.smartbookfinder.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestBody SearchRequestDTO request) {
        List<BookDTO> books = bookService.searchBooks(request);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteBook> saveFavorite(@RequestBody Map<String, String> body) {
        FavoriteBook favorite = bookService.saveFavorite(
                body.get("bookKey"),
                body.get("title"),
                body.get("author"),
                body.get("coverUrl")
        );
        return ResponseEntity.ok(favorite);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SearchHistory>> getHistory() {
        return ResponseEntity.ok(bookService.getHistory());
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteBook>> getFavorites() {
        return ResponseEntity.ok(bookService.getFavorites());
    }
}