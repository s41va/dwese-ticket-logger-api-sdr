package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDetailDTO {

    private Long id;
    private String code;
    private String name;
    private RegionDTO region;
}
