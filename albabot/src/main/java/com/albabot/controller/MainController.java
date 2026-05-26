package com.albabot.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("/main")
	public String mainPage(Model model) {
		model.addAttribute("jobs", new ArrayList<>());
		return "main";
	}
}
