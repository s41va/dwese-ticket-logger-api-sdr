package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;


import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.ProvinceService;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private MessageSource messageSource;

    // =========================
    // GET /provinces
    // =========================
    /*@GetMapping
    public ResponseEntity<Page<ProvinceDTO>> listProvinces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando provincias (REST) page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<ProvinceDTO> page = provinceService.list(pageable);

        logger.info("Se han cargado {} provincias en la pagina {}",
                page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }
    */
    @GetMapping
    public ResponseEntity<?> listProvinces(
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            @RequestParam(defaultValue = "false") boolean unpaged) {

        if (unpaged) {
            return ResponseEntity.ok(provinceService.listAll(Sort.by("name").ascending()));
        }

        return ResponseEntity.ok(provinceService.list(pageable));
    }


    // =========================
    // POST /provinces/create
    // =========================
    @PostMapping
    public ResponseEntity<ProvinceDTO> createProvince(@Valid @RequestBody ProvinceCreateDTO dto) {
        ProvinceDTO created = provinceService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // =========================
    // PUT /provinces/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ProvinceDTO> updateRegion (@PathVariable Long id, @Valid @RequestBody ProvinceUpdateDTO dto) {
        logger.info("Actualizando provincia con ID {} (REST)", id);

        dto.setId(id);

        ProvinceDTO updated = provinceService.update(dto);

        logger.info("Porvincia con ID {} actualizada con exito", id);

        return ResponseEntity.ok(updated);
    }

    // =========================
    // POST /provinces/delete
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProvince(
            @PathVariable Long id) {

        logger.info("Eliminando provincia (REST) con un ID {}", id);

        regionService.delete(id);

        logger.info("Provincia con ID {} eliminada con exito", id);

        return ResponseEntity.noContent().build();
    }

    // =========================
    // GET /provinces/detail
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDetailDTO> getRegionById(@PathVariable Long id) {
        logger.info("Mostrando detalle (REST) de la region con ID {}", id);

        ProvinceDetailDTO provinceDTO = provinceService.getDetail(id);

        return ResponseEntity.ok(provinceDTO);
    }
}