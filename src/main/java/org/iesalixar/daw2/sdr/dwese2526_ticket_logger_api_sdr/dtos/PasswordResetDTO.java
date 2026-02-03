package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDTO {
    @NotBlank
    private String token;


    @NotBlank
    @Size(min = 8, max = 72) // 72 es típico para bcrypt
    private String newPassword;


    @NotBlank
    private String confirmPassword;

}
