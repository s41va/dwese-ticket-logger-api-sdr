package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;



import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.ProvinceMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.RegionMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.ProvinceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.ProvinceRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.RegionRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceService provinceService;

    @GetMapping
    public String listProvinces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Model model, Locale locale) {

        logger.info("Listando provincias page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            Page<ProvinceDTO> page = provinceService.list(pageable);

            logger.info("Se han cargado {} provincias en la página {}.", page.getNumberOfElements(), page.getNumber());

            model.addAttribute("page", page);

            String sortParam = "name,asc";
            if (page.getSort().isSorted()) {
                Sort.Order order = page.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);

        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }

        return "views/province/province-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nueva provincia.");
        try {
            List<RegionDTO> listRegionsDTOs = provinceService.listRegionsForSelect();

            model.addAttribute("province", new ProvinceCreateDTO());
            model.addAttribute("listRegions", listRegionsDTOs);
        } catch (Exception e) {
            logger.error("Error al cargar formulario de nueva provincia: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }

    @PostMapping("/insert")
    public String insertProvince(@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Insertando nueva provincia con código {}", provinceDTO.getCode());

        if (result.hasErrors()) {
            List<RegionDTO> listRegionsDTOs = provinceService.listRegionsForSelect();
            model.addAttribute("listRegions", listRegionsDTOs);
            return "views/province/province-form";
        }

        try {
            provinceService.create(provinceDTO);
            logger.info("Provincia {} insertada con éxito.", provinceDTO.getCode());
            return "redirect:/provinces";

        } catch (DuplicateResourceException ex) {
            logger.warn("El código de la provincia {} ya existe.", provinceDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";

        } catch (Exception e) {
            logger.error("Error al insertar provincia {}: {}", provinceDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para provincia ID {}", id);
        try {
            ProvinceUpdateDTO provinceDTO = provinceService.getForEdit(id);
            List<RegionDTO> listRegionsDTOs = provinceService.listRegionsForSelect();

            model.addAttribute("province", provinceDTO);
            model.addAttribute("listRegions", listRegionsDTOs);
            return "views/province/province-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró provincia con ID {}", id);
            String msg = messageSource.getMessage("msg.province-controller.edit.notfound", new Object[]{id}, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error al obtener provincia ID {}: {}", id, e.getMessage());
            String msg = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }

    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Actualizando provincia ID {}", provinceDTO.getId());

        if (result.hasErrors()) {
            List<RegionDTO> listRegionsDTOs = provinceService.listRegionsForSelect();
            model.addAttribute("listRegions", listRegionsDTOs);
            return "views/province/province-form";
        }

        try {
            provinceService.update(provinceDTO);
            logger.info("Provincia ID {} actualizada con éxito.", provinceDTO.getId());
            return "redirect:/provinces";

        } catch (DuplicateResourceException ex) {
            logger.warn("El código {} ya existe.", provinceDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la provincia con ID {}", provinceDTO.getId());
            String notFound = messageSource.getMessage("msg.province-controller.detail.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error al actualizar provincia ID {}: {}", provinceDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();
        }
    }

    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {
        logger.info("Eliminando provincia ID {}", id);
        try {
            provinceService.delete(id);
            logger.info("Provincia ID {} eliminada.", id);
            return "redirect:/provinces";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró provincia con ID {}", id);
            String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error eliminando provincia ID {}: {}", id, e.getMessage());
            String msg = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            ProvinceDetailDTO provinceDTO = provinceService.getDetail(id);
            model.addAttribute("province", provinceDTO);
            return "views/province/province-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error detalle provincia {}: {}", id, e.getMessage());
            String msg = messageSource.getMessage("msg.province-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }
}