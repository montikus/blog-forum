package com.example.blog.controller.web;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.PostFormDto;
import com.example.blog.dto.UserDto;
import com.example.blog.exception.ForbiddenException;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import com.example.blog.service.RatingService;
import com.example.blog.service.UserService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
public class PostWebController {

	private final PostService serwisPostow;
	private final CommentService serwisKomentarzy;
	private final RatingService serwisOcen;
	private final UserService serwisUzytkownikow;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public PostWebController(
			PostService serwisPostow,
			CommentService serwisKomentarzy,
			RatingService serwisOcen,
			UserService serwisUzytkownikow,
			BiezacyUzytkownikService serwisBiezacegoUzytkownika
	) {
		this.serwisPostow = serwisPostow;
		this.serwisKomentarzy = serwisKomentarzy;
		this.serwisOcen = serwisOcen;
		this.serwisUzytkownikow = serwisUzytkownikow;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping
	public String pokazListePostow(Pageable stronicowanie, Model modelDanych) {
		Page<PostDto> stronaPostow = serwisPostow.pobierzWszystkie(stronicowanie);
		modelDanych.addAttribute("stronaPostow", stronaPostow);
		return "posts/index";
	}

	@GetMapping("/{idPosta}")
	public String pokazSzczegolyPosta(@PathVariable Long idPosta, Model modelDanych) {
		PostDto postBloga = serwisPostow.pobierzPost(idPosta);
		List<CommentDto> komentarze = serwisKomentarzy.pobierzKomentarzeDlaPosta(idPosta);
		String nazwyAutorow = postBloga.getAutorzy().stream()
				.map(UserDto::getNazwaUzytkownika)
				.collect(Collectors.joining(", "));
		String nazwaOdbiorcy = postBloga.getAutorzy().stream()
				.findFirst()
				.map(UserDto::getNazwaUzytkownika)
				.orElse(null);

		User aktualny = null;
		boolean czyZalogowany = false;
		boolean czyMozeEdytowac = false;
		try {
			aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
			czyZalogowany = true;
			boolean czyAdmin = aktualny.getRola() == Role.ADMIN;
			boolean czyAutor = false;
			for (UserDto autor : postBloga.getAutorzy()) {
				if (autor.getId() != null && autor.getId().equals(aktualny.getId())) {
					czyAutor = true;
					break;
				}
			}
			czyMozeEdytowac = czyAdmin || czyAutor;
		} catch (ForbiddenException pomijanyWyjatek) {
		}

		modelDanych.addAttribute("post", postBloga);
		modelDanych.addAttribute("komentarze", komentarze);
		modelDanych.addAttribute("nazwyAutorow", nazwyAutorow);
		modelDanych.addAttribute("nazwaOdbiorcy", nazwaOdbiorcy);
		modelDanych.addAttribute("czyZalogowany", czyZalogowany);
		modelDanych.addAttribute("czyMozeEdytowac", czyMozeEdytowac);
		return "posts/details";
	}

	@GetMapping("/new")
	public String pokazFormularzNowegoPosta(Model modelDanych) {
		modelDanych.addAttribute("formularz", new PostFormDto());
		modelDanych.addAttribute("uzytkownicy", serwisUzytkownikow.pobierzWszystkich());
		modelDanych.addAttribute("trybEdycji", false);
		return "posts/form";
	}

	@PostMapping
	public String utworzPost(
			@Valid @ModelAttribute("formularz") PostFormDto formularz,
			BindingResult wynikWalidacji,
			Model modelDanych
	) {
		if (wynikWalidacji.hasErrors()) {
			modelDanych.addAttribute("uzytkownicy", serwisUzytkownikow.pobierzWszystkich());
			modelDanych.addAttribute("trybEdycji", false);
			return "posts/form";
		}
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		PostDto wynik = serwisPostow.utworzPost(formularz, aktualny);
		serwisPostow.ustawWspolautorow(wynik.getId(), polaczWspolautorow(formularz, aktualny), aktualny);
		return "redirect:/posts/" + wynik.getId();
	}

	@GetMapping("/{idPosta}/edit")
	public String pokazFormularzEdycji(@PathVariable Long idPosta, Model modelDanych) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		PostFormDto formularz = serwisPostow.pobierzFormularzDoEdycji(idPosta, aktualny);
		modelDanych.addAttribute("formularz", formularz);
		modelDanych.addAttribute("uzytkownicy", serwisUzytkownikow.pobierzWszystkich());
		modelDanych.addAttribute("idPosta", idPosta);
		modelDanych.addAttribute("trybEdycji", true);
		return "posts/form";
	}

	@PostMapping("/{idPosta}/edit")
	public String edytujPost(
			@PathVariable Long idPosta,
			@Valid @ModelAttribute("formularz") PostFormDto formularz,
			BindingResult wynikWalidacji,
			Model modelDanych
	) {
		if (wynikWalidacji.hasErrors()) {
			modelDanych.addAttribute("uzytkownicy", serwisUzytkownikow.pobierzWszystkich());
			modelDanych.addAttribute("idPosta", idPosta);
			modelDanych.addAttribute("trybEdycji", true);
			return "posts/form";
		}
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisPostow.edytujPost(idPosta, formularz, aktualny);
		serwisPostow.ustawWspolautorow(idPosta, polaczWspolautorow(formularz, aktualny), aktualny);
		return "redirect:/posts/" + idPosta;
	}

	@PostMapping("/{idPosta}/delete")
	public String usunPost(@PathVariable Long idPosta) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisPostow.usunPost(idPosta, aktualny);
		return "redirect:/dashboard";
	}

	@PostMapping("/{idPosta}/comments")
	public String dodajKomentarz(@PathVariable Long idPosta, @RequestParam("tresc") String tresc) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisKomentarzy.dodajKomentarz(idPosta, tresc, aktualny);
		return "redirect:/posts/" + idPosta;
	}

	@PostMapping("/{idPosta}/ratings")
	public String dodajOcene(
			@PathVariable Long idPosta,
			@RequestParam("wartosc") int wartosc
	) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisOcen.ocenPost(idPosta, wartosc, aktualny);
		return "redirect:/posts/" + idPosta;
	}

	private List<Long> polaczWspolautorow(PostFormDto formularz, User aktualny) {
		List<Long> wspolautorzy = new ArrayList<>();
		if (formularz.getWspolautorzyId() != null) {
			wspolautorzy.addAll(formularz.getWspolautorzyId());
		}
		if (aktualny != null && aktualny.getId() != null && !wspolautorzy.contains(aktualny.getId())) {
			wspolautorzy.add(aktualny.getId());
		}
		return wspolautorzy;
	}
}
