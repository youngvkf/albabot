package com.albabot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import com.albabot.model.User;
import com.albabot.service.UserService;

@RestController
public class LoginController {
	private final UserService userService;
	
	public LoginController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/login-test")
//	public String loginPage() {
//		return "login";
//	}
	public String loginTest(@RequestParam String email,
			@RequestParam String password,
			HttpSession session) {
		User user = userService.login(email, password);
		if(user == null) {
			return "로그인 실패";
		}
		session.setAttribute("loginUser", user);
		return "로그인 성공: " + user.getName();
	}
}
