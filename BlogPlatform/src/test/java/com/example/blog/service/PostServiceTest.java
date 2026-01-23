package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	private PostRepository repozytoriumPostow;

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	@Mock
	private RatingRepository repozytoriumOcen;

	@Mock
	private PostMapper mapperPostow;

	private PostService serwisPostow;

	@BeforeEach
	void przygotujSerwis() {
		serwisPostow = new PostService(repozytoriumPostow, repozytoriumUzytkownikow, repozytoriumOcen, mapperPostow);
	}

	@Test
	void powinienTworzycPostIZachowacAutora() {
		User autor = utworzUzytkownika(1L, Role.USER);
		PostFormDto formularz = new PostFormDto();
		formularz.setTytul("Tytul");
		formularz.setTresc("Tresc");
		Post post = new Post();
		post.setId(11L);
		post.setAutorzy(new LinkedHashSet<>());
		PostDto dto = new PostDto();
		dto.setId(11L);

		when(mapperPostow.mapujNaEncje(formularz)).thenReturn(post);
		when(repozytoriumPostow.save(post)).thenReturn(post);
		when(repozytoriumOcen.pobierzSredniaDlaPosta(11L)).thenReturn(0.0);
		when(mapperPostow.mapujNaDto(post, 0.0, 0)).thenReturn(dto);

		PostDto wynik = serwisPostow.utworzPost(formularz, autor);

		assertThat(post.getAutorzy()).contains(autor);
		assertThat(wynik.getId()).isEqualTo(11L);
	}

	@Test
	void powinienZabraniacUsunieciaDlaNieautora() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User inny = utworzUzytkownika(2L, Role.USER);
		Post post = utworzPost(10L, autor);
		when(repozytoriumPostow.findById(10L)).thenReturn(Optional.of(post));

		assertThatThrownBy(() -> serwisPostow.usunPost(10L, inny))
				.isInstanceOf(ForbiddenException.class);

		verify(repozytoriumPostow, never()).delete(any(Post.class));
	}

	@Test
	void powinienPozwalacUsunacGdyAdmin() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User admin = utworzUzytkownika(2L, Role.ADMIN);
		Post post = utworzPost(20L, autor);
		when(repozytoriumPostow.findById(20L)).thenReturn(Optional.of(post));

		serwisPostow.usunPost(20L, admin);

		verify(repozytoriumPostow).delete(post);
	}

	@Test
	void powinienDodacAktualnegoAutoraGdyListaPusta() {
		User autor = utworzUzytkownika(5L, Role.USER);
		Post post = utworzPost(30L, autor);
		when(repozytoriumPostow.findById(30L)).thenReturn(Optional.of(post));
		when(repozytoriumUzytkownikow.findAllById(List.of())).thenReturn(List.of());

		serwisPostow.ustawWspolautorow(30L, List.of(), autor);

		assertThat(post.getAutorzy()).hasSize(1);
		assertThat(post.getAutorzy()).contains(autor);
	}

	@Test
	void powinienDodacAktualnegoAutoraGdyListaNull() {
		User autor = utworzUzytkownika(6L, Role.USER);
		Post post = utworzPost(31L, autor);
		when(repozytoriumPostow.findById(31L)).thenReturn(Optional.of(post));
		when(repozytoriumUzytkownikow.findAllById(List.of())).thenReturn(List.of());

		serwisPostow.ustawWspolautorow(31L, null, autor);

		assertThat(post.getAutorzy()).containsExactly(autor);
	}

	@Test
	void powinienEdytowacPostDlaAutora() {
		User autor = utworzUzytkownika(1L, Role.USER);
		Post post = utworzPost(12L, autor);
		PostFormDto formularz = new PostFormDto();
		formularz.setTytul("Nowy tytul");
		formularz.setTresc("Nowa tresc");
		PostDto dto = new PostDto();
		dto.setId(12L);

		when(repozytoriumPostow.findById(12L)).thenReturn(Optional.of(post));
		when(repozytoriumOcen.pobierzSredniaDlaPosta(12L)).thenReturn(2.5);
		when(mapperPostow.mapujNaDto(post, 2.5, 0)).thenReturn(dto);

		PostDto wynik = serwisPostow.edytujPost(12L, formularz, autor);

		assertThat(wynik.getId()).isEqualTo(12L);
		assertThat(post.getTytul()).isEqualTo("Nowy tytul");
		assertThat(post.getTresc()).isEqualTo("Nowa tresc");
	}

	@Test
	void powinienRzucicBladGdyNieZnalezionoWspolautorow() {
		User autor = utworzUzytkownika(1L, Role.USER);
		Post post = utworzPost(44L, autor);
		when(repozytoriumPostow.findById(44L)).thenReturn(Optional.of(post));
		when(repozytoriumUzytkownikow.findAllById(List.of(9L))).thenReturn(List.of());

		assertThatThrownBy(() -> serwisPostow.ustawWspolautorow(44L, List.of(9L), autor))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void powinienWyszukiwacPostyPoSpecyfikacji() {
		User autor = utworzUzytkownika(3L, Role.USER);
		Post post = utworzPost(21L, autor);
		Page<Post> strona = new PageImpl<>(List.of(post));
		when(repozytoriumPostow.findAll(any(Specification.class), any(Pageable.class))).thenReturn(strona);
		when(repozytoriumOcen.pobierzSredniaDlaPosta(21L)).thenReturn(0.0);
		when(mapperPostow.mapujNaDto(post, 0.0, 0)).thenReturn(new PostDto());

		Page<PostDto> wynik = serwisPostow.wyszukajPosty("test", "autor3", PageRequest.of(0, 10));

		assertThat(wynik.getContent()).hasSize(1);
	}

	@Test
	void powinienZwracacMojePosty() {
		User autor = utworzUzytkownika(7L, Role.USER);
		Post post = utworzPost(70L, autor);
		Page<Post> strona = new PageImpl<>(List.of(post));
		when(repozytoriumPostow.znajdzPoAutorachNazwieUzytkownika(eq(autor.getNazwaUzytkownika()), any(Pageable.class)))
				.thenReturn(strona);
		when(repozytoriumOcen.pobierzSredniaDlaPosta(70L)).thenReturn(3.0);
		when(mapperPostow.mapujNaDto(post, 3.0, 0)).thenReturn(new PostDto());

		Page<PostDto> wynik = serwisPostow.znajdzMojePosty(autor, PageRequest.of(0, 5));

		assertThat(wynik.getContent()).hasSize(1);
	}

	@Test
	void powinienZwracacFormularzDoEdycji() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User wspolautor = utworzUzytkownika(2L, Role.USER);
		Post post = utworzPost(40L, autor);
		post.getAutorzy().add(wspolautor);
		when(repozytoriumPostow.findById(40L)).thenReturn(Optional.of(post));

		PostFormDto formularz = serwisPostow.pobierzFormularzDoEdycji(40L, autor);

		assertThat(formularz.getTytul()).isEqualTo(post.getTytul());
		assertThat(formularz.getTresc()).isEqualTo(post.getTresc());
		assertThat(formularz.getWspolautorzyId()).containsExactlyInAnyOrder(1L, 2L);
	}

	@Test
	void powinienPobracPostyDoEksportu() {
		User autor = utworzUzytkownika(8L, Role.USER);
		Post post = utworzPost(80L, autor);
		when(repozytoriumPostow.findAll()).thenReturn(List.of(post));
		when(repozytoriumOcen.pobierzSredniaDlaPosta(80L)).thenReturn(4.0);
		PostDto dto = new PostDto();
		dto.setId(80L);
		when(mapperPostow.mapujNaDto(post, 4.0, 0)).thenReturn(dto);

		List<PostDto> wynik = serwisPostow.pobierzPostyDoEksportu();

		assertThat(wynik).hasSize(1);
		assertThat(wynik.getFirst().getId()).isEqualTo(80L);
	}

	@Test
	void powinienUstawiacWspolautorowGdyListaNiepusta() {
		User autor = utworzUzytkownika(1L, Role.USER);
		User wspolautor = utworzUzytkownika(2L, Role.USER);
		Post post = utworzPost(50L, autor);
		when(repozytoriumPostow.findById(50L)).thenReturn(Optional.of(post));
		when(repozytoriumUzytkownikow.findAllById(List.of(2L))).thenReturn(List.of(wspolautor));

		serwisPostow.ustawWspolautorow(50L, List.of(2L), autor);

		assertThat(post.getAutorzy()).containsExactly(wspolautor);
	}

	@Test
	void powinienMapowacPostBezKomentarzyGdyNull() {
		User autor = utworzUzytkownika(1L, Role.USER);
		Post post = utworzPost(60L, autor);
		post.setKomentarze(null);
		PostDto dto = new PostDto();
		dto.setId(60L);
		when(repozytoriumPostow.findById(60L)).thenReturn(Optional.of(post));
		when(repozytoriumOcen.pobierzSredniaDlaPosta(60L)).thenReturn(1.0);
		when(mapperPostow.mapujNaDto(post, 1.0, 0)).thenReturn(dto);

		PostDto wynik = serwisPostow.pobierzPost(60L);

		assertThat(wynik.getId()).isEqualTo(60L);
	}

	@Test
	void powinienRzucicBladGdyPostNieIstnieje() {
		when(repozytoriumPostow.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> serwisPostow.pobierzPost(99L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	private User utworzUzytkownika(Long id, Role rola) {
		User uzytkownik = new User();
		uzytkownik.setId(id);
		uzytkownik.setNazwaUzytkownika("uzytkownik" + id);
		uzytkownik.setAdresEmail("user" + id + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(rola);
		return uzytkownik;
	}

	private Post utworzPost(Long id, User autor) {
		Post post = new Post();
		post.setId(id);
		post.setTytul("Tytul");
		post.setTresc("Tresc");
		post.setAutorzy(new LinkedHashSet<>());
		post.getAutorzy().add(autor);
		return post;
	}
}
