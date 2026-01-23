package com.example.blog.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.dto.CommentDto;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CommentMapperTest {

	private final CommentMapper mapper = new CommentMapper();

	@Test
	void powinienMapowacKomentarzNaDto() {
		Instant czas = Instant.parse("2024-02-02T10:00:00Z");
		User autor = utworzUzytkownika(2L, "autor");
		Post post = new Post();
		post.setId(7L);
		Comment komentarz = new Comment();
		komentarz.setId(5L);
		komentarz.setTresc("Komentarz");
		komentarz.setUtworzonoDnia(czas);
		komentarz.setPost(post);
		komentarz.setAutor(autor);

		CommentDto dto = mapper.mapujNaDto(komentarz);

		assertThat(dto.getId()).isEqualTo(5L);
		assertThat(dto.getTresc()).isEqualTo("Komentarz");
		assertThat(dto.getUtworzonoDnia()).isEqualTo(czas);
		assertThat(dto.getPostId()).isEqualTo(7L);
		assertThat(dto.getAutor()).isNotNull();
		assertThat(dto.getAutor().getNazwaUzytkownika()).isEqualTo("autor");
	}

	@Test
	void powinienMapowacKomentarzBezPostaIAutora() {
		Comment komentarz = new Comment();
		komentarz.setId(9L);
		komentarz.setTresc("Brak relacji");
		komentarz.setPost(null);
		komentarz.setAutor(null);

		CommentDto dto = mapper.mapujNaDto(komentarz);

		assertThat(dto.getId()).isEqualTo(9L);
		assertThat(dto.getPostId()).isNull();
		assertThat(dto.getAutor()).isNull();
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
