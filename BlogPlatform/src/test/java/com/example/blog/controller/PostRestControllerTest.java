package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class PostRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository repozytoriumUzytkownikow;

	@Autowired
	private PostRepository repozytoriumPostow;

	@Autowired
	private PasswordEncoder szyfratorHasel;

	@Test
	void powinienZwrocicListePostowPublicznie() throws Exception {
		User autor = utworzUzytkownika("autor", "autor@example.com");
		utworzPost("Publiczny", "Tresc", autor);

		mockMvc.perform(get("/api/v1/posts"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray());
	}

	@Test
	void powinienZwrocicPostPoId() throws Exception {
		User autor = utworzUzytkownika("autor2", "autor2@example.com");
		Post post = utworzPost("Szczegoly", "Tresc", autor);

		mockMvc.perform(get("/api/v1/posts/{idPosta}", post.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(post.getId()))
				.andExpect(jsonPath("$.tytul").value("Szczegoly"));
	}

	@Test
	@WithMockUser(username = "jan_test")
	void powinienUtworzycPostGdyZalogowany() throws Exception {
		utworzUzytkownika("jan_test", "jan_test@example.com");
		String tresc = """
				{
				  "tytul": "Nowy post",
				  "tresc": "<p>Tresc</p>",
				  "wspolautorzyId": []
				}
				""";

		mockMvc.perform(post("/api/v1/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.tytul").value("Nowy post"));
	}

	@Test
	@WithMockUser(username = "autor_update")
	void powinienAktualizowacPost() throws Exception {
		User autor = utworzUzytkownika("autor_update", "autor_update@example.com");
		Post post = utworzPost("Stary tytul", "Stara tresc", autor);
		String tresc = """
				{
				  "tytul": "Nowy tytul",
				  "tresc": "Nowa tresc",
				  "wspolautorzyId": []
				}
				""";

		mockMvc.perform(put("/api/v1/posts/{idPosta}", post.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tytul").value("Nowy tytul"));
	}

	@Test
	@WithMockUser(username = "autor_delete")
	void powinienUsuwacPost() throws Exception {
		User autor = utworzUzytkownika("autor_delete", "autor_delete@example.com");
		Post post = utworzPost("Do usuniecia", "Tresc", autor);

		mockMvc.perform(delete("/api/v1/posts/{idPosta}", post.getId()))
				.andExpect(status().isNoContent());
	}

	@Test
	void powinienWyszukacPosty() throws Exception {
		User autor = utworzUzytkownika("search_author", "search_author@example.com");
		utworzPost("Searchable title", "Other content", autor);

		mockMvc.perform(get("/api/v1/posts/search")
						.param("query", "Searchable"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].tytul").value("Searchable title"));
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
