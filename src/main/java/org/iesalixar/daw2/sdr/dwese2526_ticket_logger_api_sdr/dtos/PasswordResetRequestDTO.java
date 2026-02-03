package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    @NotBlank
    @Email
    private String email;
}
