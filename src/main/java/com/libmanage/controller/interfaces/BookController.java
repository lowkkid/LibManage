package com.libmanage.controller.interfaces;

import com.libmanage.dto.BookDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/books")
public interface BookController {

    @GetMapping("/search")
    ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String isbn);


    @GetMapping("/available")
    ResponseEntity<List<BookDTO>> getAvailableBooks();

    @GetMapping("/{id}/availability")
    ResponseEntity<Integer> checkBookAvailability(@PathVariable Integer id);

    @GetMapping("/my-history")
    ResponseEntity<List<BookDTO>> getUserBookHistory();

    @GetMapping("/recommendations")
    ResponseEntity<List<BookDTO>> getBookRecommendations();

    @GetMapping("/popular")
    ResponseEntity<List<BookDTO>> getPopularBooks();
}
