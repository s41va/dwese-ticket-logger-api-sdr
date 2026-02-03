package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceCreateDTO {

    //En este DTO este campo siempre vendrá null porque es una inserción
    private Long id;

    @NotBlank(message = "{msg.province.code.notEmpty}")
    @Size(max = 2, message = "{msg.province.code.size}")
    private String code;

    @NotBlank(message = "{msg.province.name.notEmpty}")
    @Size(max = 100, message = "{msg.province.name.size}")
    private String name;

    @NotNull(message = "{msg.province.regionId.notNull}")
    private Long regionId;
    
}
