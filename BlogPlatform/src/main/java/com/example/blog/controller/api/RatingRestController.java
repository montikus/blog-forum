package com.example.blog.controller.api;

import com.example.blog.dto.RatingDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.RatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/{idPosta}/ratings")
public class RatingRestController {

	private final RatingService serwisOcen;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public RatingRestController(RatingService serwisOcen, BiezacyUzytkownikService serwisBiezacegoUzytkownika) {
		this.serwisOcen = serwisOcen;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@PostMapping
	public RatingDto ocenPost(
			@PathVariable Long idPosta,
			@Valid @RequestBody OcenaRequest zadanie
	) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		return serwisOcen.ocenPost(idPosta, zadanie.wartosc(), aktualny);
	}

	@GetMapping
	public RatingDto pobierzSrednia(@PathVariable Long idPosta) {
		double srednia = serwisOcen.pobierzSrednia(idPosta);
		RatingDto wynik = new RatingDto();
		wynik.setPostId(idPosta);
		wynik.setSrednia(srednia);
		return wynik;
	}

	public record OcenaRequest(
			@Min(value = 1, message = "Ocena musi byc w zakresie 1-5")
			@Max(value = 5, message = "Ocena musi byc w zakresie 1-5")
			int wartosc
	) {
	}
}
