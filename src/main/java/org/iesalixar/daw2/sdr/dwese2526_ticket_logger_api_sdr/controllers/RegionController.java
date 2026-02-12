package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.http.HttpStatus;
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



    /**
     * Obtiene todas las regiones almacenadas en la base de datos.
     * * @return Lista de regiones.
     */
    @Operation(summary = "Obtener todas las regiones", description = "Devuelve una lista de todas las regiones " +
            "disponibles en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de regiones recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegionDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        logger.info("Solicitando la lista de todas las regiones...");
        try {
            List<RegionDTO> regions = regionService.getAllRegions();
            logger.info("Se han encontrado {} regiones.", regions.size());
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/all")
//    public ResponseEntity<?> listAllRegions(
//            @PageableDefault(size = 10, sort = "name") Pageable pageable,
//            @RequestParam(defaultValue = "false") boolean unpaged) {
//
//
//        if (unpaged) {
//            return ResponseEntity.ok(regionService.listAll(Sort.by("name").ascending()));
//        }
//
//
//        return ResponseEntity.ok(regionService.list(pageable));
//    }


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



    @Operation(summary = "Obtener una región por ID", description = "Recupera una región " +
            "específica según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegionById(@PathVariable Long id) {
        logger.info("Buscando región con ID {}", id);
        try {
            Optional<RegionDTO> regionDTO = Optional.ofNullable(regionService.getRegionById(id));

            if (regionDTO.isPresent()) {
                logger.info("Región con ID {} encontrada.", id);
                return ResponseEntity.ok(regionDTO.get());
            } else {
                logger.warn("No se encontró ninguna región con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La región no existe.");
            }
        } catch (Exception e) {
            logger.error("Error al buscar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar la región.");
        }
    }

    /**
     * Crea una nueva región.
     * * @param regionCreateDTO Datos para crear la región.
     * @param locale Idioma para los mensajes de error.
     * @return Región creada o mensaje de error.
     */
    @Operation(summary = "Crear una nueva región", description = "Permite registrar una nueva región en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Región creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createRegion(@Valid @RequestBody RegionCreateDTO regionCreateDTO,
                                          Locale locale) {
        logger.info("Creando una nueva región con código {}", regionCreateDTO.getCode());
        try {
            RegionDTO createdRegion = regionService.createRegion(regionCreateDTO, locale);
            logger.info("Región creada exitosamente con ID {}", createdRegion.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRegion);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear la región: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al crear la región: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la región.");
        }
    }
    /**
     * Actualiza una región existente.
     * * @param id ID de la región a actualizar.
     * @param regionCreateDTO Datos de actualización.
     * @param locale Idioma para los mensajes de error.
     * @return Región actualizada o mensaje de error.
     */
    @Operation(summary = "Actualizar una región", description = "Permite actualizar los datos de una región existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegion(
            @PathVariable Long id,
            @Valid @RequestBody RegionCreateDTO regionCreateDTO,
            Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        try {
            RegionDTO updatedRegion = regionService.updateRegion(id, regionCreateDTO, locale);
            logger.info("Región con ID {} actualizada exitosamente.", id);
            return ResponseEntity.ok(updatedRegion);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la región.");
        }
    }



    /**
     * Elimina una región por su ID.
     *
     * @param id ID de la región a eliminar.
     * @return Resultado de la operación.
     */
    @Operation(summary = "Eliminar una región", description = "Permite eliminar una región específica de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con ID {}", id);
        try {
            regionService.deleteRegion(id);
            logger.info("Región con ID {} eliminada exitosamente.", id);
            return ResponseEntity.ok("Región eliminada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la región.");
        }
    }

}





























