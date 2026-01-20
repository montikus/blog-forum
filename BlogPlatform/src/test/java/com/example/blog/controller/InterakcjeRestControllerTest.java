package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
class InterakcjeRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Autowired
	private PostRepository repozytoriumPostow;

	@Autowired
	private PasswordEncoder szyfratorHasel;

	@Test
	@WithMockUser(username = "jan_test")
	void powinienDodacKomentarzDoPosta() throws Exception {
		User autor = utworzUzytkownika("autor", "autor@example.com");
		utworzUzytkownika("jan_test", "jan_test@example.com");
		Post post = utworzPost("Post", "Tresc", autor);
		String tresc = """
				{
				  "tresc": "Komentarz testowy"
				}
				""";

		mockMvc.perform(post("/api/v1/posts/{idPosta}/comments", post.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.tresc").value("Komentarz testowy"));
	}

	@Test
	@WithMockUser(username = "jan_test")
	void powinienOceniacPost() throws Exception {
		User autor = utworzUzytkownika("autor2", "autor2@example.com");
		utworzUzytkownika("jan_test", "jan_test@example.com");
		Post post = utworzPost("Post2", "Tresc2", autor);
		String tresc = """
				{
				  "wartosc": 4
				}
				""";

		mockMvc.perform(post("/api/v1/posts/{idPosta}/ratings", post.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.wartosc").value(4));
	}

	@Test
	@WithMockUser(username = "jan_test")
	void powinienWyslacWiadomosc() throws Exception {
		User autor = utworzUzytkownika("autor3", "autor3@example.com");
		utworzUzytkownika("jan_test", "jan_test@example.com");
		utworzUzytkownika("anna_test", "anna_test@example.com");
		Post post = utworzPost("Post3", "Tresc3", autor);
		String tresc = """
				{
				  "nazwaOdbiorcy": "anna_test",
				  "tresc": "Wiadomosc testowa",
				  "idPosta": %d
				}
				""".formatted(post.getId());

		mockMvc.perform(post("/api/v1/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tresc").value("Wiadomosc testowa"))
				.andExpect(jsonPath("$.odbiorca.nazwaUzytkownika").value("anna_test"));
	}

	private User utworzUzytkownika(String nazwa, String email) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(email);
		uzytkownik.setHasloHash(szyfratorHasel.encode("haslo"));
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
