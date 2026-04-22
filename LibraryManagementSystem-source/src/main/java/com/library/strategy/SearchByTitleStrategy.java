package com.library.strategy;

import com.library.model.Book;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByTitleStrategy implements BookSearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        String lq = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() { return "Title"; }
}
