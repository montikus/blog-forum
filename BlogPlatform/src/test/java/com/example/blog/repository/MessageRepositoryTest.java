package com.example.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.model.Message;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
class MessageRepositoryTest {

	@Autowired
	private MessageRepository repozytoriumWiadomosci;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Test
	void powinienZwracacWiadomosciOdbiorcyWgDaty() {
		User nadawca = utworzUzytkownika("nadawca_test", "nadawca_test@example.com");
		User odbiorca = utworzUzytkownika("odbiorca_test", "odbiorca_test@example.com");

		Message starsza = utworzWiadomosc(nadawca, odbiorca, "Stara");
		starsza.setWyslanoDnia(Instant.now().minusSeconds(3600));
		repozytoriumWiadomosci.save(starsza);

		Message nowsza = utworzWiadomosc(nadawca, odbiorca, "Nowa");
		nowsza.setWyslanoDnia(Instant.now());
		repozytoriumWiadomosci.save(nowsza);

		Page<Message> wynik = repozytoriumWiadomosci
				.znajdzPoOdbiorcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
						"odbiorca_test",
						PageRequest.of(0, 10)
				);

		assertThat(wynik.getContent()).hasSize(2);
		assertThat(wynik.getContent().getFirst().getId()).isEqualTo(nowsza.getId());
	}

	@Test
	void powinienZwracacWiadomosciNadawcyWgDaty() {
		User nadawca = utworzUzytkownika("jan_test", "jan_test@example.com");
		User odbiorca = utworzUzytkownika("ola_test", "ola_test@example.com");

		Message pierwsza = utworzWiadomosc(nadawca, odbiorca, "Pierwsza");
		pierwsza.setWyslanoDnia(Instant.now().minusSeconds(7200));
		repozytoriumWiadomosci.save(pierwsza);

		Message druga = utworzWiadomosc(nadawca, odbiorca, "Druga");
		druga.setWyslanoDnia(Instant.now());
		repozytoriumWiadomosci.save(druga);

		Page<Message> wynik = repozytoriumWiadomosci
				.znajdzPoNadawcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
						"jan_test",
						PageRequest.of(0, 10)
				);

		assertThat(wynik.getContent()).hasSize(2);
		assertThat(wynik.getContent().getFirst().getId()).isEqualTo(druga.getId());
	}

	private User utworzUzytkownika(String nazwa, String email) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(email);
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return repozytoriumUzytkownikow.save(uzytkownik);
	}

	private Message utworzWiadomosc(User nadawca, User odbiorca, String tresc) {
		Message wiadomosc = new Message();
		wiadomosc.setNadawca(nadawca);
		wiadomosc.setOdbiorca(odbiorca);
		wiadomosc.setTresc(tresc);
		return repozytoriumWiadomosci.save(wiadomosc);
	}
}
