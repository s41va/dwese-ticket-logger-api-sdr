package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.UserProfile;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.InvalidFileException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.mappers.UserProfileMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UserProfileRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService{

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private static final long MAX_IMAGE_SIZE_BYTES= 2 * 1024 * 1024;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;


    @Override
    public UserProfileFormDTO getFormByEmail(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("user", "email", email));
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(user.getId());
        UserProfile profile = profileOpt.orElse(null);
        return UserProfileMapper.toFormDto(user, profile);
    }

    @Override
    public void updateProfile(String email, UserProfileFormDTO profileDto, MultipartFile profileImageFile) {
        Long userId = profileDto.getUserId();
        logger.info("Actualizando perfil para email={}", email);

        User user = usersRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("user", "email", email));

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        boolean isNew = (profile == null);

        if (profileImageFile != null && !profileImageFile.isEmpty()){

            validateProfileImage(profileImageFile);

            String oldImagePath = profileDto.getProfileImage();

            String newImageWebPath = fileStorageService.saveFile(profileImageFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()){
                throw new InvalidFileException(
                        "userProfile",
                        "profileImageFile",
                        profileImageFile.getOriginalFilename(),
                        "No se pudo guardar la imagen de perfil."
                );
            }
            profileDto.setProfileImage(newImageWebPath);

            if (oldImagePath != null || !oldImagePath.isBlank()){
                fileStorageService.deleteFile(oldImagePath);
            }
        }

        if (isNew){
            profile = UserProfileMapper.toNewEntity(profileDto, user);
        }else{
            UserProfileMapper.copyToExistingEntity(profileDto, profile);
        }
        userProfileRepository.save(profile);
    }
    private void validateProfileImage(MultipartFile file){
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")){
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    contentType,
                    "Tipo de archivo no permitido"
            );
        }
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES){
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    file.getSize(),
                    "Archivo demasiado grande (maximo " + MAX_IMAGE_SIZE_BYTES + " bytes)"
            );
        }
    }
}
