package com.library.strategy;

import com.library.model.Book;
import java.util.List;

public interface BookSearchStrategy {
    List<Book> search(List<Book> books, String query);
    String getSearchType();
}
