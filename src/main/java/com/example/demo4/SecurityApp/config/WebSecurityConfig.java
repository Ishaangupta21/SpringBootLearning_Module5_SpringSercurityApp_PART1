package com.example.demo4.SecurityApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/posts").permitAll()
                            .requestMatchers("/posts/**").hasAnyRole("ADMIN")
                            .anyRequest().authenticated())
                    .csrf(csrfConfig-> csrfConfig.disable())
                    .sessionManagement(sessionConfig -> sessionConfig
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .formLogin(Customizer.withDefaults());

            //Won't be needing forms since we are going to use JWT with stateless sessions
            // If you run the App and the form keeps on coming even after the login Creds are correct
            // that is because there are no session
        return httpSecurity.build();
    }

    // This Bean uses UserBuilder to give two Users to be stored in Memory using the InMemoryUserDetailsManager()
    //Only useful for testing purposes
    //In production we store these login info in databases
    @Bean
    UserDetailsService myInMemoryUserDetailService(){
        UserDetails normalUser = User
                .withUsername("Ishaan")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails adminUser = User
                .withUsername("Admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(normalUser,adminUser);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
