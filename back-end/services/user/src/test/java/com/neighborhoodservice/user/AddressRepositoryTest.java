package com.neighborhoodservice.user;


import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.model.AddressType;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.AddressRepository;
import com.neighborhoodservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;



@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackageClasses = Address.class)
@ActiveProfiles("test")
@Transactional(rollbackOn = DataIntegrityViolationException.class)
public class AddressRepositoryTest {



    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;


    static Address staticAddress;


    @Test
    void connectionEstablished() {
        assertTrue(postgresContainer.isCreated());
        assertTrue(postgresContainer.isRunning());
    }

    @BeforeEach
    public void setUp() {

        User user = User.builder()
                .userId(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();


        userRepository.save(user);


        staticAddress = Address.builder()
                .user(user)
                .address("Levka Lukianenka St, 29")
                .city("Kyiv")
                .postalCode("04205")
                .latitude(50.4501)
                .longitude(30.5234)
                .addressType(AddressType.HOME)
                .isDefault(true)
                .build();
    }

    @BeforeEach
    void setUpEachTest() {
        addressRepository.deleteAll();  // Clear the repository before each test
    }

    @Test
    public void createAddressSuccess() {

        addressRepository.save(staticAddress);

        Address address = addressRepository.findById(staticAddress.getAddressId()).get();

        assertEquals(staticAddress, address);

    }


    @Test
    public void deleteAddressSuccess() {

        addressRepository.save(staticAddress);

        addressRepository.deleteById(staticAddress.getAddressId());

        assertFalse(addressRepository.findById(staticAddress.getAddressId()).isPresent());

    }

    @Test
    public void updateAddressSuccess() {

        addressRepository.save(staticAddress);

        Address address = addressRepository.findById(staticAddress.getAddressId()).get();

        address.setAddress("Levka Lukianenka St, 28");

        addressRepository.save(address);

        Address updatedAddress = addressRepository.findById(staticAddress.getAddressId()).get();

        assertEquals(address, updatedAddress);

    }

    @Test
    public void createAddress_Failure_NullAddress() {

        Address address = staticAddress;
        address.setAddress(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.saveAndFlush(address); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void createAddress_Failure_NullCity() {

        Address address = staticAddress;
        address.setCity(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.saveAndFlush(address); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void createAddress_Failure_NullPostalCode() {

        Address address = staticAddress;
        address.setPostalCode(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.saveAndFlush(address); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void createAddress_Failure_InvalidPostalCode() {

        Address address = staticAddress;
        address.setPostalCode("321433");

        assertThrows(ConstraintViolationException.class, () -> {
            addressRepository.saveAndFlush(address); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void createAddress_Failure_NullAddressType() {

        Address address = staticAddress;
        address.setAddressType(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.saveAndFlush(address); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void addAddressSuccess() {

        addressRepository.save(staticAddress);

        Address secondAddress = staticAddress;
        secondAddress.setAddressType(AddressType.WORK);
        addressRepository.save(secondAddress);


        Address address = addressRepository.findById(secondAddress.getAddressId()).get();

        assertEquals(secondAddress, address);

    }








}
