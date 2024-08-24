package com.nhp.ecommerce.services;

import com.nhp.ecommerce.components.JwtTokenUtil;
import com.nhp.ecommerce.dtos.RegisterUserDTO;
import com.nhp.ecommerce.exceptions.DataNotFoundException;
import com.nhp.ecommerce.models.Role;
import com.nhp.ecommerce.models.User;
import com.nhp.ecommerce.repositories.RoleRepository;
import com.nhp.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImpUserService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public void createUser(RegisterUserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role does not exist"));
        if(!userDTO.getRetypePassword().equals(userDTO.getPassword())) {
            throw new Exception("Password and retype password do not match");
        }
        User newUser = User.builder()
                .name(userDTO.getName())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .role(role)
                .build();
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("User does not exist");
        }
        User existingUser = optionalUser.get();

        if(!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }

        Long roleId = existingUser.getRole().getId();
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException("Role does not exist");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber,
                password,
                existingUser.getAuthorities()
        );

        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
}
