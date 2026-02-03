package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.InvalidFileException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UserProfileRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.UserProfile;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.UserProfileMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.FileStorageService;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.attribute.standard.PrinterInfo;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private static final long MAX_PROFILE_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/edit")
    public String showProfileForm(Model model, Locale locale, Principal principal) {
        String email = principal.getName();
        logger.info("Mostrando formulario de perfil para el usuario fijo {}", email);

        try{
            UserProfileFormDTO formDTO = userProfileService.getFormByEmail(email);
            model.addAttribute("userProfileForm", formDTO);
            return "views/user-profile/user-profile-form";
        } catch (ResourceNotFoundException e) {
            logger.warn("No se encontró el usuario para cargar el perfil: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound", null, locale);
            return "views/user-profile/user-profile-form";
        }catch (Exception e){
            logger.error("Error inesperado cargando el formulario de perfil: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.userProfile.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";

        }
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("userProfileForm") UserProfileFormDTO profileDto,
            BindingResult result,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Principal principal) {

        String email = principal.getName();

        logger.info("Actualizando perfil para el usuario con ID {}", email);

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de perfil para email={}", email);
            return "views/user-profile/user-profile-form";
        }

        try {
            userProfileService.updateProfile(email, profileDto, profileImageFile);
            String successMessage = messageSource.getMessage("msg.userProfile.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        }catch (ResourceNotFoundException ex){
            logger.warn("No se pudo actualizar el perfile porque falta el recurso: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound",null, locale );
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        }catch (InvalidFileException ex){
            logger.warn("Imagen de perfil invalida: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage("msg.userProfile.image.invalid", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        }catch (Exception e) {
            logger.error("Error inesperado actualizando el perfil: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.userProfile.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/profile/edit";
    }

}
