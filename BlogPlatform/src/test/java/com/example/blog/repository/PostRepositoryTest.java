package com.example.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import java.util.LinkedHashSet;
import java.util.Set;
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
class PostRepositoryTest {

	@Autowired
	private PostRepository repozytoriumPostow;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Test
	void powinienZnalezcPostyPoAutorze() {
		User autor = utworzUzytkownika("autor", "autor@example.com");
		utworzPost("Pierwszy", "<p>A</p>", Set.of(autor));

		Page<Post> wynik = repozytoriumPostow.znajdzPoAutorachNazwieUzytkownika(
				"autor",
				PageRequest.of(0, 10)
		);

		assertThat(wynik.getContent()).hasSize(1);
		assertThat(wynik.getContent().getFirst().getTytul()).isEqualTo("Pierwszy");
	}

	@Test
	void powinienZwracacStronicowanePosty() {
		User autor = utworzUzytkownika("autor2", "autor2@example.com");
		utworzPost("A", "A", Set.of(autor));
		utworzPost("B", "B", Set.of(autor));
		utworzPost("C", "C", Set.of(autor));

		Page<Post> pierwsza = repozytoriumPostow.findAll(PageRequest.of(0, 2));
		Page<Post> druga = repozytoriumPostow.findAll(PageRequest.of(1, 2));

		assertThat(pierwsza.getTotalElements()).isEqualTo(3);
		assertThat(pierwsza.getContent()).hasSize(2);
		assertThat(druga.getContent()).hasSize(1);
	}

	private User utworzUzytkownika(String nazwa, String email) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(email);
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return repozytoriumUzytkownikow.save(uzytkownik);
	}

	private Post utworzPost(String tytul, String tresc, Set<User> autorzy) {
		Post post = new Post();
		post.setTytul(tytul);
		post.setTresc(tresc);
		post.setAutorzy(new LinkedHashSet<>(autorzy));
		return repozytoriumPostow.save(post);
	}
}
