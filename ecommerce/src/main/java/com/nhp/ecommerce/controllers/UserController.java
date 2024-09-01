package com.nhp.ecommerce.controllers;


import com.nhp.ecommerce.components.LocalizationUtil;
import com.nhp.ecommerce.dtos.LoginUserDTO;
import com.nhp.ecommerce.dtos.RegisterUserDTO;
import com.nhp.ecommerce.responses.ApiResponse;
import com.nhp.ecommerce.responses.LoginResponse;
import com.nhp.ecommerce.responses.RegisterUserResponse;
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
    public ApiResponse<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ApiResponse.<RegisterUserResponse>builder()
                    .message(errorMessages.toString())
                    .build();
        }
        LOGGER.info("Registering user with phone number: {}", userDTO.getPhoneNumber());

        userService.createUser(userDTO);
        return ApiResponse.<RegisterUserResponse>builder()
                .message(localizationUtil.getLocalizedMessage(MessageKey.REGISTER_SUCCESSFULLY))
                .result(RegisterUserResponse.builder()
                        .phoneNumber(userDTO.getPhoneNumber())
                        .build())
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginUserDTO userLoginDTO) {
        LOGGER.info("Logging in user with phone number: {}", userLoginDTO.getPhoneNumber());
        String token =  userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
        return ApiResponse.<LoginResponse>builder()
                .message(localizationUtil.getLocalizedMessage(MessageKey.LOGIN_SUCCESSFULLY))
                .result(LoginResponse.builder()
                        .token(token)
                        .build())
                .build();
    }
}
