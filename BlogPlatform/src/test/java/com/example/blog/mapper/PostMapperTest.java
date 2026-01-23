package com.example.blog.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.PostFormDto;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class PostMapperTest {

	private final PostMapper mapper = new PostMapper();

	@Test
	void powinienMapowacPostNaDtoZRozszerzonymiPolami() {
		Instant teraz = Instant.parse("2024-01-01T12:00:00Z");
		User autor = utworzUzytkownika(1L, "autor");
		Post post = new Post();
		post.setId(10L);
		post.setTytul("Tytul");
		post.setTresc("Tresc");
		post.setUtworzonoDnia(teraz);
		post.setZaktualizowanoDnia(teraz.plusSeconds(60));
		post.setAutorzy(new LinkedHashSet<>(List.of(autor)));
		post.setKomentarze(List.of(new Comment(), new Comment()));

		PostDto dto = mapper.mapujNaDto(post, 4.25, 2);

		assertThat(dto.getId()).isEqualTo(10L);
		assertThat(dto.getTytul()).isEqualTo("Tytul");
		assertThat(dto.getTresc()).isEqualTo("Tresc");
		assertThat(dto.getUtworzonoDnia()).isEqualTo(teraz);
		assertThat(dto.getZaktualizowanoDnia()).isEqualTo(teraz.plusSeconds(60));
		assertThat(dto.getAutorzy()).hasSize(1);
		assertThat(dto.getAutorzy().getFirst().getNazwaUzytkownika()).isEqualTo("autor");
		assertThat(dto.getSredniaOcena()).isEqualTo(4.25);
		assertThat(dto.getLiczbaKomentarzy()).isEqualTo(2);
	}

	@Test
	void powinienMapowacPostNaDtoZDomyslnaLiczbaKomentarzy() {
		Post post = new Post();
		post.setId(12L);
		post.setTytul("Tytul2");
		post.setTresc("Tresc2");
		post.setAutorzy(null);
		post.setKomentarze(null);

		PostDto dto = mapper.mapujNaDto(post);

		assertThat(dto.getId()).isEqualTo(12L);
		assertThat(dto.getAutorzy()).isNotNull().isEmpty();
		assertThat(dto.getSredniaOcena()).isEqualTo(0.0);
		assertThat(dto.getLiczbaKomentarzy()).isEqualTo(0);
	}

	@Test
	void powinienMapowacAutoraNull() {
		Post post = new Post();
		post.setId(13L);
		post.setTytul("Tytul3");
		post.setTresc("Tresc3");
		LinkedHashSet<User> autorzy = new LinkedHashSet<>();
		autorzy.add(null);
		post.setAutorzy(autorzy);

		PostDto dto = mapper.mapujNaDto(post);

		assertThat(dto.getAutorzy()).hasSize(1);
		assertThat(dto.getAutorzy().getFirst()).isNull();
	}

	@Test
	void powinienMapowacFormularzNaEncje() {
		PostFormDto formularz = new PostFormDto();
		formularz.setTytul("Nowy");
		formularz.setTresc("Opis");

		Post post = mapper.mapujNaEncje(formularz);

		assertThat(post.getTytul()).isEqualTo("Nowy");
		assertThat(post.getTresc()).isEqualTo("Opis");
	}

	private User utworzUzytkownika(Long id, String nazwa) {
		User uzytkownik = new User();
		uzytkownik.setId(id);
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(nazwa + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return uzytkownik;
	}
}
