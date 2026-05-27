package com.smartbook.smartbookfinder.client;

import com.smartbook.smartbookfinder.dto.BookDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenLibraryClient {

    private static final String BASE_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/{id}-M.jpg";

    private final RestTemplate restTemplate;

    public OpenLibraryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BookDTO> searchBooks(String title, String author) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("fields", "key,title,author_name,first_publish_year,edition_count,cover_i")
                .queryParam("limit", 50);

        if (title != null && !title.isBlank()) {
            builder.queryParam("title", title);
        }
        if (author != null && !author.isBlank()) {
            builder.queryParam("author", author);
        }

        String url = builder.toUriString();
        Map response = restTemplate.getForObject(url, Map.class);

        List<BookDTO> books = new ArrayList<>();
        if (response == null || !response.containsKey("docs")) return books;

        List<Map> docs = (List<Map>) response.get("docs");
        for (Map doc : docs) {
            BookDTO book = new BookDTO();
            book.setKey(getString(doc, "key"));
            book.setTitle(getString(doc, "title"));
            book.setFirstPublishYear(getInteger(doc, "first_publish_year"));
            book.setEditionCount(getInteger(doc, "edition_count"));

            List<String> authors = (List<String>) doc.get("author_name");
            if (authors != null && !authors.isEmpty()) {
                book.setAuthor(authors.get(0));
            }

            Object coverId = doc.get("cover_i");
            if (coverId != null) {
                book.setCoverUrl(COVER_URL.replace("{id}", coverId.toString()));
            }

            books.add(book);
        }
        return books;
    }

    private String getString(Map doc, String key) {
        Object val = doc.get(key);
        return val != null ? val.toString() : null;
    }

    private Integer getInteger(Map doc, String key) {
        Object val = doc.get(key);
        if (val instanceof Integer) return (Integer) val;
        if (val != null) {
            try { return Integer.parseInt(val.toString()); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}