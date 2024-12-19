package com.libmanage.service;

import com.libmanage.config.CustomAuthenticationToken;
import com.libmanage.dto.BookDTO;
import com.libmanage.exception.EntityNotFoundException;
import com.libmanage.model.Reservation;
import com.libmanage.model.ReservationStatus;
import com.libmanage.model.User;
import com.libmanage.repository.AuthorRepository;
import com.libmanage.repository.BookRepository;
import com.libmanage.repository.GenreRepository;
import com.libmanage.repository.ReservationRepository;
import com.libmanage.repository.UserRepository;
import com.libmanage.model.Book;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, GenreRepository genreRepository, ReservationRepository reservationRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public List<BookDTO> searchBooks(String title, String author, String genre, String isbn) {
        return bookRepository.findBooksByCriteria(title, author, genre, isbn)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public int checkBookAvailability(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getAvailableCopies)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + bookId));
    }

    // История книг пользователя
    public List<BookDTO> getUserBookHistory() {
        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return reservationRepository.findByUser(currentUser).stream()
                .filter(reservation -> ReservationStatus.Отменено.equals(reservation.getStatus()))
                .map(reservation -> BookDTO.fromEntity(reservation.getBook()))
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBookRecommendations() {
        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<Reservation> reservations = reservationRepository.findByUser(currentUser);

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> genreIds = reservations.stream()
                .map(reservation -> reservation.getBook().getGenre().id())
                .collect(Collectors.toSet());

        return bookRepository.findByGenreIds(genreIds).stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getPopularBooks() {
        return bookRepository.findPopularBooks(LocalDate.now().minusDays(30)).stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    Logger logger = Logger.getLogger(BookService.class.getName());

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication class: " + auth.getClass().getName());

//        CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || auth.getUserId() == null) {
//            throw new IllegalStateException("No authenticated user found");
//        }
//        return auth.getUserId();
        return 1;
    }
}