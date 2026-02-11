package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.RegionMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public Page<RegionDTO> list(Pageable pageable) {
        return regionRepository.findAll(pageable).map(RegionMapper::toDTO);
    }

    @Override
    public RegionUpdateDTO getForEdit(Long id) {
        Region region = regionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("region", "id", id));
        return RegionMapper.toUpdateDTO(region) ;
    }

    @Override
    public RegionDTO create(RegionCreateDTO dto) {
        if (regionRepository.existsByCode(dto.getCode())){
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }
        Region region = RegionMapper.toEntity(dto);
        region = regionRepository.save(region);
        return RegionMapper.toDTO(region);

    }

    @Override
    public RegionDTO update(RegionUpdateDTO dto) {

        if (regionRepository.existsByCodeAndIdNot(dto.getCode(), dto.getId())){
            throw new DuplicateResourceException("region", "code", dto.getId());

        }
        Region region = regionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("region" ,"id", dto.getId()));

        RegionMapper.copyToExistingEntity(dto, region);
        region = regionRepository.save(region);
        return RegionMapper.toDTO(region);

    }

    @Override
    public void delete(Long id) {
        if (!regionRepository.existsById(id)){
            throw new ResourceNotFoundException("region", "id", id);

        }
        regionRepository.deleteById(id);
    }

    @Override
    public RegionDetailDTO getDetail(Long id) {
        Region region = regionRepository.findByIdWithProvinces(id)
                .orElseThrow(() -> new ResourceNotFoundException("region", "id", id));
        return RegionMapper.toDetailDTO(region);
    }



}
