package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.LoginDTO;
import com.example.demo4.SecurityApp.dto.LoginResponseDTO;
import com.example.demo4.SecurityApp.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public LoginResponseDTO logIn(LoginDTO loginDTO) {
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken =  jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(user.getId(), accessToken,refreshToken);
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        //First Validate that the refresh token is Valid or not.
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.findUserById(userId);

        // now generate the accessToken again
        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponseDTO(user.getId(),accessToken,refreshToken);
    }
}
