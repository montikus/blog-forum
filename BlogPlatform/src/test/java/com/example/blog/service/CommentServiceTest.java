package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private CommentRepository repozytoriumKomentarzy;

	@Mock
	private PostRepository repozytoriumPostow;

	@Mock
	private CommentMapper mapperKomentarzy;

	private CommentService serwisKomentarzy;

	@BeforeEach
	void przygotujSerwis() {
		serwisKomentarzy = new CommentService(repozytoriumKomentarzy, repozytoriumPostow, mapperKomentarzy);
	}

	@Test
	void powinienDodacKomentarzDoPosta() {
		User uzytkownik = utworzUzytkownika(1L, Role.USER);
		Post post = utworzPost(2L);
		Comment komentarz = new Comment();
		komentarz.setId(5L);
		CommentDto dto = new CommentDto();
		dto.setId(5L);
		dto.setTresc("OK");

		when(repozytoriumPostow.findById(2L)).thenReturn(Optional.of(post));
		when(repozytoriumKomentarzy.save(any(Comment.class))).thenReturn(komentarz);
		when(mapperKomentarzy.mapujNaDto(komentarz)).thenReturn(dto);

		CommentDto wynik = serwisKomentarzy.dodajKomentarz(2L, "OK", uzytkownik);

		assertThat(wynik.getId()).isEqualTo(5L);
		assertThat(wynik.getTresc()).isEqualTo("OK");
	}

	@Test
	void powinienBlokowacUsuwanieDlaObcegoUzytkownika() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User inny = utworzUzytkownika(2L, Role.USER);
		Comment komentarz = new Comment();
		komentarz.setId(9L);
		komentarz.setAutor(autor);
		when(repozytoriumKomentarzy.findById(9L)).thenReturn(Optional.of(komentarz));

		assertThatThrownBy(() -> serwisKomentarzy.usunKomentarz(9L, inny))
				.isInstanceOf(ForbiddenException.class);

		verify(repozytoriumKomentarzy, never()).delete(any(Comment.class));
	}

	@Test
	void powinienPozwalacUsunacKomentarzAdministratorowi() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User admin = utworzUzytkownika(2L, Role.ADMIN);
		Comment komentarz = new Comment();
		komentarz.setId(7L);
		komentarz.setAutor(autor);
		when(repozytoriumKomentarzy.findById(7L)).thenReturn(Optional.of(komentarz));

		serwisKomentarzy.usunKomentarz(7L, admin);

		verify(repozytoriumKomentarzy).delete(komentarz);
	}

	@Test
	void powinienPobracKomentarzeDlaPosta() {
		Comment komentarz = new Comment();
		komentarz.setId(3L);
		CommentDto dto = new CommentDto();
		dto.setId(3L);
		when(repozytoriumKomentarzy.znajdzPoIdPosta(11L)).thenReturn(List.of(komentarz));
		when(mapperKomentarzy.mapujNaDto(komentarz)).thenReturn(dto);

		List<CommentDto> wynik = serwisKomentarzy.pobierzKomentarzeDlaPosta(11L);

		assertThat(wynik).hasSize(1);
		assertThat(wynik.getFirst().getId()).isEqualTo(3L);
	}

	@Test
	void powinienPozwalacUsunacKomentarzAutorowi() {
		User autor = utworzUzytkownika(1L, Role.USER);
		Comment komentarz = new Comment();
		komentarz.setId(4L);
		komentarz.setAutor(autor);
		when(repozytoriumKomentarzy.findById(4L)).thenReturn(Optional.of(komentarz));

		serwisKomentarzy.usunKomentarz(4L, autor);

		verify(repozytoriumKomentarzy).delete(komentarz);
	}

	@Test
	void powinienRzucicBladGdyPostNieIstniejePrzyDodawaniu() {
		User autor = utworzUzytkownika(1L, Role.USER);
		when(repozytoriumPostow.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> serwisKomentarzy.dodajKomentarz(99L, "Test", autor))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void powinienRzucicBladGdyKomentarzNieIstnieje() {
		User autor = utworzUzytkownika(1L, Role.USER);
		when(repozytoriumKomentarzy.findById(123L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> serwisKomentarzy.usunKomentarz(123L, autor))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	private User utworzUzytkownika(Long id, Role rola) {
		User uzytkownik = new User();
		uzytkownik.setId(id);
		uzytkownik.setNazwaUzytkownika("user" + id);
		uzytkownik.setAdresEmail("user" + id + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(rola);
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
