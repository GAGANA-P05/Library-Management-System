package com.library.strategy;

import com.library.model.Book;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByISBNStrategy implements BookSearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(query.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() { return "ISBN"; }
}
