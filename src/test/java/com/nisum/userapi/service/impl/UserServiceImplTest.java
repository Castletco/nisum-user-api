package com.nisum.userapi.service.impl;

import com.nisum.userapi.config.ValidationProperties;
import com.nisum.userapi.dto.UserRequest;
import com.nisum.userapi.entity.User;
import com.nisum.userapi.exception.EmailAlreadyExistsException;
import com.nisum.userapi.repository.UserRepository;
import com.nisum.userapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidationProperties validationProperties;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {

        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Juan Rodriguez");
        request.setEmail("juan@rodriguez.org");
        request.setPassword("hunter2");

        when(validationProperties.emailRegex()).thenReturn("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        when(validationProperties.passwordRegex()).thenReturn("^.{6,}$");

        when(userRepository.existsByEmail("juan@rodriguez.org")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WhenDataIsValid_ShouldSaveUserAndGenerateToken() {

        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Juan Rodriguez");
        request.setEmail("JUAN@RODRIGUEZ.ORG");
        request.setPassword("hunter2");

        when(validationProperties.emailRegex()).thenReturn("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        when(validationProperties.passwordRegex()).thenReturn("^.{6,}$");

        when(passwordEncoder.encode("hunter2")).thenReturn("hashed-password");

        UUID userId = UUID.randomUUID();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });

        when(jwtService.generateToken(userId)).thenReturn("jwt-token-test");

        // Act
        User result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("juan@rodriguez.org", result.getEmail());
        assertEquals("hashed-password", result.getPassword());
        assertEquals("jwt-token-test", result.getToken());
        assertTrue(result.getIsActive());

        verify(userRepository).existsByEmail("juan@rodriguez.org");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(userId);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateData() {

        // Arrange
        UUID userId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Juan Rodriguez");
        existingUser.setEmail("juan@rodriguez.org");
        existingUser.setCreated(created);

        UserRequest request = new UserRequest();
        request.setName("Juan Carlos Rodriguez");
        request.setEmail("juancarlos@rodriguez.org");
        request.setPassword("hunter3");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(validationProperties.emailRegex()).thenReturn("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        when(validationProperties.passwordRegex()).thenReturn("^.{6,}$");

        when(passwordEncoder.encode("hunter3")).thenReturn("new-hashed-password");

        // Act
        User result = userService.updateUser(userId, request);

        // Assert
        assertEquals(userId, result.getId());
        assertEquals("Juan Carlos Rodriguez", result.getName());
        assertEquals("juancarlos@rodriguez.org", result.getEmail());
        assertEquals("new-hashed-password", result.getPassword());
        assertEquals(created, result.getCreated());
        assertNotNull(result.getModified());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("juancarlos@rodriguez.org");
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {

        // Arrange
        UUID userId = UUID.randomUUID();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("juan@rodriguez.org");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).delete(existingUser);
    }
}