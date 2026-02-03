package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

}
