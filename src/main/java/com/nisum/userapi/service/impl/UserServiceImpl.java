package com.nisum.userapi.service.impl;

import com.nisum.userapi.config.ValidationProperties;
import com.nisum.userapi.dto.PhoneRequest;
import com.nisum.userapi.dto.UserPatchRequest;
import com.nisum.userapi.dto.UserRequest;
import com.nisum.userapi.entity.Phone;
import com.nisum.userapi.entity.User;
import com.nisum.userapi.exception.EmailAlreadyExistsException;
import com.nisum.userapi.exception.UserNotFoundException;
import com.nisum.userapi.repository.UserRepository;
import com.nisum.userapi.security.JwtService;
import com.nisum.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationProperties validationProperties;
    private final JwtService jwtService;

    @Override
    @Transactional
    public User createUser(UserRequest request) {

        String email = normalizeEmail(request.getEmail());

        validateUserData(email, request.getPassword());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El correo ya registrado");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        LocalDateTime now = LocalDateTime.now();

        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setIsActive(true);

        addPhones(user, request.getPhones());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getId());

        savedUser.setToken(token);

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("El usuario no existe"));
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UserRequest request) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("El usuario no existe"));

        String email = normalizeEmail(request.getEmail());

        validateUserData(email, request.getPassword());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El correo ya registrado");
        }

        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setModified(LocalDateTime.now());

        // Eliminamos los teléfonos anteriores.
        user.getPhones().clear();

        // Agregamos los teléfonos recibidos en la petición.
        addPhones(user, request.getPhones());

        return user;
    }

    @Override
    @Transactional
    public User patchUser(UUID id, UserPatchRequest request) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("El usuario no existe"));

        boolean modified = false;

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            user.setName(request.getName());
            modified = true;
        }

        if (request.getEmail() != null) {

            String email = normalizeEmail(request.getEmail());

            if (!Pattern.matches(validationProperties.emailRegex(), email)) {
                throw new IllegalArgumentException("El formato del correo es inválido");
            }

            if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                throw new EmailAlreadyExistsException("El correo ya registrado");
            }

            user.setEmail(email);
            modified = true;
        }

        if (request.getPassword() != null) {

            if (!Pattern.matches(
                validationProperties.passwordRegex(),
                request.getPassword())) {
                    throw new IllegalArgumentException("La contraseña no cumple con el formato requerido");
            }

            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );

            modified = true;
        }

        if (request.getPhones() != null) {
            user.getPhones().clear();
            addPhones(user, request.getPhones());
            modified = true;
        }

        if (modified) {
            user.setModified(LocalDateTime.now());
        }

        return user;
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("El usuario no existe"));
        userRepository.delete(user);
    }

    private void validateUserData(String email, String password) {

        if (!Pattern.matches(validationProperties.emailRegex(), email)) {
            throw new IllegalArgumentException("El formato del correo es inválido");
        }

        if (!Pattern.matches(validationProperties.passwordRegex(), password)) {
            throw new IllegalArgumentException("La contraseña no cumple con el formato requerido");
        }
    }

    private void addPhones(User user, List<PhoneRequest> phones) {

        if (phones == null) {
            return;
        }

        phones.forEach(phoneRequest -> {

            Phone phone = new Phone();

            phone.setNumber(phoneRequest.getNumber());
            phone.setCityCode(phoneRequest.getCityCode());
            phone.setCountryCode(phoneRequest.getCountryCode());

            user.addPhone(phone);
        });
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}