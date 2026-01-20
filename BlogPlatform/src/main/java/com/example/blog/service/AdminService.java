package com.example.blog.service;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.PostMapper;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
import com.example.blog.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class AdminService {

	private final UserRepository repozytoriumUzytkownikow;
	private final PostRepository repozytoriumPostow;
	private final RatingRepository repozytoriumOcen;
	private final PostMapper mapperPostow;

	public AdminService(
			UserRepository repozytoriumUzytkownikow,
			PostRepository repozytoriumPostow,
			RatingRepository repozytoriumOcen,
			PostMapper mapperPostow
	) {
		this.repozytoriumUzytkownikow = repozytoriumUzytkownikow;
		this.repozytoriumPostow = repozytoriumPostow;
		this.repozytoriumOcen = repozytoriumOcen;
		this.mapperPostow = mapperPostow;
	}

	@Transactional(readOnly = true)
	public List<UserDto> pobierzUzytkownikow() {
		return repozytoriumUzytkownikow.findAll().stream()
				.map(this::mapujUzytkownika)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PostDto> pobierzPosty() {
		return repozytoriumPostow.findAll().stream()
				.map(this::mapujPost)
				.collect(Collectors.toList());
	}

	public void usunUzytkownika(Long idUzytkownika) {
		User uzytkownik = repozytoriumUzytkownikow.findById(idUzytkownika)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono uzytkownika"));
		repozytoriumUzytkownikow.delete(uzytkownik);
	}

	public void usunPost(Long idPosta) {
		Post post = repozytoriumPostow.findById(idPosta)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta"));
		repozytoriumPostow.delete(post);
	}

	private UserDto mapujUzytkownika(User uzytkownik) {
		return new UserDto(
				uzytkownik.getId(),
				uzytkownik.getNazwaUzytkownika(),
				uzytkownik.getAdresEmail(),
				uzytkownik.getRola()
		);
	}

	private PostDto mapujPost(Post post) {
		double srednia = repozytoriumOcen.pobierzSredniaDlaPosta(post.getId());
		int liczbaKomentarzy = post.getKomentarze() != null ? post.getKomentarze().size() : 0;
		return mapperPostow.mapujNaDto(post, srednia, liczbaKomentarzy);
	}
}
