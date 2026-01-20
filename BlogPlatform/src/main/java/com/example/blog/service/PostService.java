package com.example.blog.service;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.PostFormDto;
import com.example.blog.exception.ForbiddenException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.PostMapper;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.specification.PostSpecifications;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class PostService {

	private final PostRepository repozytoriumPostow;
	private final UserRepository repozytoriumUzytkownikow;
	private final RatingRepository repozytoriumOcen;
	private final PostMapper mapperPostow;

	public PostService(
			PostRepository repozytoriumPostow,
			UserRepository repozytoriumUzytkownikow,
			RatingRepository repozytoriumOcen,
			PostMapper mapperPostow
	) {
		this.repozytoriumPostow = repozytoriumPostow;
		this.repozytoriumUzytkownikow = repozytoriumUzytkownikow;
		this.repozytoriumOcen = repozytoriumOcen;
		this.mapperPostow = mapperPostow;
	}

	public PostDto utworzPost(PostFormDto formularz, User aktualnyUzytkownik) {
		Post post = mapperPostow.mapujNaEncje(formularz);
		post.getAutorzy().add(aktualnyUzytkownik);
		Post zapisany = repozytoriumPostow.save(post);
		return mapujZOcenami(zapisany);
	}

	public PostDto edytujPost(Long idPosta, PostFormDto formularz, User aktualnyUzytkownik) {
		Post post = pobierzEncjePosta(idPosta);
		zweryfikujUprawnienia(post, aktualnyUzytkownik);
		post.setTytul(formularz.getTytul());
		post.setTresc(formularz.getTresc());
		return mapujZOcenami(post);
	}

	public void usunPost(Long idPosta, User aktualnyUzytkownik) {
		Post post = pobierzEncjePosta(idPosta);
		zweryfikujUprawnienia(post, aktualnyUzytkownik);
		repozytoriumPostow.delete(post);
	}

	@Transactional(readOnly = true)
	public PostDto pobierzPost(Long idPosta) {
		Post post = pobierzEncjePosta(idPosta);
		return mapujZOcenami(post);
	}

	@Transactional(readOnly = true)
	public Page<PostDto> pobierzWszystkie(Pageable stronicowanie) {
		return repozytoriumPostow.findAll(stronicowanie)
				.map(this::mapujZOcenami);
	}

	@Transactional(readOnly = true)
	public Page<PostDto> znajdzMojePosty(User uzytkownik, Pageable stronicowanie) {
		return repozytoriumPostow.znajdzPoAutorachNazwieUzytkownika(uzytkownik.getNazwaUzytkownika(), stronicowanie)
				.map(this::mapujZOcenami);
	}

	@Transactional(readOnly = true)
	public Page<PostDto> wyszukajPosty(String fraza, String nazwaAutora, Pageable stronicowanie) {
		Specification<Post> specyfikacja = Specification.where(PostSpecifications.zawieraFraze(fraza))
				.and(PostSpecifications.maAutora(nazwaAutora));
		return repozytoriumPostow.findAll(specyfikacja, stronicowanie)
				.map(this::mapujZOcenami);
	}

	public void ustawWspolautorow(Long idPosta, List<Long> listaUzytkownikow, User aktualnyUzytkownik) {
		Post post = pobierzEncjePosta(idPosta);
		zweryfikujUprawnienia(post, aktualnyUzytkownik);
		List<Long> identyfikatory = listaUzytkownikow != null ? listaUzytkownikow : List.of();
		List<User> znalezieni = repozytoriumUzytkownikow.findAllById(identyfikatory);
		if (znalezieni.size() != identyfikatory.size()) {
			throw new ResourceNotFoundException("Nie znaleziono wszystkich wspolautorow");
		}
		Set<User> nowiAutorzy = new LinkedHashSet<>(znalezieni);
		if (nowiAutorzy.isEmpty()) {
			nowiAutorzy.add(aktualnyUzytkownik);
		}
		post.setAutorzy(nowiAutorzy);
	}

	@Transactional(readOnly = true)
	public PostFormDto pobierzFormularzDoEdycji(Long idPosta, User aktualnyUzytkownik) {
		Post post = pobierzEncjePosta(idPosta);
		zweryfikujUprawnienia(post, aktualnyUzytkownik);
		PostFormDto formularz = new PostFormDto();
		formularz.setTytul(post.getTytul());
		formularz.setTresc(post.getTresc());
		formularz.setWspolautorzyId(
				post.getAutorzy().stream()
						.map(User::getId)
						.collect(Collectors.toList())
		);
		return formularz;
	}

	@Transactional(readOnly = true)
	public List<PostDto> pobierzPostyDoEksportu() {
		return repozytoriumPostow.findAll().stream()
				.map(this::mapujZOcenami)
				.collect(Collectors.toList());
	}

	private Post pobierzEncjePosta(Long idPosta) {
		return repozytoriumPostow.findById(idPosta)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta"));
	}

	private void zweryfikujUprawnienia(Post post, User uzytkownik) {
		boolean czyAdmin = uzytkownik != null && uzytkownik.getRola() == Role.ADMIN;
		boolean czyAutor = post.getAutorzy().stream()
				.anyMatch(autor -> autor.getId().equals(uzytkownik.getId()));
		if (!czyAdmin && !czyAutor) {
			throw new ForbiddenException("Brak uprawnien do posta");
		}
	}

	private PostDto mapujZOcenami(Post post) {
		double srednia = repozytoriumOcen.pobierzSredniaDlaPosta(post.getId());
		int liczbaKomentarzy = post.getKomentarze() != null ? post.getKomentarze().size() : 0;
		return mapperPostow.mapujNaDto(post, srednia, liczbaKomentarzy);
	}
}
