package com.albabot.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import com.albabot.model.User;
import com.albabot.service.UserService;

@Controller
public class LoginController {
	private final UserService userService;
	
	public LoginController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			HttpSession session
			) {
		User user = userService.login(email, password);
		
		if(user != null) {
			session.setAttribute(email, password);
			System.out.println("로그인 성공");
			return "redirect:/main";
		}
		
		return "redirect:/login";
	}

}
