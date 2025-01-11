package com.neighborhoodservice.unit.user.repository;


import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
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
@EntityScan(basePackageClasses = User.class)
@ActiveProfiles("test")
@Transactional(rollbackOn = DataIntegrityViolationException.class)
public class UserRepositoryTest {



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
    private UserRepository userRepository;


    static User staticUser;


    @Test
    void connectionEstablished() {
        assertTrue(postgresContainer.isCreated());
        assertTrue(postgresContainer.isRunning());
    }

    @BeforeAll
    public static void setUp() {
        UUID userId = UUID.randomUUID();
        staticUser = User.builder()
                .userId(userId)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();
    }

    @BeforeEach
    void setUpEachTest() {
        userRepository.deleteAll();  // Clear the repository before each test
    }

    @Test
    public void createUserSuccess() {

        userRepository.save(staticUser);

        User user = userRepository.findById(staticUser.getUserId()).get();

        assertEquals(staticUser, user);

    }

    @Test
    @Transactional(rollbackOn = DataIntegrityViolationException.class)
    public void createUser_Failure_NotUniqueEmail() {

        userRepository.save(staticUser);

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();


        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user); // Force Hibernate to execute the SQL statement
        });
    }

    @Test
    public void updateUser_Success() {

        userRepository.save(staticUser);

        User user = userRepository.findById(staticUser.getUserId()).get();

        user.setFirstName("John");

        userRepository.save(user);

        User updatedUser = userRepository.findById(staticUser.getUserId()).get();

        assertEquals("John", updatedUser.getFirstName());
    }

    @Test
    public void createUser_FirstNameNull_Failure() {

        User user = staticUser;
        user.setFirstName(null);

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    public void createUser_LastNameNull_Failure() {

        User user = staticUser;
        user.setLastName(null);

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    public void createUser_EmailNull_Failure() {

        User user = staticUser;
        user.setEmail(null);

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    public void createUser_InvalidPhoneNumber_Failure() {

        User user = staticUser;
        user.setPhoneNumber("+1234567890");

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    public void deleteUserById_Success() {

        userRepository.save(staticUser);

        userRepository.deleteById(staticUser.getUserId());

        assertFalse(userRepository.findById(staticUser.getUserId()).isPresent());
    }
}
