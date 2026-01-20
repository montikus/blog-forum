package com.example.blog.controller.web;

import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

	private final UserService serwisUzytkownikow;
	private final PasswordEncoder szyfratorHasel;

	public AuthController(UserService serwisUzytkownikow, PasswordEncoder szyfratorHasel) {
		this.serwisUzytkownikow = serwisUzytkownikow;
		this.szyfratorHasel = szyfratorHasel;
	}

	@GetMapping("/login")
	public String pokazLogowanie() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String pokazRejestracje(Model modelDanych) {
		if (!modelDanych.containsAttribute("rejestracja")) {
			modelDanych.addAttribute("rejestracja", new RejestracjaFormularz());
		}
		return "auth/register";
	}

	@PostMapping("/register")
	public String zarejestruj(
			@Valid @ModelAttribute("rejestracja") RejestracjaFormularz formularz,
			BindingResult wynikWalidacji,
			Model modelDanych
	) {
		if (serwisUzytkownikow.czyIstniejeNazwaUzytkownika(formularz.getNazwaUzytkownika())) {
			wynikWalidacji.rejectValue("nazwaUzytkownika", "nazwaUzytkownika", "Nazwa uzytkownika jest zajeta");
		}
		if (serwisUzytkownikow.czyIstniejeAdresEmail(formularz.getAdresEmail())) {
			wynikWalidacji.rejectValue("adresEmail", "adresEmail", "Adres email jest zajety");
		}
		if (wynikWalidacji.hasErrors()) {
			modelDanych.addAttribute("rejestracja", formularz);
			return "auth/register";
		}
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(formularz.getNazwaUzytkownika());
		uzytkownik.setAdresEmail(formularz.getAdresEmail());
		uzytkownik.setHasloHash(szyfratorHasel.encode(formularz.getHaslo()));
		uzytkownik.setRola(Role.USER);
		serwisUzytkownikow.zapiszUzytkownika(uzytkownik);
		return "redirect:/login?registered";
	}

	public static class RejestracjaFormularz {

		@NotBlank(message = "Nazwa uzytkownika jest wymagana")
		@Size(min = 3, max = 100, message = "Nazwa uzytkownika musi miec 3-100 znakow")
		private String nazwaUzytkownika;

		@NotBlank(message = "Adres email jest wymagany")
		@Email(message = "Adres email jest niepoprawny")
		private String adresEmail;

		@NotBlank(message = "Haslo jest wymagane")
		@Size(min = 6, max = 100, message = "Haslo musi miec 6-100 znakow")
		private String haslo;

		public String getNazwaUzytkownika() {
			return nazwaUzytkownika;
		}

		public void setNazwaUzytkownika(String nazwaUzytkownika) {
			this.nazwaUzytkownika = nazwaUzytkownika;
		}

		public String getAdresEmail() {
			return adresEmail;
		}

		public void setAdresEmail(String adresEmail) {
			this.adresEmail = adresEmail;
		}

		public String getHaslo() {
			return haslo;
		}

		public void setHaslo(String haslo) {
			this.haslo = haslo;
		}
	}
}
