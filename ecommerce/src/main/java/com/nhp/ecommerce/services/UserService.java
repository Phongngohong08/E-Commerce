package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.RegisterUserDTO;

public interface UserService {
    void createUser(RegisterUserDTO userDTO) throws Exception;
    public String login(String phoneNumber, String password) throws Exception;
}
