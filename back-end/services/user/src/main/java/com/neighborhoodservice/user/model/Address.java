package com.neighborhoodservice.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The address entity. Used for location of the job and calculation of the distance between the job and the user.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    /**
     * The unique identifier for the address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    /**
     * The user who owns the address.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Full address of the user. E.g. Levka Lukianenka St, 29, Kyiv, Ukraine, 04205
     */
    @Column(nullable = false, length = 255)
    private String address;

    /**
     * The city of the address.
     */
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * The postal code of the address.
     */
    @Column(nullable = false, length = 5)
    @Size(min = 5, max = 5, message = "Postal code must be exactly 5 digits")
    private String postalCode;

    /**
     * The latitude of the address.
     */
    @Column
    private Double latitude;

    /**
     * The longitude of the address.
     */
    @Column
    private Double longitude;

    /**
     * The type of the address. User can have 3 addresses at most(HOME, WORK, OTHER).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressType addressType;  // Treating ENUM as String

    /**
     * The default address boolean of the user. (To be used on front-end)
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDefault = false;

    public Address (User user, String address, String city, String postalCode, Double latitude, Double longitude, AddressType addressType, boolean isDefault) {
        this.user = user;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressType = addressType;
        this.isDefault = isDefault;
    }

}