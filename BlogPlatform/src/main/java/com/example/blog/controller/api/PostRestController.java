package com.example.blog.controller.api;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.PostFormDto;
import com.example.blog.model.User;
import com.example.blog.service.BiezacyUzytkownikService;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

	private final PostService serwisPostow;
	private final BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	public PostRestController(PostService serwisPostow, BiezacyUzytkownikService serwisBiezacegoUzytkownika) {
		this.serwisPostow = serwisPostow;
		this.serwisBiezacegoUzytkownika = serwisBiezacegoUzytkownika;
	}

	@GetMapping
	public Page<PostDto> pobierzPosty(@ParameterObject Pageable stronicowanie) {
		return serwisPostow.pobierzWszystkie(stronicowanie);
	}

	@GetMapping("/{idPosta}")
	public PostDto pobierzPost(@PathVariable Long idPosta) {
		return serwisPostow.pobierzPost(idPosta);
	}

	@PostMapping
	public ResponseEntity<PostDto> utworzPost(@Valid @RequestBody PostFormDto formularz) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		PostDto wynik = serwisPostow.utworzPost(formularz, aktualny);
		URI lokacja = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{idPosta}")
				.buildAndExpand(wynik.getId())
				.toUri();
		return ResponseEntity.created(lokacja).body(wynik);
	}

	@PutMapping("/{idPosta}")
	public PostDto zaktualizujPost(@PathVariable Long idPosta, @Valid @RequestBody PostFormDto formularz) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		return serwisPostow.edytujPost(idPosta, formularz, aktualny);
	}

	@DeleteMapping("/{idPosta}")
	public ResponseEntity<Void> usunPost(@PathVariable Long idPosta) {
		User aktualny = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();
		serwisPostow.usunPost(idPosta, aktualny);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search")
	public Page<PostDto> wyszukajPosty(
			@RequestParam(name = "query", required = false) String fraza,
			@RequestParam(name = "author", required = false) String nazwaAutora,
			@ParameterObject Pageable stronicowanie
	) {
		return serwisPostow.wyszukajPosty(fraza, nazwaAutora, stronicowanie);
	}
}
