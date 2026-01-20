package com.example.blog.controller.web;

import com.example.blog.dto.ImportResultDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.service.AdminService;
import com.example.blog.service.CsvService;
import com.example.blog.service.StatystykiService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

	private final AdminService serwisAdmina;
	private final CsvService serwisCsv;
	private final StatystykiService serwisStatystyk;

	public AdminWebController(
			AdminService serwisAdmina,
			CsvService serwisCsv,
			StatystykiService serwisStatystyk
	) {
		this.serwisAdmina = serwisAdmina;
		this.serwisCsv = serwisCsv;
		this.serwisStatystyk = serwisStatystyk;
	}

	@GetMapping
	public String pokazPanel(Model modelDanych) {
		List<Map<String, Object>> topPosty = serwisStatystyk.pobierzTopPosty();
		List<Map<String, Object>> statystykiUzytkownikow = serwisStatystyk.pobierzStatystykiUzytkownikow();
		modelDanych.addAttribute("topPosty", topPosty);
		modelDanych.addAttribute("statystykiUzytkownikow", statystykiUzytkownikow);
		return "admin/panel";
	}

	@GetMapping("/users")
	public String pokazUzytkownikow(Model modelDanych) {
		List<UserDto> uzytkownicy = serwisAdmina.pobierzUzytkownikow();
		modelDanych.addAttribute("uzytkownicy", uzytkownicy);
		return "admin/users";
	}

	@PostMapping("/users/{idUzytkownika}/delete")
	public String usunUzytkownika(@PathVariable Long idUzytkownika) {
		serwisAdmina.usunUzytkownika(idUzytkownika);
		return "redirect:/admin/users";
	}

	@GetMapping("/posts")
	public String pokazPosty(Model modelDanych) {
		List<PostDto> posty = serwisAdmina.pobierzPosty();
		modelDanych.addAttribute("posty", posty);
		return "admin/posts";
	}

	@PostMapping("/posts/{idPosta}/delete")
	public String usunPost(@PathVariable Long idPosta) {
		serwisAdmina.usunPost(idPosta);
		return "redirect:/admin/posts";
	}

	@GetMapping("/import_export")
	public String pokazImportEksport(Model modelDanych) {
		modelDanych.addAttribute("wynikImportuUzytkownikow", null);
		modelDanych.addAttribute("wynikImportuPostow", null);
		return "admin/import_export";
	}

	@PostMapping("/import/users")
	public String importujUzytkownikow(@RequestPart("plik") MultipartFile plik, Model modelDanych) {
		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);
		modelDanych.addAttribute("wynikImportuUzytkownikow", wynik);
		modelDanych.addAttribute("wynikImportuPostow", null);
		return "admin/import_export";
	}

	@PostMapping("/import/posts")
	public String importujPosty(@RequestPart("plik") MultipartFile plik, Model modelDanych) {
		ImportResultDto wynik = serwisCsv.importujPosty(plik);
		modelDanych.addAttribute("wynikImportuUzytkownikow", null);
		modelDanych.addAttribute("wynikImportuPostow", wynik);
		return "admin/import_export";
	}
}
