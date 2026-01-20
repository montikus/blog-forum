package com.example.blog.service;

import com.example.blog.dto.CommentDto;
import com.example.blog.exception.ForbiddenException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class CommentService {

	private final CommentRepository repozytoriumKomentarzy;
	private final PostRepository repozytoriumPostow;
	private final CommentMapper mapperKomentarzy;

	public CommentService(
			CommentRepository repozytoriumKomentarzy,
			PostRepository repozytoriumPostow,
			CommentMapper mapperKomentarzy
	) {
		this.repozytoriumKomentarzy = repozytoriumKomentarzy;
		this.repozytoriumPostow = repozytoriumPostow;
		this.mapperKomentarzy = mapperKomentarzy;
	}

	public CommentDto dodajKomentarz(Long idPosta, String tresc, User aktualnyUzytkownik) {
		Post post = repozytoriumPostow.findById(idPosta)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta"));
		Comment komentarz = new Comment();
		komentarz.setTresc(tresc);
		komentarz.setAutor(aktualnyUzytkownik);
		komentarz.setPost(post);
		return mapperKomentarzy.mapujNaDto(repozytoriumKomentarzy.save(komentarz));
	}

	public void usunKomentarz(Long idKomentarza, User aktualnyUzytkownik) {
		Comment komentarz = repozytoriumKomentarzy.findById(idKomentarza)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono komentarza"));
		boolean czyAdmin = aktualnyUzytkownik != null && aktualnyUzytkownik.getRola() == Role.ADMIN;
		boolean czyAutor = komentarz.getAutor() != null
				&& komentarz.getAutor().getId().equals(aktualnyUzytkownik.getId());
		if (!czyAdmin && !czyAutor) {
			throw new ForbiddenException("Brak uprawnien do komentarza");
		}
		repozytoriumKomentarzy.delete(komentarz);
	}

	@Transactional(readOnly = true)
	public List<CommentDto> pobierzKomentarzeDlaPosta(Long idPosta) {
		return repozytoriumKomentarzy.znajdzPoIdPosta(idPosta).stream()
				.map(mapperKomentarzy::mapujNaDto)
				.collect(Collectors.toList());
	}
}
