package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionCreateDTO {

    private Long id;

    @NotBlank(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;


    @NotBlank(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;
}
