package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers;



import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.ProvinceDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;

import java.util.List;

public class ProvinceMapper {



    // Entity -> DTO (Listado/tabla básico)
//

    /**
     * Convierte una entidad de {@link Province} a {@link ProvinceDTO}.
     */
    public static ProvinceDTO toDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        // Establecemos solo el nombre de la región que es lo único que mostramos en los listados.
        dto.setRegionName(entity.getRegion().getName());
        return dto;
    }

    /**
     * Convierte una lista de {@link Province} a una lista de {@link ProvinceDTO}.
     */
    public static List<ProvinceDTO> toDTOList(List<Province> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ProvinceMapper::toDTO).toList();
    }

    // Entity -> DTO (detalle con región)
//

    /**
     * Convierte una {@link Province} a {@link ProvinceDetailDTO}, incluyendo la región asociada.
     */
    public static ProvinceDetailDTO toDetailDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDetailDTO dto = new ProvinceDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegion(RegionMapper.toDTO(entity.getRegion()));
        return dto;
    }

    /**
     * Convierte una {@link Province} a {@link ProvinceUpdateDTO}, para precargar el formulario de edición.
     */
    public static ProvinceUpdateDTO toUpdateDTO(Province entity) {
        if (entity == null) return null;
        ProvinceUpdateDTO dto = new ProvinceUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegionId(entity.getRegion() != null ? entity.getRegion().getId() : null);
        return dto;
    }

    /**
     * Crea una nueva entidad de {@link Province} desde un {@link ProvinceCreateDTO}.
     * (Id se deja null para autogenerarse)
     */
    public static Province toEntity(ProvinceCreateDTO dto) {
        if (dto == null) return null;
        Province e = new Province();
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        //Creamos una región vacía y le pasamos solo el ID,
        //ya que para la inserción de una provincia solo necesitamos el ID de la región
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }

    /**
     * Crea una entidad {@link Province} desde una {@link ProvinceUpdateDTO}.
     * * Útil si trabajas con update por reemplazo.
     * * Si prefieres evitar perder relaciones/estado, usa {@link #copyToExistingEntity(ProvinceUpdateDTO, Province)}.
     */
    public static Province toEntity(ProvinceUpdateDTO dto) {
        if (dto == null) return null;
        Province e = new Province();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        //Creamos una region vacía y le pasamos solo el ID,
        //ya que para la actualización de la provincia solo necesitamos el ID de región
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }
    public static void copyToExistingEntity(ProvinceUpdateDTO dto, Province entity) {
        if (dto == null || entity == null) return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        //Creamos una region vacía y le pasamos solo el ID,
        //ya que para la actualización de la provincia solo necesitamos el ID de región
        Region region = new Region();
        region.setId(dto.getRegionId());
        entity.setRegion(region);
        // No tocar entity.setId(...)
    }


}
