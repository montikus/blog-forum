package com.example.blog.controller.api;

import com.example.blog.dto.MessageDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageRestController {

	private final MessageService serwisWiadomosci;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public MessageRestController(
			MessageService serwisWiadomosci,
			BiezacyUzytkownikService serwisBiezacegoUzytkownika
	) {
		this.serwisWiadomosci = serwisWiadomosci;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping("/inbox")
	public Page<MessageDto> pobierzInbox(Pageable stronicowanie) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		return serwisWiadomosci.pobierzOdebrane(aktualny, stronicowanie);
	}

	@GetMapping("/sent")
	public Page<MessageDto> pobierzSent(Pageable stronicowanie) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		return serwisWiadomosci.pobierzWyslane(aktualny, stronicowanie);
	}

	@PostMapping
	public ResponseEntity<MessageDto> wyslijWiadomosc(@Valid @RequestBody WyslijWiadomoscRequest zadanie) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		MessageDto wynik;
		if (zadanie.idOdbiorcy() != null) {
			wynik = serwisWiadomosci.wyslijWiadomosc(
					zadanie.idOdbiorcy(),
					zadanie.tresc(),
					aktualny,
					zadanie.idPosta()
			);
		} else if (zadanie.nazwaOdbiorcy() != null && !zadanie.nazwaOdbiorcy().isBlank()) {
			wynik = serwisWiadomosci.wyslijWiadomosc(
					zadanie.nazwaOdbiorcy(),
					zadanie.tresc(),
					aktualny,
					zadanie.idPosta()
			);
		} else {
			throw new IllegalArgumentException("Brak odbiorcy wiadomosci");
		}
		return ResponseEntity.ok(wynik);
	}

	public record WyslijWiadomoscRequest(
			Long idOdbiorcy,
			String nazwaOdbiorcy,
			@NotBlank(message = "Tresc jest wymagana") String tresc,
			Long idPosta
	) {
	}
}
