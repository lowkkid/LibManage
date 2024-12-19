package com.libmanage.repository;

import com.libmanage.model.Reservation;
import com.libmanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser(User user);
}

