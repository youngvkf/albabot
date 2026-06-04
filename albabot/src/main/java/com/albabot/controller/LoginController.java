package com.albabot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	
	// 로그인 페이지 열기
	@GetMapping("/login")
	public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
		}
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
			session.setAttribute("loginUser", user);
			System.out.println("로그인 성공: " + user.getName() + "님");
			return "redirect:/main";
		}
		
		System.out.println("로그인 실패: 이메일 또는 비밀번호 불일치");
		return "redirect:/login?error=true";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		System.out.println("로그아웃 성공 -> 로그인 화면으로 이동");
		return "redirect:/login";
	}
}