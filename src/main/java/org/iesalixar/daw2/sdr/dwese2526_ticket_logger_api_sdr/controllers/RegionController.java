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

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Controller
@RequestMapping("/regions")
public class RegionController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionService regionService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listRegions(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)Pageable pageable,
                              Model model){
        logger.info("Solicitando la lista de todas las regiones... page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            // Page<RegionDTO> listRegionsDTOs = regionRepository.findAll(pageable).map(RegionMapper::toDTO);

            Page<RegionDTO> listRegionsDTOs = regionService.list(pageable);
            logger.info("Se han cargado {} regiones en la pagina {}",
                    listRegionsDTOs.getNumberOfElements(), listRegionsDTOs.getNumber());
            model.addAttribute("page", listRegionsDTOs);

            String sortParam = "name,asc";
            if (listRegionsDTOs.getSort().isSorted()) {
                Sort.Order order = listRegionsDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        }
        catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las regiones.");
        }
        return "views/region/region-list";
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




    @GetMapping("/new")
    public String showNewForm(Model model){
        logger.info("Mostando el nuevo formilario de regiones");
        model.addAttribute("region", new RegionCreateDTO());
        return "views/region/region-form";
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam ("id") Long id,Model model, RedirectAttributes redirectAttributes, Locale locale){
        logger.info("Entrando al metodo showEditForm");
        try{
            RegionUpdateDTO regionDTO = regionService.getForEdit(id);
            model.addAttribute("region", regionDTO);
            return "views/region/region-list";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la region con ID {}", id);
            String msg = messageSource.getMessage("msg.region.error.notfound", new Object[]{id}, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";

        }catch (Exception e){
            logger.error("Error al obtener la region con Id {} :{}", id ,e.getMessage());
            String msg = messageSource.getMessage("msg.region.error.load", null, locale);
            model.addAttribute("errorMessage", msg);
            return "redirect:/regions";
        }


    }



    @PostMapping("/delete")
    //@PreAuthorize("hasRole('ADMIN')")
    public String deleteRegion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale){
        logger.info("Entrando al metodo deleteRegion");

        try{
            regionService.delete(id);
            logger.info("Region con Id {} eliminada con exito" ,id);
            return "redirect:/regions";
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la region con id {}" ,id );
            String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/regions";
        }catch (Exception e){
            logger.error("Error al eliminar la region con ID {} : {}", id , e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la region");
            return "redirect:/regions";
        }

    }

}





























