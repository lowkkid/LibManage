package com.libmanage.controller.implenetations;

import com.libmanage.controller.interfaces.BookController;
import com.libmanage.dto.BookDTO;
import com.libmanage.service.BookService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class BookControllerImpl implements BookController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BookControllerImpl.class);
    private final BookService bookService;

    public BookControllerImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<List<BookDTO>> searchBooks(String title, String author, String genre, String isbn) {
        List<BookDTO> books = bookService.searchBooks(title, author, genre, isbn);
        return ResponseEntity.ok(books);
    }

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    public ResponseEntity<List<BookDTO>> getAvailableBooks() {
        logger.info("GET Available Books");
        List<BookDTO> availableBooks = bookService.getAvailableBooks();
        for (BookDTO bookDTO : availableBooks) {
            logger.info(bookDTO.toString());
        }
        return ResponseEntity.ok(availableBooks);
    }

    @Override
    public ResponseEntity<Integer> checkBookAvailability(Integer id) {
        int availableCopies = bookService.checkBookAvailability(id);
        return ResponseEntity.ok(availableCopies);
    }

    @Override
    public ResponseEntity<List<BookDTO>> getUserBookHistory() {
        List<BookDTO> bookHistory = bookService.getUserBookHistory();
        return ResponseEntity.ok(bookHistory);
    }

    @Override
    public ResponseEntity<List<BookDTO>> getBookRecommendations() {
        List<BookDTO> recommendations = bookService.getBookRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @Override
    public ResponseEntity<List<BookDTO>> getPopularBooks() {
        List<BookDTO> popularBooks = bookService.getPopularBooks();
        return ResponseEntity.ok(popularBooks);
    }
}
