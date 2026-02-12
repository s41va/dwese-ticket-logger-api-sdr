package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionUpdateDTO;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;

public interface RegionService {
    Page<RegionDTO> list(Pageable pageable);
    RegionUpdateDTO getForEdit(Long id);
    RegionDTO create(RegionCreateDTO dto);
    RegionDTO update(RegionUpdateDTO dto);
    void delete(Long id);
    RegionDetailDTO getDetail( Long id);



    List<RegionDTO> getAllRegions();
    RegionDTO getRegionById(Long id);
    RegionDTO createRegion(@Valid RegionCreateDTO regionCreateDTO, Locale locale);
    RegionDTO updateRegion(Long id, @Valid RegionCreateDTO regionCreateDTO, Locale locale);
    void deleteRegion(Long id);
    //RegionDTO listAll(Sort name);
    



}
