package com.example.blog.service;

import com.example.blog.dto.RatingDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.model.Rating;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class RatingService {

	private final RatingRepository repozytoriumOcen;
	private final PostRepository repozytoriumPostow;

	public RatingService(RatingRepository repozytoriumOcen, PostRepository repozytoriumPostow) {
		this.repozytoriumOcen = repozytoriumOcen;
		this.repozytoriumPostow = repozytoriumPostow;
	}

	public RatingDto ocenPost(Long idPosta, int wartosc, User uzytkownik) {
		if (wartosc < 1 || wartosc > 5) {
			throw new IllegalArgumentException("Ocena musi byc w zakresie 1-5");
		}
		Post post = repozytoriumPostow.findById(idPosta)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta"));
		Rating ocena = repozytoriumOcen.znajdzPoIdPostaIUzytkownika(idPosta, uzytkownik.getId())
				.orElseGet(() -> {
					Rating nowa = new Rating();
					nowa.setPost(post);
					nowa.setUzytkownik(uzytkownik);
					return nowa;
				});
		ocena.setWartosc(wartosc);
		Rating zapisana = repozytoriumOcen.save(ocena);
		double srednia = repozytoriumOcen.pobierzSredniaDlaPosta(idPosta);

		RatingDto dto = new RatingDto();
		dto.setId(zapisana.getId());
		dto.setWartosc(zapisana.getWartosc());
		dto.setPostId(idPosta);
		dto.setUzytkownikId(uzytkownik.getId());
		dto.setSrednia(srednia);
		return dto;
	}

	@Transactional(readOnly = true)
	public double pobierzSrednia(Long idPosta) {
		if (!repozytoriumPostow.existsById(idPosta)) {
			throw new ResourceNotFoundException("Nie znaleziono posta");
		}
		return repozytoriumOcen.pobierzSredniaDlaPosta(idPosta);
	}
}
