package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.libmanage.model.Reservation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class ReservationDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("bookTitle")
    private String bookTitle;

    @JsonProperty("reservationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @JsonProperty("status")
    private String status;

    public ReservationDTO() {
    }

    public static ReservationDTO fromEntity(Reservation reservation) {
        return new ReservationDTO(
                reservation.id(),
                reservation.getBook().getTitle(),
                reservation.getReservationDate(),
                reservation.getStatus().name()
        );
    }

    public ReservationDTO(Integer id, String bookTitle, LocalDate reservationDate, String status) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}