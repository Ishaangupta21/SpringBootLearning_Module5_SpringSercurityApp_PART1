package com.example.demo4.SecurityApp.controllers;

import com.example.demo4.SecurityApp.dto.LoginDTO;
import com.example.demo4.SecurityApp.dto.LoginResponseDTO;
import com.example.demo4.SecurityApp.dto.SignupDTO;
import com.example.demo4.SecurityApp.dto.UserDTO;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.services.AuthService;
import com.example.demo4.SecurityApp.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Value("${deploy.env}")
    private String deployEnv;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignupDTO signupDTO){
        UserDTO userDto = userService.signUp(signupDTO);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logIn(@RequestBody LoginDTO loginDTO, HttpServletResponse response, HttpServletRequest request){
        LoginResponseDTO loginResponseDTO = authService.logIn(loginDTO);
        Cookie cookie = new Cookie("refreshToken", loginResponseDTO.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AuthenticationServiceException("Refresh Token not found insinde the cookie"));

        LoginResponseDTO loginResponseDTO = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(loginResponseDTO);
    }
}
