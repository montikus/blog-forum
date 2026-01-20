package com.example.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import com.example.blog.model.Post;
import com.example.blog.model.Rating;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.util.LinkedHashSet;
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
class RatingRepositoryTest {

	@Autowired
	private RatingRepository repozytoriumOcen;

	@Autowired
	private PostRepository repozytoriumPostow;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Test
	void powinienZnalezcOcenePoPoiscieIUzytkowniku() {
		User uzytkownik = utworzUzytkownika("jan_test", "jan_test@example.com");
		Post post = utworzPost("Tytul", "Tresc", uzytkownik);
		Rating ocena = utworzOcene(post, uzytkownik, 4);

		assertThat(repozytoriumOcen.znajdzPoIdPostaIUzytkownika(post.getId(), uzytkownik.getId()))
				.isPresent()
				.contains(ocena);
	}

	@Test
	void powinienZwracacSredniaLubZero() {
		User uzytkownik = utworzUzytkownika("ola_test", "ola_test@example.com");
		Post post = utworzPost("Tytul2", "Tresc2", uzytkownik);

		double brakOcen = repozytoriumOcen.pobierzSredniaDlaPosta(post.getId());

		assertThat(brakOcen).isEqualTo(0.0);

		User drugi = utworzUzytkownika("kasia_test", "kasia_test@example.com");
		utworzOcene(post, uzytkownik, 2);
		utworzOcene(post, drugi, 4);

		double srednia = repozytoriumOcen.pobierzSredniaDlaPosta(post.getId());

		assertThat(srednia).isCloseTo(3.0, offset(0.01));
	}

	private User utworzUzytkownika(String nazwa, String email) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(email);
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return repozytoriumUzytkownikow.save(uzytkownik);
	}

	private Post utworzPost(String tytul, String tresc, User autor) {
		Post post = new Post();
		post.setTytul(tytul);
		post.setTresc(tresc);
		post.setAutorzy(new LinkedHashSet<>());
		post.getAutorzy().add(autor);
		return repozytoriumPostow.save(post);
	}

	private Rating utworzOcene(Post post, User uzytkownik, int wartosc) {
		Rating ocena = new Rating();
		ocena.setPost(post);
		ocena.setUzytkownik(uzytkownik);
		ocena.setWartosc(wartosc);
		return repozytoriumOcen.save(ocena);
	}
}
