package com.example.blog.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModelAccessorsTest {

	@Test
	void shouldHandlePostOcenyAccessors() {
		Post post = new Post();
		Rating rating = new Rating();
		List<Rating> oceny = List.of(rating);

		post.setOceny(oceny);

		assertThat(post.getOceny()).containsExactly(rating);
	}

	@Test
	void shouldHandleRatingRelationsAccessors() {
		Post post = new Post();
		User user = new User();
		Rating rating = new Rating();

		rating.setPost(post);
		rating.setUzytkownik(user);

		assertThat(rating.getPost()).isSameAs(post);
		assertThat(rating.getUzytkownik()).isSameAs(user);
	}

	@Test
	void shouldHandleUserSensitiveFieldsAccessors() {
		User user = new User();
		Instant teraz = Instant.parse("2024-05-01T10:15:30Z");

		user.setHasloHash("hash");
		user.setUtworzonoDnia(teraz);

		assertThat(user.getHasloHash()).isEqualTo("hash");
		assertThat(user.getUtworzonoDnia()).isEqualTo(teraz);
	}
}
