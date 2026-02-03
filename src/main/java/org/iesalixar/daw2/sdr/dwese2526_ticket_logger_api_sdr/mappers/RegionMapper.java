package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;

import java.util.List;

public class RegionMapper {

    /**
     *
     * Convierte una entidad {@link Region} a {@link RegionDTO}
     */
    public static RegionDTO toDTO(Region entity){
        if (entity==null)return null;
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    /**
     *
     * Convierte una lista de entidades {@link Region} a {@link RegionDTO}
     */
    public static List<RegionDTO> toDTOList(List<Region> entities){
        if (entities == null) return List.of();
        return entities.stream().map(RegionMapper::toDTO).toList();
    }

    //
// Entity -> DTO (detalle con provincias)
//
    /**
     * Convierte una {@link Region} a {@link RegionDetailDTO}, mapeando su lista de provincias.
     */
    public static RegionDetailDTO toDetailDTO(Region entity) {
        if (entity == null) return null;

        RegionDetailDTO dto = new RegionDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setProvinces(toProvinceList(entity.getProvinces()));
        return dto;
    }

    /**
     * Convierte una entidad {@link Province} a {@link ProvinceDTO}.
     */
    public static ProvinceDTO toProvinceDTO(Province p) {
        if (p == null) return null;
        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setCode(p.getCode()); // Nota: En la imagen se veía 'dto.setName(p.getName());' dos veces, asumo que uno debería ser setCode
        return dto;
    }


    /**
     * Convierte una lista de {@link Province} a {@link ProvinceDTO}.
     */
    public static List<ProvinceDTO> toProvinceList(List<Province> provinces) {
        if (provinces == null) return List.of();
        return provinces.stream().map(RegionMapper::toProvinceDTO).toList();
    }

    public static RegionUpdateDTO toUpdateDTO(Region entity) {
        if (entity == null) return null;
        RegionUpdateDTO dto = new RegionUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    public static Region toEntity(RegionCreateDTO dto){
        if (dto==null) return null;
        Region e = new Region();
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        return e;
    }

    public Region toEntity(RegionUpdateDTO dto){
        if (dto==null) return null;
        Region e = new Region();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        return e;
    }

    public static void copyToExistingEntity(RegionUpdateDTO dto, Region entity){
        if (dto == null || entity == null) return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
    }
}
