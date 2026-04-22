package com.library.factory;

import com.library.model.Book;

public class BookFactory implements EntityFactory<Book> {
    @Override
    public Book create(Object... params) {
        // params: bookId, title, author, publisher, pubYear(int), isbn, category, shelfLocation
        if (params.length < 8) throw new IllegalArgumentException("BookFactory requires 8 parameters");
        return new Book(
                (String) params[0],
                (String) params[1],
                (String) params[2],
                (String) params[3],
                (int)    params[4],
                (String) params[5],
                (String) params[6],
                (String) params[7]
        );
    }
}
