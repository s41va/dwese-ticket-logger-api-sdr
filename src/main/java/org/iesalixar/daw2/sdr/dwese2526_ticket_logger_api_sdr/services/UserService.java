package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UsersDTO> list(Pageable pageable);
    UsersUpdateDTO getForEdit(Long id);
    void create(UsersCreateDTO dto);
    void update(UsersUpdateDTO dto);
    void delete(Long id);
    UsersDetailDTO getDetail(Long id);
    List<Role> findAllRoles();
}
