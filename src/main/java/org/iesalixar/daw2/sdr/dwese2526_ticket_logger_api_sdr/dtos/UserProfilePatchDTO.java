package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfilePatchDTO {

    @Size(max = 100, message = "{msg.userProfile.firstName.size}")
    private String firstName;

    @Size(max = 100, message = "{msg.userProfile.lastName.size}")
    private String lastName;

    @Size(max = 100, message = "{msg.userProfile.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 500, message = "{msg.userProfile.bio.size}")
    private String bio;

    @Size(max = 10, message = "{msg.userProfile.locale.size}")
    private String locale;

}
