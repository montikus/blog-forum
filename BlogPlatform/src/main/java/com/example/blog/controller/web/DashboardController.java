package com.example.blog.controller.web;

import com.example.blog.dto.PostDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	private final PostService serwisPostow;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public DashboardController(PostService serwisPostow, BiezacyUzytkownikService serwisBiezacegoUzytkownika) {
		this.serwisPostow = serwisPostow;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping("/dashboard")
	public String pokazDashboard(Pageable stronicowanie, Model modelDanych) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		Page<PostDto> stronaPostow = serwisPostow.znajdzMojePosty(aktualny, stronicowanie);
		modelDanych.addAttribute("stronaPostow", stronaPostow);
		return "posts/dashboard";
	}
}
