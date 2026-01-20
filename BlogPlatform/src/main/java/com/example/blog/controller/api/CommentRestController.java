package com.example.blog.controller.api;

import com.example.blog.dto.CommentDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/posts/{idPosta}/comments")
public class CommentRestController {

	private final CommentService serwisKomentarzy;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public CommentRestController(
			CommentService serwisKomentarzy,
			BiezacyUzytkownikService serwisBiezacegoUzytkownika
	) {
		this.serwisKomentarzy = serwisKomentarzy;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping
	public List<CommentDto> pobierzKomentarze(@PathVariable Long idPosta) {
		return serwisKomentarzy.pobierzKomentarzeDlaPosta(idPosta);
	}

	@PostMapping
	public ResponseEntity<CommentDto> dodajKomentarz(
			@PathVariable Long idPosta,
			@Valid @RequestBody DodajKomentarzRequest zadanie
	) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		CommentDto wynik = serwisKomentarzy.dodajKomentarz(idPosta, zadanie.tresc(), aktualny);
		URI lokacja = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{idKomentarza}")
				.buildAndExpand(wynik.getId())
				.toUri();
		return ResponseEntity.created(lokacja).body(wynik);
	}

	public record DodajKomentarzRequest(@NotBlank(message = "Tresc jest wymagana") String tresc) {
	}
}
