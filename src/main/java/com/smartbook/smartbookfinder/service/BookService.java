package com.smartbook.smartbookfinder.service;

import com.smartbook.smartbookfinder.client.OpenLibraryClient;
import com.smartbook.smartbookfinder.dto.BookDTO;
import com.smartbook.smartbookfinder.dto.SearchRequestDTO;
import com.smartbook.smartbookfinder.exception.InsufficientResultsException;
import com.smartbook.smartbookfinder.exception.InvalidSearchException;
import com.smartbook.smartbookfinder.model.FavoriteBook;
import com.smartbook.smartbookfinder.model.SearchHistory;
import com.smartbook.smartbookfinder.repository.FavoriteBookRepository;
import com.smartbook.smartbookfinder.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Set<String> VALID_LANGUAGES = Set.of("eng", "spa", "por", "fre", "ger");
    private static final int MIN_RESULTS = 3;

    private final OpenLibraryClient openLibraryClient;
    private final SearchHistoryRepository searchHistoryRepository;
    private final FavoriteBookRepository favoriteBookRepository;

    public BookService(OpenLibraryClient openLibraryClient,
                       SearchHistoryRepository searchHistoryRepository,
                       FavoriteBookRepository favoriteBookRepository) {
        this.openLibraryClient = openLibraryClient;
        this.searchHistoryRepository = searchHistoryRepository;
        this.favoriteBookRepository = favoriteBookRepository;
    }

    public List<BookDTO> searchBooks(SearchRequestDTO request) {

        // Validación 1: title o author obligatorio
        if ((request.getTitle() == null || request.getTitle().isBlank()) &&
                (request.getAuthor() == null || request.getAuthor().isBlank())) {
            throw new InvalidSearchException("Debe ingresar al menos un título o autor.");
        }

        // Validación 2: publishedAfter no puede ser mayor al año actual
        int currentYear = Year.now().getValue();
        if (request.getPublishedAfter() != null && request.getPublishedAfter() > currentYear) {
            throw new InvalidSearchException("El año de publicación no puede ser mayor al año actual.");
        }

        // Validación 3: idioma válido
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            if (!VALID_LANGUAGES.contains(request.getLanguage().toLowerCase())) {
                throw new InvalidSearchException("Idioma no válido. Use: eng, spa, por, fre, ger.");
            }
        }

        // Guardar historial
        SearchHistory history = new SearchHistory();
        history.setTitle(request.getTitle());
        history.setAuthor(request.getAuthor());
        history.setLanguage(request.getLanguage());
        history.setPublishedAfter(request.getPublishedAfter());
        searchHistoryRepository.save(history);

        // Consumir API externa
        List<BookDTO> books = openLibraryClient.searchBooks(request.getTitle(), request.getAuthor());

        // Validación 5: filtrar libros anteriores al año solicitado
        if (request.getPublishedAfter() != null) {
            books = books.stream()
                    .filter(b -> b.getFirstPublishYear() != null &&
                            b.getFirstPublishYear() >= request.getPublishedAfter())
                    .collect(Collectors.toList());
        }

        // Validación 4: mínimo 3 resultados
        if (books.size() < MIN_RESULTS) {
            throw new InsufficientResultsException(
                    "No se encontraron suficientes resultados. Mínimo requerido: " + MIN_RESULTS);
        }

        return books;
    }

    public FavoriteBook saveFavorite(String bookKey, String title, String author, String coverUrl) {
        if (favoriteBookRepository.existsByBookKey(bookKey)) {
            return favoriteBookRepository.findByBookKey(bookKey).get();
        }
        FavoriteBook favorite = new FavoriteBook();
        favorite.setBookKey(bookKey);
        favorite.setTitle(title);
        favorite.setAuthor(author);
        favorite.setCoverUrl(coverUrl);
        return favoriteBookRepository.save(favorite);
    }

    public List<SearchHistory> getHistory() {
        return searchHistoryRepository.findAllByOrderBySearchedAtDesc();
    }

    public List<FavoriteBook> getFavorites() {
        return favoriteBookRepository.findAll();
    }
}