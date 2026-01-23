package com.example.blog;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class BlogApplicationMainTest {

	@Test
	void powinienUruchamiacMainBezWyjatku() {
		String poprzedniTyp = System.getProperty("spring.main.web-application-type");
		String poprzedniPort = System.getProperty("server.port");
		String poprzedniaInicjalizacja = System.getProperty("spring.main.lazy-initialization");
		System.setProperty("spring.main.web-application-type", "none");
		System.setProperty("server.port", "0");
		System.setProperty("spring.main.lazy-initialization", "true");

		try {
			assertThatCode(() -> BlogApplication.main(new String[] {}))
					.doesNotThrowAnyException();
		} finally {
			ustawLubWyczysc("spring.main.web-application-type", poprzedniTyp);
			ustawLubWyczysc("server.port", poprzedniPort);
			ustawLubWyczysc("spring.main.lazy-initialization", poprzedniaInicjalizacja);
		}
	}

	private void ustawLubWyczysc(String klucz, String wartosc) {
		if (wartosc == null) {
			System.clearProperty(klucz);
		} else {
			System.setProperty(klucz, wartosc);
		}
	}
}
