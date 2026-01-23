package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.PostMapper;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
import com.example.blog.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	@Mock
	private PostRepository repozytoriumPostow;

	@Mock
	private RatingRepository repozytoriumOcen;

	@Mock
	private PostMapper mapperPostow;

	private AdminService serwisAdmina;

	@BeforeEach
	void przygotujSerwis() {
		serwisAdmina = new AdminService(repozytoriumUzytkownikow, repozytoriumPostow, repozytoriumOcen, mapperPostow);
	}

	@Test
	void powinienPobracUzytkownikow() {
		User uzytkownik = utworzUzytkownika(1L, "admin");
		when(repozytoriumUzytkownikow.findAll()).thenReturn(List.of(uzytkownik));

		List<UserDto> wynik = serwisAdmina.pobierzUzytkownikow();

		assertThat(wynik).hasSize(1);
		assertThat(wynik.getFirst().getNazwaUzytkownika()).isEqualTo("admin");
	}

	@Test
	void powinienPobracPosty() {
		Post post = utworzPost(10L, "Tytul");
		when(repozytoriumPostow.findAll()).thenReturn(List.of(post));
		when(repozytoriumOcen.pobierzSredniaDlaPosta(10L)).thenReturn(3.5);
		when(mapperPostow.mapujNaDto(post, 3.5, 0)).thenReturn(new PostDto());

		List<PostDto> wynik = serwisAdmina.pobierzPosty();

		assertThat(wynik).hasSize(1);
	}

	@Test
	void powinienUsunacUzytkownika() {
		User uzytkownik = utworzUzytkownika(2L, "jan");
		when(repozytoriumUzytkownikow.findById(2L)).thenReturn(java.util.Optional.of(uzytkownik));

		serwisAdmina.usunUzytkownika(2L);

		verify(repozytoriumUzytkownikow).delete(uzytkownik);
	}

	@Test
	void powinienUsunacPost() {
		Post post = utworzPost(5L, "Post");
		when(repozytoriumPostow.findById(5L)).thenReturn(java.util.Optional.of(post));

		serwisAdmina.usunPost(5L);

		verify(repozytoriumPostow).delete(post);
	}

	@Test
	void powinienRzucicBladGdyBrakUzytkownika() {
		when(repozytoriumUzytkownikow.findById(9L)).thenReturn(java.util.Optional.empty());

		assertThatThrownBy(() -> serwisAdmina.usunUzytkownika(9L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void powinienRzucicBladGdyBrakPosta() {
		when(repozytoriumPostow.findById(8L)).thenReturn(java.util.Optional.empty());

		assertThatThrownBy(() -> serwisAdmina.usunPost(8L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void powinienMapowacPostyGdyKomentarzeNull() {
		Post post = utworzPost(12L, "Null comments");
		post.setKomentarze(null);
		when(repozytoriumPostow.findAll()).thenReturn(List.of(post));
		when(repozytoriumOcen.pobierzSredniaDlaPosta(12L)).thenReturn(1.5);
		when(mapperPostow.mapujNaDto(post, 1.5, 0)).thenReturn(new PostDto());

		List<PostDto> wynik = serwisAdmina.pobierzPosty();

		assertThat(wynik).hasSize(1);
	}

	private User utworzUzytkownika(Long id, String nazwa) {
		User uzytkownik = new User();
		uzytkownik.setId(id);
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(nazwa + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.ADMIN);
		return uzytkownik;
	}

	private Post utworzPost(Long id, String tytul) {
		Post post = new Post();
		post.setId(id);
		post.setTytul(tytul);
		post.setTresc("Tresc");
		return post;
	}
}
