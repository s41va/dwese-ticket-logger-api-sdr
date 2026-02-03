package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id) ;
    @Override
    Optional<Region> findById(Long id);
    @Query("select r from Region r left join fetch r.provinces where r.id = :id")
    Optional<Region> findByIdWithProvinces(@Param("id") Long id);

}