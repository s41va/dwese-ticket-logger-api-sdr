package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.RoleRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UsersCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UsersDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UsersDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UsersUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Role;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.UsersMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 💻 Controlador Spring MVC para la gestión de usuarios (CRUD).
 * <p>
 * Migra la funcionalidad del UserServlet a un entorno Spring, utilizando
 * inyección de dependencias para el UsersDAO y manejando las solicitudes
 * mediante anotaciones.
 * </p>
 *
 * @author Salvador Díaz Román (Adaptado a Spring Controller)
 * @version 2.0
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lista los usuarios con paginación y ordenación usando Pageable estándar.
     */
    @GetMapping
    public String listUsers(
            @PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        logger.info("Listando usuarios page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            Page<UsersDTO> page = userService.list(pageable);

            logger.info("Se han cargado {} usuarios en la página {}.", page.getNumberOfElements(), page.getNumber());

            model.addAttribute("page", page);

            String sortParam = "email,asc";
            if (page.getSort().isSorted()) {
                Sort.Order order = page.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);

        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }

        return "views/users/user-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo usuario.");
        model.addAttribute("user", new UsersCreateDTO());
        model.addAttribute("allRoles", userService.findAllRoles());
        return "views/users/user-form";
    }

    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") UsersCreateDTO userDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        logger.info("Insertando nuevo usuario email={}", userDTO.getEmail());

        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "views/users/user-form";
        }

        try {
            userService.create(userDTO);
            logger.info("Usuario {} insertado con éxito.", userDTO.getEmail());
            return "redirect:/users";

        } catch (DuplicateResourceException ex) {
            logger.warn("El email {} ya existe.", userDTO.getEmail());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/new";

        } catch (Exception e) {
            logger.error("Error al insertar usuario {}: {}", userDTO.getEmail(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/new";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición ID {}", id);
        try {
            UsersUpdateDTO userDTO = userService.getForEdit(id);
            model.addAttribute("user", userDTO);
            model.addAttribute("allRoles", userService.findAllRoles());
            return "views/users/user-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró usuario ID {}", id);
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al obtener usuario ID {}: {}", id, e.getMessage());
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UsersUpdateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             Locale locale) {

        logger.info("Actualizando usuario ID {}", userDTO.getId());

        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "views/users/user-form";
        }

        try {
            userService.update(userDTO);
            logger.info("Usuario ID {} actualizado.", userDTO.getId());
            return "redirect:/users";

        } catch (DuplicateResourceException ex) {
            String errorMessage = messageSource.getMessage("msg.user-controller.update.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/edit?id=" + userDTO.getId();

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al actualizar usuario ID {}: {}", userDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users/edit?id=" + userDTO.getId();
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Eliminando usuario ID {}", id);
        try {
            userService.delete(id);
            logger.info("Usuario eliminado.");
            return "redirect:/users";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error al eliminar usuario ID {}: {}", id, e.getMessage());
            String msg = messageSource.getMessage("msg.user-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            UsersDetailDTO userDetailDTO = userService.getDetail(id);
            model.addAttribute("user", userDetailDTO);
            return "views/user/user-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";

        } catch (Exception e) {
            logger.error("Error detalle usuario {}: {}", id, e.getMessage());
            return "redirect:/users";
        }
    }
}