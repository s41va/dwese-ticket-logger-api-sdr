package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String message;
}
