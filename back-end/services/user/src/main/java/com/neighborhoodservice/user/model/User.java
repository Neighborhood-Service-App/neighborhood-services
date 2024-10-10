package com.neighborhoodservice.user.model;


import com.neighborhoodservice.user.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The user entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long userId;

    /**
     * The first name of the user.
     */
    @Column(nullable = false, updatable = false, length = 100)
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column(nullable = false, updatable = false, length = 100)
    private String lastName;

    /**
     * The email address of the user.
     */
    @Column(nullable = false, unique = true, length = 255)
    @Email
    private String email;

    /**
     * The phone number of the user.
     */
    @Column(nullable = false, length = 13)
    @ValidPhoneNumber
    private String phoneNumber;

    /**
     * The password of the user.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * The timestamp when the user was created. It is set automatically when a new user is created.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user was last updated. It is set automatically when a user is updated.
     */
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    /**
     * The URL of the user's profile image. (AWS S3 URL)
     */
    @Column(length = 255)
    private String imgUrl;

    /**
     * The list of addresses associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }


}


