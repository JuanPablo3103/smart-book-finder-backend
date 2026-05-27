package com.smartbook.smartbookfinder;

import com.smartbook.smartbookfinder.client.OpenLibraryClient;
import com.smartbook.smartbookfinder.dto.BookDTO;
import com.smartbook.smartbookfinder.dto.SearchRequestDTO;
import com.smartbook.smartbookfinder.exception.InsufficientResultsException;
import com.smartbook.smartbookfinder.exception.InvalidSearchException;
import com.smartbook.smartbookfinder.model.FavoriteBook;
import com.smartbook.smartbookfinder.model.SearchHistory;
import com.smartbook.smartbookfinder.repository.FavoriteBookRepository;
import com.smartbook.smartbookfinder.repository.SearchHistoryRepository;
import com.smartbook.smartbookfinder.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private OpenLibraryClient openLibraryClient;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private FavoriteBookRepository favoriteBookRepository;

    @InjectMocks
    private BookService bookService;

    private List<BookDTO> mockBooks;

    @BeforeEach
    void setUp() {
        mockBooks = new ArrayList<>();
        mockBooks.add(new BookDTO("/works/1", "Harry Potter 1", "J.K. Rowling", 1997, 50, "http://cover1.jpg"));
        mockBooks.add(new BookDTO("/works/2", "Harry Potter 2", "J.K. Rowling", 1998, 45, "http://cover2.jpg"));
        mockBooks.add(new BookDTO("/works/3", "Harry Potter 3", "J.K. Rowling", 1999, 40, "http://cover3.jpg"));
        mockBooks.add(new BookDTO("/works/4", "Harry Potter 4", "J.K. Rowling", 2000, 35, "http://cover4.jpg"));
    }

    // ===================== VALIDACIÓN 1 =====================

    @Test
    void searchBooks_whenTitleAndAuthorEmpty_throwsInvalidSearchException() {
        SearchRequestDTO request = new SearchRequestDTO("", "", "eng", 1990);
        assertThrows(InvalidSearchException.class, () -> bookService.searchBooks(request));
    }

    @Test
    void searchBooks_whenTitleAndAuthorNull_throwsInvalidSearchException() {
        SearchRequestDTO request = new SearchRequestDTO(null, null, "eng", 1990);
        assertThrows(InvalidSearchException.class, () -> bookService.searchBooks(request));
    }

    @Test
    void searchBooks_whenOnlyTitleProvided_success() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "eng", 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void searchBooks_whenOnlyAuthorProvided_success() {
        when(openLibraryClient.searchBooks(any(), anyString())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO(null, "J.K. Rowling", "eng", 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ===================== VALIDACIÓN 2 =====================

    @Test
    void searchBooks_whenPublishedAfterGreaterThanCurrentYear_throwsInvalidSearchException() {
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "eng", 9999);
        assertThrows(InvalidSearchException.class, () -> bookService.searchBooks(request));
    }

    @Test
    void searchBooks_whenPublishedAfterIsCurrentYear_success() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "eng", 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
    }

    // ===================== VALIDACIÓN 3 =====================

    @Test
    void searchBooks_whenLanguageInvalid_throwsInvalidSearchException() {
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "xyz", 1990);
        assertThrows(InvalidSearchException.class, () -> bookService.searchBooks(request));
    }

    @Test
    void searchBooks_whenLanguageIsEng_success() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "eng", 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
    }

    @Test
    void searchBooks_whenLanguageIsSpa_success() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, "spa", 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
    }

    @Test
    void searchBooks_whenLanguageIsNull_success() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, 1990);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
    }

    // ===================== VALIDACIÓN 4 =====================

    @Test
    void searchBooks_whenLessThan3Results_throwsInsufficientResultsException() {
        List<BookDTO> fewBooks = new ArrayList<>();
        fewBooks.add(new BookDTO("/works/1", "Book 1", "Author 1", 2000, 5, "http://cover1.jpg"));
        fewBooks.add(new BookDTO("/works/2", "Book 2", "Author 2", 2001, 3, "http://cover2.jpg"));
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(fewBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, null);
        assertThrows(InsufficientResultsException.class, () -> bookService.searchBooks(request));
    }

    @Test
    void searchBooks_whenExactly3Results_success() {
        List<BookDTO> threeBooks = new ArrayList<>();
        threeBooks.add(new BookDTO("/works/1", "Book 1", "Author 1", 2000, 5, "http://cover1.jpg"));
        threeBooks.add(new BookDTO("/works/2", "Book 2", "Author 2", 2001, 3, "http://cover2.jpg"));
        threeBooks.add(new BookDTO("/works/3", "Book 3", "Author 3", 2002, 2, "http://cover3.jpg"));
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(threeBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, null);
        List<BookDTO> result = bookService.searchBooks(request);
        assertEquals(3, result.size());
    }

    // ===================== VALIDACIÓN 5 =====================

    @Test
    void searchBooks_whenPublishedAfterFiltersBooks_returnsOnlyValidBooks() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, 1997);
        List<BookDTO> result = bookService.searchBooks(request);
        assertTrue(result.stream().allMatch(b -> b.getFirstPublishYear() >= 1997));
    }

    @Test
    void searchBooks_whenPublishedAfterIsNull_returnsAllBooks() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, null);
        List<BookDTO> result = bookService.searchBooks(request);
        assertEquals(mockBooks.size(), result.size());
    }

    // ===================== FAVORITOS =====================

    @Test
    void saveFavorite_whenBookNotExists_saveAndReturn() {
        when(favoriteBookRepository.existsByBookKey(anyString())).thenReturn(false);
        when(favoriteBookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        FavoriteBook result = bookService.saveFavorite("/works/1", "Harry Potter", "J.K. Rowling", "http://cover.jpg");
        assertNotNull(result);
        assertEquals("/works/1", result.getBookKey());
        verify(favoriteBookRepository, times(1)).save(any());
    }

    @Test
    void saveFavorite_whenBookAlreadyExists_returnExisting() {
        FavoriteBook existing = new FavoriteBook(1L, "/works/1", "Harry Potter", "J.K. Rowling", "http://cover.jpg", null);
        when(favoriteBookRepository.existsByBookKey(anyString())).thenReturn(true);
        when(favoriteBookRepository.findByBookKey(anyString())).thenReturn(Optional.of(existing));
        FavoriteBook result = bookService.saveFavorite("/works/1", "Harry Potter", "J.K. Rowling", "http://cover.jpg");
        assertEquals(existing, result);
        verify(favoriteBookRepository, never()).save(any());
    }

    // ===================== HISTORIAL =====================

    @Test
    void getHistory_returnsListFromRepository() {
        List<SearchHistory> history = List.of(new SearchHistory(), new SearchHistory());
        when(searchHistoryRepository.findAllByOrderBySearchedAtDesc()).thenReturn(history);
        List<SearchHistory> result = bookService.getHistory();
        assertEquals(2, result.size());
    }

    @Test
    void getFavorites_returnsListFromRepository() {
        List<FavoriteBook> favorites = List.of(new FavoriteBook(), new FavoriteBook());
        when(favoriteBookRepository.findAll()).thenReturn(favorites);
        List<FavoriteBook> result = bookService.getFavorites();
        assertEquals(2, result.size());
    }

    // ===================== HISTORIAL GUARDADO =====================

    @Test
    void searchBooks_savesHistoryOnEverySearch() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, null);
        bookService.searchBooks(request);
        verify(searchHistoryRepository, times(1)).save(any());
    }
    // ===================== MUTANTES SOBREVIVIDOS - BOUNDARY =====================

    @Test
    void searchBooks_whenPublishedAfterIsExactlyCurrentYear_success() {
        int currentYear = java.time.Year.now().getValue();
        List<BookDTO> currentYearBooks = new ArrayList<>();
        currentYearBooks.add(new BookDTO("/works/1", "Book A", "Author A", currentYear, 10, "http://a.jpg"));
        currentYearBooks.add(new BookDTO("/works/2", "Book B", "Author B", currentYear, 8, "http://b.jpg"));
        currentYearBooks.add(new BookDTO("/works/3", "Book C", "Author C", currentYear, 6, "http://c.jpg"));
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(currentYearBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, currentYear);
        List<BookDTO> result = bookService.searchBooks(request);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void searchBooks_whenPublishedAfterEqualsBookYear_bookIsIncluded() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        // publishedAfter = 1997, libro con año 1997 debe incluirse (>=)
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, 1997);
        List<BookDTO> result = bookService.searchBooks(request);
        assertTrue(result.stream().anyMatch(b -> b.getFirstPublishYear() == 1997));
    }

    @Test
    void searchBooks_whenBookYearIsOneBeforePublishedAfter_bookIsExcluded() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        // publishedAfter = 1998, libro con año 1997 debe excluirse
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", null, null, 1998);
        List<BookDTO> result = bookService.searchBooks(request);
        assertFalse(result.stream().anyMatch(b -> b.getFirstPublishYear() < 1998));
    }

    // ===================== MUTANTES SOBREVIVIDOS - HISTORIAL =====================

    @Test
    void searchBooks_savesCorrectTitleInHistory() {
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(mockBooks);
        when(searchHistoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        SearchRequestDTO request = new SearchRequestDTO("Harry Potter", "Rowling", "eng", 1990);
        bookService.searchBooks(request);
        verify(searchHistoryRepository).save(argThat(h ->
                "Harry Potter".equals(h.getTitle()) &&
                        "Rowling".equals(h.getAuthor()) &&
                        "eng".equals(h.getLanguage()) &&
                        Integer.valueOf(1990).equals(h.getPublishedAfter())
        ));
    }

    // ===================== MUTANTES SOBREVIVIDOS - FAVORITOS =====================

    @Test
    void saveFavorite_setsAllFieldsCorrectly() {
        when(favoriteBookRepository.existsByBookKey(anyString())).thenReturn(false);
        when(favoriteBookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        FavoriteBook result = bookService.saveFavorite(
                "/works/99", "Clean Code", "Robert Martin", "http://cover99.jpg"
        );
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert Martin", result.getAuthor());
        assertEquals("http://cover99.jpg", result.getCoverUrl());
    }

    // ===================== MUTANTES SOBREVIVIDOS - FILTRO NULL =====================

    @Test
    void searchBooks_whenBookFirstPublishYearIsNull_bookIsExcluded() {
        List<BookDTO> booksWithNull = new ArrayList<>();
        booksWithNull.add(new BookDTO("/works/1", "Book A", "Author A", null, 10, "http://a.jpg"));
        booksWithNull.add(new BookDTO("/works/2", "Book B", "Author B", 2000, 8, "http://b.jpg"));
        booksWithNull.add(new BookDTO("/works/3", "Book C", "Author C", 2001, 6, "http://c.jpg"));
        booksWithNull.add(new BookDTO("/works/4", "Book D", "Author D", 2002, 4, "http://d.jpg"));
        when(openLibraryClient.searchBooks(anyString(), any())).thenReturn(booksWithNull);
        when(searchHistoryRepository.save(any())).thenReturn(new SearchHistory());
        SearchRequestDTO request = new SearchRequestDTO("Book", null, null, 1999);
        List<BookDTO> result = bookService.searchBooks(request);
        assertTrue(result.stream().noneMatch(b -> b.getFirstPublishYear() == null));
    }
}

