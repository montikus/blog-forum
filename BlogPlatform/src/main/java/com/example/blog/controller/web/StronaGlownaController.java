package com.example.blog.controller.web;

import com.example.blog.dto.PostDto;
import com.example.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StronaGlownaController {

	private final PostService serwisPostow;

	public StronaGlownaController(PostService serwisPostow) {
		this.serwisPostow = serwisPostow;
	}

	@GetMapping("/")
	public String pokazStroneGlowna(Pageable stronicowanie, Model modelDanych) {
		Page<PostDto> stronaPostow = serwisPostow.pobierzWszystkie(stronicowanie);
		modelDanych.addAttribute("stronaPostow", stronaPostow);
		return "posts/index";
	}

	@GetMapping("/search")
	public String wyszukajPosty(
			@RequestParam(name = "query", required = false) String fraza,
			@RequestParam(name = "author", required = false) String nazwaAutora,
			Pageable stronicowanie,
			Model modelDanych
	) {
		Page<PostDto> stronaPostow = serwisPostow.wyszukajPosty(fraza, nazwaAutora, stronicowanie);
		modelDanych.addAttribute("stronaPostow", stronaPostow);
		modelDanych.addAttribute("query", fraza);
		modelDanych.addAttribute("author", nazwaAutora);
		return "posts/search";
	}
}
