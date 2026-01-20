package com.example.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
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
class CommentRepositoryTest {

	@Autowired
	private CommentRepository repozytoriumKomentarzy;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Autowired
	private PostRepository repozytoriumPostow;

	@Test
	void powinienZapisywacKomentarze() {
		User autor = utworzUzytkownika("jan_test", "jan_test@example.com");
		Post post = utworzPost("Tytul", "Tresc", autor);
		Comment komentarz = new Comment();
		komentarz.setAutor(autor);
		komentarz.setPost(post);
		komentarz.setTresc("Komentarz");
		repozytoriumKomentarzy.save(komentarz);

		assertThat(repozytoriumKomentarzy.findAll()).hasSize(1);
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
}
