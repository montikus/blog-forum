package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.blog.dto.UserDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	private UserService serwisUzytkownikow;

	@BeforeEach
	void przygotujSerwis() {
		serwisUzytkownikow = new UserService(repozytoriumUzytkownikow);
	}

	@Test
	void powinienPobieracPoNazwieUzytkownika() {
		User uzytkownik = utworzUzytkownika(1L, "jan_test");
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("jan_test"))
				.thenReturn(Optional.of(uzytkownik));

		User wynik = serwisUzytkownikow.pobierzPoNazwieUzytkownika("jan_test");

		assertThat(wynik.getId()).isEqualTo(1L);
	}

	@Test
	void powinienRzucacBladGdyBrakUzytkownika() {
		when(repozytoriumUzytkownikow.findById(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> serwisUzytkownikow.pobierzUzytkownika(5L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void powinienMapowacNaDto() {
		User uzytkownik = utworzUzytkownika(3L, "ola_test");

		UserDto dto = serwisUzytkownikow.mapujNaDto(uzytkownik);

		assertThat(dto.getId()).isEqualTo(3L);
		assertThat(dto.getNazwaUzytkownika()).isEqualTo("ola_test");
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
