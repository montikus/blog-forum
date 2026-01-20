package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.blog.dto.RatingDto;
import com.example.blog.model.Post;
import com.example.blog.model.Rating;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

	@Mock
	private RatingRepository repozytoriumOcen;

	@Mock
	private PostRepository repozytoriumPostow;

	private RatingService serwisOcen;

	@BeforeEach
	void przygotujSerwis() {
		serwisOcen = new RatingService(repozytoriumOcen, repozytoriumPostow);
	}

	@Test
	void powinienAktualizowacIstniejacaOcene() {
		User uzytkownik = utworzUzytkownika(5L);
		Post post = utworzPost(9L);
		Rating ocena = new Rating();
		ocena.setId(7L);
		ocena.setPost(post);
		ocena.setUzytkownik(uzytkownik);
		ocena.setWartosc(2);

		when(repozytoriumPostow.findById(9L)).thenReturn(Optional.of(post));
		when(repozytoriumOcen.znajdzPoIdPostaIUzytkownika(9L, 5L)).thenReturn(Optional.of(ocena));
		when(repozytoriumOcen.save(any(Rating.class))).thenReturn(ocena);
		when(repozytoriumOcen.pobierzSredniaDlaPosta(9L)).thenReturn(4.0);

		RatingDto wynik = serwisOcen.ocenPost(9L, 5, uzytkownik);

		assertThat(wynik.getId()).isEqualTo(7L);
		assertThat(wynik.getWartosc()).isEqualTo(5);
		assertThat(wynik.getSrednia()).isEqualTo(4.0);
		assertThat(wynik.getUzytkownikId()).isEqualTo(5L);
		assertThat(ocena.getWartosc()).isEqualTo(5);
	}

	@Test
	void powinienOdrzucicNiepoprawnaOcene() {
		User uzytkownik = utworzUzytkownika(3L);

		assertThatThrownBy(() -> serwisOcen.ocenPost(1L, 7, uzytkownik))
				.isInstanceOf(IllegalArgumentException.class);

		verifyNoInteractions(repozytoriumOcen);
		verifyNoInteractions(repozytoriumPostow);
	}

	private User utworzUzytkownika(Long id) {
		User uzytkownik = new User();
		uzytkownik.setId(id);
		uzytkownik.setNazwaUzytkownika("uzytkownik" + id);
		uzytkownik.setAdresEmail("user" + id + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return uzytkownik;
	}

	private Post utworzPost(Long id) {
		Post post = new Post();
		post.setId(id);
		post.setTytul("Tytul");
		post.setTresc("Tresc");
		return post;
	}
}
