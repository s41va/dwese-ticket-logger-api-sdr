package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {


    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    @Override
    Optional<Province> findById(Long id);
    @Query("SELECT p FROM Province p LEFT JOIN FETCH p.region WHERE p.id = :id")
    Optional<Province> findByIdWithRegion(@Param("id")Long id);



}
