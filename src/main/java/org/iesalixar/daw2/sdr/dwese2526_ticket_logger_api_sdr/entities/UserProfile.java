package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column
    private Long id;


    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "locale", length = 10)
    private String locale;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updateAt;

//
//    @Override
//    public int hashCode() {
//        // Replace: return user.hashCode();  <-- This causes the recursion
//
//        // With: return id != null ? id.hashCode() : 0;
//
//        // OR, if using Lombok/IDE generated methods, exclude the User field:
//        return Objects.hash(firstName, lastName,  phoneNumber, profileImage, bio, locale, createdAt, updateAt); // Exclude the User object
//    }

    public UserProfile(User user,
                       String firstName,
                       String lastName,
                       String phoneNumber,
                       String profileImage,
                       String bio,
                       String locale) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.bio = bio;
        this.locale = locale;
    }
}
