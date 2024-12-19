package com.libmanage.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {
    private Integer bookId;

    public Integer getBookId() {
        return bookId;
    }
}
