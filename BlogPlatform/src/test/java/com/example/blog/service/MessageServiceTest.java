package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.blog.dao.PostRaportDao;
import com.example.blog.dto.MessageDto;
import com.example.blog.mapper.MessageMapper;
import com.example.blog.model.Message;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.MessageRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository repozytoriumWiadomosci;

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	@Mock
	private PostRepository repozytoriumPostow;

	@Mock
	private MessageMapper mapperWiadomosci;

	@Mock
	private PostRaportDao daoRaportow;

	private MessageService serwisWiadomosci;

	@BeforeEach
	void przygotujSerwis() {
		serwisWiadomosci = new MessageService(
				repozytoriumWiadomosci,
				repozytoriumUzytkownikow,
				repozytoriumPostow,
				mapperWiadomosci,
				daoRaportow
		);
	}

	@Test
	void powinienWysylacWiadomoscDoOdbiorcy() {
		User nadawca = utworzUzytkownika(1L, "nadawca");
		User odbiorca = utworzUzytkownika(2L, "odbiorca");
		Message wiadomosc = new Message();
		wiadomosc.setId(7L);
		MessageDto dto = new MessageDto();
		dto.setId(7L);
		dto.setTresc("Czesc");
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("odbiorca")).thenReturn(Optional.of(odbiorca));
		when(repozytoriumWiadomosci.save(any(Message.class))).thenReturn(wiadomosc);
		when(mapperWiadomosci.mapujNaDto(wiadomosc)).thenReturn(dto);

		MessageDto wynik = serwisWiadomosci.wyslijWiadomosc("odbiorca", "Czesc", nadawca, null);

		assertThat(wynik.getId()).isEqualTo(7L);
		assertThat(wynik.getTresc()).isEqualTo("Czesc");
	}

	@Test
	void powinienOznaczacWiadomosciJakoPrzeczytanePrzyInbox() {
		User odbiorca = utworzUzytkownika(5L, "odbiorca");
		Message wiadomosc = new Message();
		wiadomosc.setId(3L);
		MessageDto dto = new MessageDto();
		dto.setId(3L);
		Page<Message> strona = new PageImpl<>(List.of(wiadomosc));
		when(repozytoriumWiadomosci.znajdzPoOdbiorcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
				"odbiorca",
				PageRequest.of(0, 5)
		)).thenReturn(strona);
		when(mapperWiadomosci.mapujNaDto(wiadomosc)).thenReturn(dto);

		Page<MessageDto> wynik = serwisWiadomosci.pobierzOdebrane(odbiorca, PageRequest.of(0, 5));

		assertThat(wynik.getContent()).hasSize(1);
		verify(daoRaportow).oznaczWiadomosciJakoPrzeczytane(5L);
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
