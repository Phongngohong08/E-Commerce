package com.nhp.ecommerce.controllers;


import com.nhp.ecommerce.components.LocalizationUtil;
import com.nhp.ecommerce.dtos.LoginUserDTO;
import com.nhp.ecommerce.dtos.RegisterUserDTO;
import com.nhp.ecommerce.responses.LoginResponse;
import com.nhp.ecommerce.services.UserService;
import com.nhp.ecommerce.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final UserService userService;
    private final LocalizationUtil localizationUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        try {
            LOGGER.info("Registering user with phone number: {}", userDTO.getPhoneNumber());
            userService.createUser(userDTO);
            return ResponseEntity.ok(localizationUtil.getLocalizedMessage(MessageKey.REGISTER_SUCCESSFULLY));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginUserDTO userLoginDTO) {
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message(localizationUtil.getLocalizedMessage(MessageKey.LOGIN_SUCCESSFULLY))
                            .token(token).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(e.getMessage()).build()
            );
        }
    }
}
