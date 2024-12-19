package com.libmanage.service;

import com.libmanage.config.CustomAuthenticationToken;
import com.libmanage.dto.CreateReservationRequest;
import com.libmanage.dto.ReservationDTO;
import com.libmanage.exception.EntityNotFoundException;
import com.libmanage.model.Book;
import com.libmanage.model.Reservation;
import com.libmanage.model.ReservationStatus;
import com.libmanage.model.User;
import com.libmanage.repository.BookRepository;
import com.libmanage.repository.ReservationRepository;
import com.libmanage.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }


    public List<ReservationDTO> getUserReservations() {
        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return reservationRepository.findByUser(currentUser)
                .stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ReservationDTO createReservation(CreateReservationRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for book with id " + request.getBookId());
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.Активное);

        reservation = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(reservation);
    }

    public void cancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id " + reservationId));

        if (!ReservationStatus.Активное.equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot cancel reservation with status " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.Отменено);
        reservationRepository.save(reservation);

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        if (authentication instanceof CustomAuthenticationToken) {
            CustomAuthenticationToken customAuth = (CustomAuthenticationToken) authentication;
            return customAuth.getUserId();
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            Object principal = auth.getPrincipal();

            UserDetails userDetails = (UserDetails) principal;
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found"));
            return user.id();
        }

        throw new IllegalStateException("Unsupported authentication token type");
    }
}
