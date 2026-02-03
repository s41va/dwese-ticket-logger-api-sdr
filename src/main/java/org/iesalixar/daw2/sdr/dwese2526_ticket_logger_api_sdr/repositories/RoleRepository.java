package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Busca un rol por su nombre.
     *
     * @param name el nombre del rol a buscar.
     * @return un Optional que contiene el rol si se encuentra, o vacío si no existe.
     */
    Optional<Role> findByName(String name);
}
