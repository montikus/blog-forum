package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.blog.exception.ForbiddenException;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BiezacyUzytkownikServiceTest {

	@Mock
	private UserService serwisUzytkownikow;

	private BiezacyUzytkownikService serwisBiezacegoUzytkownika;

	@BeforeEach
	void przygotujSerwis() {
		serwisBiezacegoUzytkownika = new BiezacyUzytkownikService(serwisUzytkownikow);
	}

	@AfterEach
	void wyczyscKontekst() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void powinienPobracAktualnegoUzytkownika() {
		User uzytkownik = utworzUzytkownika(1L, "jan_test");
		when(serwisUzytkownikow.pobierzPoNazwieUzytkownika("jan_test")).thenReturn(uzytkownik);
		UsernamePasswordAuthenticationToken uwierzytelnienie =
				new UsernamePasswordAuthenticationToken("jan_test", "haslo", java.util.List.of());
		SecurityContextHolder.getContext().setAuthentication(uwierzytelnienie);

		User wynik = serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika();

		assertThat(wynik.getId()).isEqualTo(1L);
	}

	@Test
	void powinienOdrzucacBrakAutoryzacji() {
		SecurityContextHolder.clearContext();

		assertThatThrownBy(() -> serwisBiezacegoUzytkownika.pobierzAktualnegoUzytkownika())
				.isInstanceOf(ForbiddenException.class);
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
