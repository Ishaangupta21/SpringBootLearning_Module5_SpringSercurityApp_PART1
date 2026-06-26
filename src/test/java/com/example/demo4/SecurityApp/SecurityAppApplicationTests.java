package com.example.demo4.SecurityApp;

import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityAppApplicationTests {

	@Autowired
	private JwtService jwtService;
	@Test
	void contextLoads() {
		User user = new User(4L,"Ishaan@gmail.com","Ishaan12345","Ishaan");

		String jwtToken = jwtService.generateToken(user);
		System.out.println(jwtToken);

		Long id = jwtService.getUserIdFromToken(jwtToken);
		System.out.println(id);
	}

}
