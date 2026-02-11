package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfileDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfilePatchDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfileDTO getFormByEmail(String email);
    void updateProfile(String email, UserProfilePatchDTO patchDTO, MultipartFile profileImageFile);
}
