package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blog.model.Post;
import com.example.blog.model.Rating;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.model.Comment;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.MessageRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RatingRepository;
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

	@Autowired
	private CommentRepository repozytoriumKomentarzy;

	@Autowired
	private RatingRepository repozytoriumOcen;

	@Autowired
	private MessageRepository repozytoriumWiadomosci;

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

	@Test
	void powinienPobracKomentarzePosta() throws Exception {
		User autor = utworzUzytkownika("autor4", "autor4@example.com");
		Post post = utworzPost("Post4", "Tresc4", autor);
		Comment komentarz = new Comment();
		komentarz.setAutor(autor);
		komentarz.setPost(post);
		komentarz.setTresc("Komentarz z bazy");
		repozytoriumKomentarzy.save(komentarz);

		mockMvc.perform(get("/api/v1/posts/{idPosta}/comments", post.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].tresc").value("Komentarz z bazy"));
	}

	@Test
	void powinienPobracSredniaOcenePosta() throws Exception {
		User autor = utworzUzytkownika("autor5", "autor5@example.com");
		User oceniajacy = utworzUzytkownika("oceniajacy", "oceniajacy@example.com");
		Post post = utworzPost("Post5", "Tresc5", autor);
		Rating ocena = new Rating();
		ocena.setPost(post);
		ocena.setUzytkownik(oceniajacy);
		ocena.setWartosc(4);
		repozytoriumOcen.save(ocena);

		mockMvc.perform(get("/api/v1/posts/{idPosta}/ratings", post.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.postId").value(post.getId()))
				.andExpect(jsonPath("$.srednia").value(4.0));
	}

	@Test
	@WithMockUser(username = "jan_id")
	void powinienWyslacWiadomoscPoIdOdbiorcy() throws Exception {
		User nadawca = utworzUzytkownika("jan_id", "jan_id@example.com");
		User odbiorca = utworzUzytkownika("anna_id", "anna_id@example.com");
		String tresc = """
				{
				  "idOdbiorcy": %d,
				  "tresc": "Wiadomosc id"
				}
				""".formatted(odbiorca.getId());

		mockMvc.perform(post("/api/v1/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tresc").value("Wiadomosc id"))
				.andExpect(jsonPath("$.odbiorca.nazwaUzytkownika").value("anna_id"));
	}

	@Test
	@WithMockUser(username = "jan_error")
	void powinienZglaszacBladGdyBrakOdbiorcy() throws Exception {
		utworzUzytkownika("jan_error", "jan_error@example.com");
		String tresc = """
				{
				  "tresc": "Brak odbiorcy"
				}
				""";

		mockMvc.perform(post("/api/v1/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(tresc.getBytes(StandardCharsets.UTF_8)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.wiadomosc").value("Brak odbiorcy wiadomosci"));
	}

	@Test
	@WithMockUser(username = "jan_inbox")
	void powinienPobracInbox() throws Exception {
		User nadawca = utworzUzytkownika("nadawca_inbox", "nadawca_inbox@example.com");
		User odbiorca = utworzUzytkownika("jan_inbox", "jan_inbox@example.com");
		repozytoriumWiadomosci.save(utworzWiadomosc(nadawca, odbiorca, "Inbox msg"));

		mockMvc.perform(get("/api/v1/messages/inbox"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].tresc").value("Inbox msg"));
	}

	@Test
	@WithMockUser(username = "jan_sent")
	void powinienPobracWyslane() throws Exception {
		User nadawca = utworzUzytkownika("jan_sent", "jan_sent@example.com");
		User odbiorca = utworzUzytkownika("odbiorca_sent", "odbiorca_sent@example.com");
		repozytoriumWiadomosci.save(utworzWiadomosc(nadawca, odbiorca, "Sent msg"));

		mockMvc.perform(get("/api/v1/messages/sent"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].tresc").value("Sent msg"));
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

	private com.example.blog.model.Message utworzWiadomosc(User nadawca, User odbiorca, String tresc) {
		com.example.blog.model.Message wiadomosc = new com.example.blog.model.Message();
		wiadomosc.setNadawca(nadawca);
		wiadomosc.setOdbiorca(odbiorca);
		wiadomosc.setTresc(tresc);
		return wiadomosc;
	}
}
