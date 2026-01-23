package com.example.blog.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.dto.MessageDto;
import com.example.blog.model.Message;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MessageMapperTest {

	private final MessageMapper mapper = new MessageMapper();

	@Test
	void powinienMapowacWiadomoscNaDto() {
		Instant czas = Instant.parse("2024-03-03T14:30:00Z");
		User nadawca = utworzUzytkownika(1L, "nadawca");
		User odbiorca = utworzUzytkownika(2L, "odbiorca");
		Post post = new Post();
		post.setId(15L);
		Message wiadomosc = new Message();
		wiadomosc.setId(20L);
		wiadomosc.setTresc("Tresc");
		wiadomosc.setWyslanoDnia(czas);
		wiadomosc.setNadawca(nadawca);
		wiadomosc.setOdbiorca(odbiorca);
		wiadomosc.setPost(post);

		MessageDto dto = mapper.mapujNaDto(wiadomosc);

		assertThat(dto.getId()).isEqualTo(20L);
		assertThat(dto.getTresc()).isEqualTo("Tresc");
		assertThat(dto.getWyslanoDnia()).isEqualTo(czas);
		assertThat(dto.getPostId()).isEqualTo(15L);
		assertThat(dto.getNadawca()).isNotNull();
		assertThat(dto.getNadawca().getNazwaUzytkownika()).isEqualTo("nadawca");
		assertThat(dto.getOdbiorca()).isNotNull();
		assertThat(dto.getOdbiorca().getNazwaUzytkownika()).isEqualTo("odbiorca");
	}

	@Test
	void powinienMapowacWiadomoscBezPostaIUzytkownikow() {
		Message wiadomosc = new Message();
		wiadomosc.setId(21L);
		wiadomosc.setTresc("Brak relacji");
		wiadomosc.setPost(null);
		wiadomosc.setNadawca(null);
		wiadomosc.setOdbiorca(null);

		MessageDto dto = mapper.mapujNaDto(wiadomosc);

		assertThat(dto.getId()).isEqualTo(21L);
		assertThat(dto.getPostId()).isNull();
		assertThat(dto.getNadawca()).isNull();
		assertThat(dto.getOdbiorca()).isNull();
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
