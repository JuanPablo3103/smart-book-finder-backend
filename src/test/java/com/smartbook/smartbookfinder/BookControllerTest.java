package com.smartbook.smartbookfinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbook.smartbookfinder.dto.BookDTO;
import com.smartbook.smartbookfinder.dto.SearchRequestDTO;
import com.smartbook.smartbookfinder.exception.InsufficientResultsException;
import com.smartbook.smartbookfinder.exception.InvalidSearchException;
import com.smartbook.smartbookfinder.model.FavoriteBook;
import com.smartbook.smartbookfinder.model.SearchHistory;
import com.smartbook.smartbookfinder.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smartbook.smartbookfinder.controller.BookController;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    // ===================== POST /api/books/search =====================

    @Test
    void searchBooks_whenValidRequest_returns200WithBooks() throws Exception {
        List<BookDTO> books = List.of(
                new BookDTO("/works/1", "Harry Potter", "J.K. Rowling", 1997, 50, "http://cover.jpg"),
                new BookDTO("/works/2", "Harry Potter 2", "J.K. Rowling", 1998, 45, "http://cover2.jpg"),
                new BookDTO("/works/3", "Harry Potter 3", "J.K. Rowling", 1999, 40, "http://cover3.jpg")
        );
        when(bookService.searchBooks(any())).thenReturn(books);

        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "eng", 1990);

        mockMvc.perform(post("/api/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"))
                .andExpect(jsonPath("$[0].author").value("J.K. Rowling"));
    }

    @Test
    void searchBooks_whenInvalidRequest_returns400() throws Exception {
        when(bookService.searchBooks(any()))
                .thenThrow(new InvalidSearchException("Debe enviar título o autor."));

        SearchRequestDTO request = new SearchRequestDTO(null, null, null, null);

        mockMvc.perform(post("/api/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void searchBooks_whenInsufficientResults_returns422() throws Exception {
        when(bookService.searchBooks(any()))
                .thenThrow(new InsufficientResultsException("No se encontraron suficientes resultados."));

        SearchRequestDTO request = new SearchRequestDTO("XYZ", null, null, null);

        mockMvc.perform(post("/api/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }

    // ===================== POST /api/books/favorites =====================

    @Test
    void saveFavorite_whenValidRequest_returns200() throws Exception {
        FavoriteBook favorite = new FavoriteBook();
        favorite.setBookKey("/works/1");
        favorite.setTitle("Harry Potter");
        favorite.setAuthor("J.K. Rowling");
        favorite.setCoverUrl("http://cover.jpg");

        when(bookService.saveFavorite(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(favorite);

        Map<String, String> body = new HashMap<>();
        body.put("bookKey", "/works/1");
        body.put("title", "Harry Potter");
        body.put("author", "J.K. Rowling");
        body.put("coverUrl", "http://cover.jpg");

        mockMvc.perform(post("/api/books/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookKey").value("/works/1"))
                .andExpect(jsonPath("$.title").value("Harry Potter"));
    }

    // ===================== GET /api/books/history =====================

    @Test
    void getHistory_returns200WithList() throws Exception {
        SearchHistory h1 = new SearchHistory();
        h1.setTitle("Harry Potter");
        SearchHistory h2 = new SearchHistory();
        h2.setTitle("Clean Code");

        when(bookService.getHistory()).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/books/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));
    }

    @Test
    void getHistory_whenEmpty_returns200WithEmptyList() throws Exception {
        when(bookService.getHistory()).thenReturn(List.of());

        mockMvc.perform(get("/api/books/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ===================== GET /api/books/favorites =====================

    @Test
    void getFavorites_returns200WithList() throws Exception {
        FavoriteBook f1 = new FavoriteBook();
        f1.setTitle("Harry Potter");
        FavoriteBook f2 = new FavoriteBook();
        f2.setTitle("Clean Code");

        when(bookService.getFavorites()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/books/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));
    }

    @Test
    void getFavorites_whenEmpty_returns200WithEmptyList() throws Exception {
        when(bookService.getFavorites()).thenReturn(List.of());

        mockMvc.perform(get("/api/books/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}