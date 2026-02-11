package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfilePatchDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.InvalidFileException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UserProfileRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfileDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.FileStorageService;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/api/profile")
@Validated
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private static final long MAX_PROFILE_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("userProfileForm")UserProfilePatchDTO patchDTO,
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
            userProfileService.updateProfile(email, patchDTO, profileImageFile);
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

    @GetMapping
    public ResponseEntity<UserProfileDTO> getMyProfile(Principal principal){
        String email = principal.getName();

        logger.info("API getMyProfile para {}", email);

        UserProfileDTO dto = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(dto);
    }
    /**
     * Actualiza parcialmente el perfil del usuario autenticado (PATCH).
     * * <p>Consume <b>multipart/form-data</b> con:</p>
     * <ul>
     * <li><b>profile</b>: JSON con los campos a modificar (solo se actualizan los presentes).</li>
     * <li><b>profileImageFile</b> (opcional): nueva imagen de perfil.</li>
     * </ul>
     * * <p>El usuario se identifica a partir del {@link Principal}.</p>
     * * @param patchDto datos parciales del perfil a aplicar
     * @param profileImageFile imagen de perfil opcional
     * @param principal usuario autenticado
     * @return perfil actualizado
     */
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDTO> patchMyProfile(
            @ModelAttribute UserProfilePatchDTO patchDto,
            @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            Principal principal
    ) {
        String email = principal.getName();
        logger.info("API patchMyProfile para {}", email);

        userProfileService.updateProfile(email, patchDto, profileImageFile);

        UserProfileDTO updated = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(updated);
    }

}
