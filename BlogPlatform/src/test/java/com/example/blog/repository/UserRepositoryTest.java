package com.example.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.model.Role;
import com.example.blog.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(
		statements = {
				"delete from wiadomosci",
				"delete from oceny",
				"delete from komentarze",
				"delete from post_autorzy",
				"delete from posty",
				"delete from uzytkownicy"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class UserRepositoryTest {

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Test
	void powinienZnalezcPoNazwieUzytkownika() {
		User uzytkownik = utworzUzytkownika("jan_test", "jan_test@example.com");

		assertThat(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("jan_test"))
				.isPresent()
				.contains(uzytkownik);
	}

	@Test
	void powinienSprawdzacIstnienieNazwyUzytkownika() {
		utworzUzytkownika("anna_test", "anna_test@example.com");

		boolean wynik = repozytoriumUzytkownikow.istniejePoNazwieUzytkownika("anna_test");

		assertThat(wynik).isTrue();
	}

	@Test
	void powinienSprawdzacIstnienieEmaila() {
		utworzUzytkownika("ola_test", "ola_test@example.com");

		boolean wynik = repozytoriumUzytkownikow.istniejePoAdresieEmail("ola_test@example.com");

		assertThat(wynik).isTrue();
	}

	private User utworzUzytkownika(String nazwa, String email) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(email);
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return repozytoriumUzytkownikow.save(uzytkownik);
	}
}
