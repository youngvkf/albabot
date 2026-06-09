package com.albabot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // HTML 리다이렉트를 위해 @RestController에서 @Controller로 변경
public class IndexController {
	@GetMapping("/")
	public String index() {
		return "redirect:/login"; // 첫 접속 시 공고 목록 화면으로 강제 이동
	}
}