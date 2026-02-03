package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.RegionMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.RegionService;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.RegionRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionService regionService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public ResponseEntity<Page<RegionDTO>> listRegions (
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)Pageable pageable){
            
        logger.info("Listaqndo regiones (REST) page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        Page<RegionDTO> page = regionService.list(pageable);
        logger.info("Se han cargado {} regiones en la página {}.",
                page.getNumberOfElements(), page.getNumber());
        
        return ResponseEntity.ok(page);
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id")Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale){
        logger.info("Mostrando detalle de la region con ID: {}", id);
        try{
            RegionDetailDTO regionDTO = regionService.getDetail(id);
            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";
        }catch (ResourceNotFoundException ex){
            String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        } catch(Exception e){
            logger.error("Error al obtener el detalle de la region {} : {}", id, e.getMessage(),e);
            String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }

    /**
     * Inserta una nueva región en la base de datos.
     * @param regionDTO que contiene los datos del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/insert")
    public String insertRegion(@Valid @ModelAttribute("region") RegionCreateDTO regionDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva región con código {}", regionDTO.getCode());
        try {
            if (result.hasErrors()) {
                return "region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            regionService.create(regionDTO);
            logger.info("Region {} insertada con exito", regionDTO.getCode());
            return "redirect:/regions";

        }catch (DuplicateResourceException ex){
            logger.warn("El codigo de la region {} ya existe", regionDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/new";

        }catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", regionDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/new";
        }
         // Redirigir a la lista de regiones
    }


    /**
     * Actualiza una región existente en la base de datos.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/update")
    public String updateRegion(@Valid @ModelAttribute("region") RegionUpdateDTO regionDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando región con ID {}", regionDTO.getId());
        try {
            if (result.hasErrors()) {
                return "region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            regionService.update(regionDTO);
            logger.info("Región con ID {} actualizada con éxito.", regionDTO.getId());
            return "redirect:/regions";
        } catch (DuplicateResourceException ex) {
            logger.warn("El codigo de la region {} ya existe para otra region.", regionDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/edit?id=" + regionDTO.getId();
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la region con ID {}", regionDTO.getId());
            String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null ,locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/regions";
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID {}: {}", regionDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/edit?id=" + regionDTO.getId(); // Redirigir a la lista de regiones
        }

    }



    @GetMapping("/{id}")
    public ResponseEntity<RegionDetailDTO> getRegionById(@PathVariable Long id){
        logger. info("Mostrando detalle (REST) de la region con id{}: ", id);

        RegionDetailDTO regionDetailDTO = regionService.getDetail(id);

        return ResponseEntity.ok(regionDetailDTO);
    }


    @PostMapping
    public ResponseEntity<RegionDTO> createRegion(@Valid @RequestBody RegionCreateDTO dto){
        RegionDTO created = regionService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}")
    public ResponseEntity<RegionDTO> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionUpdateDTO dto){

        logger.info("Actualizando region con ID {} (REST) ", id);

        dto.setId(id);

        RegionDTO updated = regionService.update(dto);

        logger.info("Region con Id {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }



    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id){
        logger.info("Entrando al metodo deleteRegion");

        regionService.delete(id);

        logger.info("Region con Id {} eliminada con éxito.", id);

        return ResponseEntity.noContent().build();
    }

}





























