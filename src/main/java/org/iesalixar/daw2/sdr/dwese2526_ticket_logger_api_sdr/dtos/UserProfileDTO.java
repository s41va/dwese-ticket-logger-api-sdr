package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long userId;

    private String email;

    @NotBlank(message = "{msg.userProfile.firstName.notblank}")
    @Size(max = 100, message = "{msg.userProfile.firstName.size}")
    private String firstName;


    @NotBlank(message = "{msg.userProfile.lastName.notblank}")
    @Size(max = 100, message = "{msg.userProfile.lastName.size}")
    private String lastName;

    @Size(max = 30, message = "{msg.userProfile.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 255, message = "{msg.userProfile.profileImage.size}")
    private String profileImage;

    @Size(max = 500, message = "{msg.userProfile.bio.size}")
    private String bio;

    @Size(max = 10, message = "{msg.userProfile.locale.size}")
    private String locale;

}
