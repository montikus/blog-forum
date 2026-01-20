package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.blog.dao.StatystykiDao;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatystykiServiceTest {

	@Mock
	private StatystykiDao daoStatystyk;

	private StatystykiService serwisStatystyk;

	@BeforeEach
	void przygotujSerwis() {
		serwisStatystyk = new StatystykiService(daoStatystyk);
	}

	@Test
	void powinienPobieracTopPosty() {
		when(daoStatystyk.pobierzTopPosty()).thenReturn(List.of(Map.of("tytul", "Test")));

		List<Map<String, Object>> wynik = serwisStatystyk.pobierzTopPosty();

		assertThat(wynik).hasSize(1);
	}

	@Test
	void powinienPobieracStatystykiUzytkownikow() {
		when(daoStatystyk.pobierzStatystykiUzytkownikow())
				.thenReturn(List.of(Map.of("nazwa_uzytkownika", "jan_test")));

		List<Map<String, Object>> wynik = serwisStatystyk.pobierzStatystykiUzytkownikow();

		assertThat(wynik).hasSize(1);
	}
}
