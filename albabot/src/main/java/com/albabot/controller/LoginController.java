package com.albabot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model 임포트 추가
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
	
	// 로그인 페이지 열기 (실패 시 들어오는 error 파라미터 처리 추가)
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
			// 💡 [수정] 프론트엔드(Thymeleaf) 구조에 맞게 "loginUser"라는 Key로 유저 객체를 통째로 세션에 저장합니다.
			session.setAttribute("loginUser", user);
			System.out.println("로그인 성공: " + user.getName() + "님");
			return "redirect:/main";
		}
		
		System.out.println("로그인 실패: 이메일 또는 비밀번호 불일치");
		// 💡 [수정] 로그인 실패 시 에러 파라미터를 달고 로그인 페이지로 리다이렉트합니다.
		return "redirect:/login?error=true";
	}
}