package com.example.blog.controller.web;

import com.example.blog.dto.MessageDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/messages")
public class WiadomosciWebController {

	private final MessageService serwisWiadomosci;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public WiadomosciWebController(
			MessageService serwisWiadomosci,
			BiezacyUzytkownikService serwisBiezacegoUzytkownika
	) {
		this.serwisWiadomosci = serwisWiadomosci;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping("/inbox")
	public String pokazInbox(Pageable stronicowanie, Model modelDanych) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		Page<MessageDto> stronaWiadomosci = serwisWiadomosci.pobierzOdebrane(aktualny, stronicowanie);
		modelDanych.addAttribute("stronaWiadomosci", stronaWiadomosci);
		return "messages/inbox";
	}

	@GetMapping("/sent")
	public String pokazWyslane(Pageable stronicowanie, Model modelDanych) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		Page<MessageDto> stronaWiadomosci = serwisWiadomosci.pobierzWyslane(aktualny, stronicowanie);
		modelDanych.addAttribute("stronaWiadomosci", stronaWiadomosci);
		return "messages/sent";
	}

	@GetMapping("/compose")
	public String pokazFormularzWiadomosci(
			@RequestParam(name = "nazwaOdbiorcy", required = false) String nazwaOdbiorcy,
			@RequestParam(name = "postId", required = false) Long idPosta,
			Model modelDanych
	) {
		FormularzWiadomosci formularz = new FormularzWiadomosci();
		formularz.setNazwaOdbiorcy(nazwaOdbiorcy);
		formularz.setIdPosta(idPosta);
		modelDanych.addAttribute("formularz", formularz);
		return "messages/compose";
	}

	@PostMapping("/send")
	public String wyslijWiadomosc(
			@ModelAttribute("formularz") FormularzWiadomosci formularz,
			BindingResult wynikWalidacji,
			Model modelDanych
	) {
		if (formularz.getTresc() == null || formularz.getTresc().isBlank()) {
			wynikWalidacji.rejectValue("tresc", "tresc", "Tresc jest wymagana");
		}
		if (formularz.getNazwaOdbiorcy() == null || formularz.getNazwaOdbiorcy().isBlank()) {
			wynikWalidacji.rejectValue("nazwaOdbiorcy", "nazwaOdbiorcy", "Odbiorca jest wymagany");
		}
		if (wynikWalidacji.hasErrors()) {
			modelDanych.addAttribute("formularz", formularz);
			return "messages/compose";
		}
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisWiadomosci.wyslijWiadomosc(
				formularz.getNazwaOdbiorcy(),
				formularz.getTresc(),
				aktualny,
				formularz.getIdPosta()
		);
		return "redirect:/messages/sent";
	}

	public static class FormularzWiadomosci {

		private String nazwaOdbiorcy;
		private String tresc;
		private Long idPosta;

		public String getNazwaOdbiorcy() {
			return nazwaOdbiorcy;
		}

		public void setNazwaOdbiorcy(String nazwaOdbiorcy) {
			this.nazwaOdbiorcy = nazwaOdbiorcy;
		}

		public String getTresc() {
			return tresc;
		}

		public void setTresc(String tresc) {
			this.tresc = tresc;
		}

		public Long getIdPosta() {
			return idPosta;
		}

		public void setIdPosta(Long idPosta) {
			this.idPosta = idPosta;
		}
	}
}
