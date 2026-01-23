package com.example.blog.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.model.Role;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DtoTest {

	@Test
	void shouldHandlePostDtoFields() {
		PostDto dto = new PostDto();
		assertThat(dto.getAutorzy()).isNotNull().isEmpty();

		Instant teraz = Instant.parse("2024-01-01T00:00:00Z");
		UserDto autor = new UserDto(1L, "author", "author@example.com", Role.USER);
		List<UserDto> autorzy = new ArrayList<>();
		autorzy.add(autor);

		dto.setId(10L);
		dto.setTytul("Title");
		dto.setTresc("Content");
		dto.setUtworzonoDnia(teraz);
		dto.setZaktualizowanoDnia(teraz.plusSeconds(60));
		dto.setAutorzy(autorzy);
		dto.setSredniaOcena(4.5);
		dto.setLiczbaKomentarzy(3);

		assertThat(dto.getId()).isEqualTo(10L);
		assertThat(dto.getTytul()).isEqualTo("Title");
		assertThat(dto.getTresc()).isEqualTo("Content");
		assertThat(dto.getUtworzonoDnia()).isEqualTo(teraz);
		assertThat(dto.getZaktualizowanoDnia()).isEqualTo(teraz.plusSeconds(60));
		assertThat(dto.getAutorzy()).containsExactly(autor);
		assertThat(dto.getSredniaOcena()).isEqualTo(4.5);
		assertThat(dto.getLiczbaKomentarzy()).isEqualTo(3);
	}

	@Test
	void shouldHandleMessageDtoFields() {
		MessageDto dto = new MessageDto();
		Instant wyslano = Instant.parse("2024-02-02T10:15:30Z");
		UserDto nadawca = new UserDto(2L, "sender", "sender@example.com", Role.USER);
		UserDto odbiorca = new UserDto(3L, "receiver", "receiver@example.com", Role.USER);

		dto.setId(5L);
		dto.setTresc("Hello");
		dto.setWyslanoDnia(wyslano);
		dto.setNadawca(nadawca);
		dto.setOdbiorca(odbiorca);
		dto.setPostId(99L);

		assertThat(dto.getId()).isEqualTo(5L);
		assertThat(dto.getTresc()).isEqualTo("Hello");
		assertThat(dto.getWyslanoDnia()).isEqualTo(wyslano);
		assertThat(dto.getNadawca()).isEqualTo(nadawca);
		assertThat(dto.getOdbiorca()).isEqualTo(odbiorca);
		assertThat(dto.getPostId()).isEqualTo(99L);
	}

	@Test
	void shouldHandleImportResultDtoConstructors() {
		ImportResultDto domyslny = new ImportResultDto(1, 2, null);
		assertThat(domyslny.getLiczbaZaimportowanych()).isEqualTo(1);
		assertThat(domyslny.getLiczbaPominietych()).isEqualTo(2);
		assertThat(domyslny.getBledy()).isNotNull().isEmpty();

		List<String> bledy = List.of("Error A", "Error B");
		ImportResultDto zBledami = new ImportResultDto(3, 4, bledy);
		assertThat(zBledami.getBledy()).containsExactlyElementsOf(bledy);
	}

	@Test
	void shouldHandleImportResultDtoSetters() {
		ImportResultDto dto = new ImportResultDto();
		List<String> bledy = new ArrayList<>();
		bledy.add("blad");

		dto.setLiczbaZaimportowanych(5);
		dto.setLiczbaPominietych(6);
		dto.setBledy(bledy);

		assertThat(dto.getLiczbaZaimportowanych()).isEqualTo(5);
		assertThat(dto.getLiczbaPominietych()).isEqualTo(6);
		assertThat(dto.getBledy()).containsExactly("blad");
	}

	@Test
	void shouldHandleCommentDtoFields() {
		CommentDto dto = new CommentDto();
		Instant czas = Instant.parse("2024-03-01T12:00:00Z");
		UserDto autor = new UserDto(7L, "commenter", "commenter@example.com", Role.USER);

		dto.setId(8L);
		dto.setTresc("Comment");
		dto.setUtworzonoDnia(czas);
		dto.setAutor(autor);
		dto.setPostId(100L);

		assertThat(dto.getId()).isEqualTo(8L);
		assertThat(dto.getTresc()).isEqualTo("Comment");
		assertThat(dto.getUtworzonoDnia()).isEqualTo(czas);
		assertThat(dto.getAutor()).isEqualTo(autor);
		assertThat(dto.getPostId()).isEqualTo(100L);
	}

	@Test
	void shouldHandleRatingDtoFields() {
		RatingDto dto = new RatingDto();
		dto.setId(11L);
		dto.setWartosc(4);
		dto.setPostId(12L);
		dto.setUzytkownikId(13L);
		dto.setSrednia(4.2);

		assertThat(dto.getId()).isEqualTo(11L);
		assertThat(dto.getWartosc()).isEqualTo(4);
		assertThat(dto.getPostId()).isEqualTo(12L);
		assertThat(dto.getUzytkownikId()).isEqualTo(13L);
		assertThat(dto.getSrednia()).isEqualTo(4.2);
	}

	@Test
	void shouldHandlePostFormDtoFields() {
		PostFormDto dto = new PostFormDto();
		assertThat(dto.getWspolautorzyId()).isNotNull().isEmpty();

		dto.setTytul("Form title");
		dto.setTresc("Form content");
		dto.setWspolautorzyId(List.of(1L, 2L));

		assertThat(dto.getTytul()).isEqualTo("Form title");
		assertThat(dto.getTresc()).isEqualTo("Form content");
		assertThat(dto.getWspolautorzyId()).containsExactly(1L, 2L);
	}

	@Test
	void shouldHandleUserDtoConstructorsAndAccessors() {
		UserDto dto = new UserDto(20L, "user", "user@example.com", Role.ADMIN);

		assertThat(dto.getId()).isEqualTo(20L);
		assertThat(dto.getNazwaUzytkownika()).isEqualTo("user");
		assertThat(dto.getAdresEmail()).isEqualTo("user@example.com");
		assertThat(dto.getRola()).isEqualTo(Role.ADMIN);

		dto.setId(21L);
		dto.setNazwaUzytkownika("user2");
		dto.setAdresEmail("user2@example.com");
		dto.setRola(Role.USER);

		assertThat(dto.getId()).isEqualTo(21L);
		assertThat(dto.getNazwaUzytkownika()).isEqualTo("user2");
		assertThat(dto.getAdresEmail()).isEqualTo("user2@example.com");
		assertThat(dto.getRola()).isEqualTo(Role.USER);
	}

	@Test
	void shouldHandleUserDtoDefaultConstructor() {
		UserDto dto = new UserDto();
		dto.setId(30L);
		dto.setNazwaUzytkownika("nowy");
		dto.setAdresEmail("nowy@example.com");
		dto.setRola(Role.USER);

		assertThat(dto.getId()).isEqualTo(30L);
		assertThat(dto.getNazwaUzytkownika()).isEqualTo("nowy");
		assertThat(dto.getAdresEmail()).isEqualTo("nowy@example.com");
		assertThat(dto.getRola()).isEqualTo(Role.USER);
	}
}
