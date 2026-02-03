package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Region;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.Role;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.RegionMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.UsersMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.RoleRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private static final int PASSWORD_EXPIRY_DAYS = 90;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Page<UsersDTO> list(Pageable pageable) {
        return usersRepository.findAll(pageable).map(UsersMapper::toDTO);
    }

    @Override
    public UsersUpdateDTO getForEdit(Long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("user", "id", id));
        return UsersMapper.toUpdateDTO(user);
    }

    @Override
    public void create(UsersCreateDTO dto) {
        if (usersRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("user", "email", dto.getEmail());
        }
        LocalDateTime lastPasswordChange = dto.getLastPasswordChange();
        if (lastPasswordChange == null) {
            lastPasswordChange = LocalDateTime.now();
            dto.setLastPasswordChange(lastPasswordChange);
        }
        dto.setPasswordExpiresAt(lastPasswordChange.plusDays(PASSWORD_EXPIRY_DAYS));

        // 3. Recuperar roles
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
        User user = UsersMapper.toEntity(dto, roles);
        usersRepository.save(user);
    }

    @Override
    public void update(UsersUpdateDTO dto) {
        if (usersRepository.existsByEmailAndIdNot(dto.getEmail(), dto.getId())){
            throw new DuplicateResourceException("user", "email", dto.getEmail());
        }
        User user = usersRepository.findById(dto.getId())
                .orElseThrow(()-> new ResourceNotFoundException("user", "id", dto.getId()));

        // 3. Lógica de contraseñas
        LocalDateTime lastPasswordChange = dto.getLastPasswordChange();
        if (lastPasswordChange == null) {
            lastPasswordChange = LocalDateTime.now();
            dto.setLastPasswordChange(lastPasswordChange);
        }
        dto.setPasswordExpiresAt(lastPasswordChange.plusDays(PASSWORD_EXPIRY_DAYS));

        // 4. Recuperar roles
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
        UsersMapper.copyToExistingEntity(dto, user ,roles);
       // UsersMapper.toEntity(dto, roles);
        usersRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("user", "id", id);
        }
        usersRepository.deleteById(id);
    }

    @Override
    public UsersDetailDTO getDetail(Long id) {
        User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
        return UsersMapper.toDetailDTO(user);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}
