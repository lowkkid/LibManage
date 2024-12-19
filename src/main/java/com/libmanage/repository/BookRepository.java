package com.libmanage.repository;

import com.libmanage.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);

    @Query("""
            SELECT b FROM Book b
            JOIN b.author a
            JOIN b.genre g
            WHERE (:title IS NULL OR b.title LIKE %:title%)
            AND (:author IS NULL OR a.name LIKE %:author%)
            AND (:genre IS NULL OR g.name LIKE %:genre%)
            AND (:isbn IS NULL OR b.isbn = :isbn)""")
    List<Book> findBooksByCriteria(@Param("title") String title,
                                   @Param("author") String author,
                                   @Param("genre") String genre,
                                   @Param("isbn") String isbn);

    List<Book> findByAvailableCopiesGreaterThan(int count);

    @Query("""
            SELECT b FROM Book b
            JOIN b.genre g
            WHERE g.id IN :genreIds""")
    List<Book> findByGenreIds(@Param("genreIds") Set<Integer> genreIds);

    @Query("""
            SELECT b FROM Book b
            JOIN b.reservations r
            WHERE r.reservationDate > :startDate
            GROUP BY b.id
            ORDER BY COUNT(r.id) DESC""")
    List<Book> findPopularBooks(LocalDate startDate);
}