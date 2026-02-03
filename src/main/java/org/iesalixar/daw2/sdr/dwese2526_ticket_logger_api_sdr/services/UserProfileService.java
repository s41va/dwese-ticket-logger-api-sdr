package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfileFormDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfileFormDTO getFormByEmail(String email);
    void updateProfile(String email, UserProfileFormDTO profileDto, MultipartFile profileImageFile);
}
