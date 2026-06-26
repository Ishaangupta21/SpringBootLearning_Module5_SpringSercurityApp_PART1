package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.LoginDTO;
import com.example.demo4.SecurityApp.dto.SignupDTO;
import com.example.demo4.SecurityApp.dto.UserDTO;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.exceptions.ResourceNotFoundException;
import com.example.demo4.SecurityApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // Will now use this instead of the bean for Adding data
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User with email: "+ username + " not found."));
    }

    public User findUserById(Long usedId){
        return userRepository.findById(usedId).orElseThrow(()->new ResourceNotFoundException("User with id: "+ usedId+ "not found."));
    }


    public UserDTO signUp(SignupDTO signupDTO) {
        Optional<User> user = userRepository.findByEmail(signupDTO.getEmail());
        if(user.isPresent()) {
            throw new BadCredentialsException("User with email already exits "+ signupDTO.getEmail());
        }

        User toBeCreatedUser = modelMapper.map(signupDTO, User.class);
        toBeCreatedUser.setPassword(passwordEncoder.encode(toBeCreatedUser.getPassword()));

        User savedUser = userRepository.save(toBeCreatedUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }


}
