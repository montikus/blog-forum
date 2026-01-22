package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.blog.dto.ImportResultDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	@Mock
	private PostRepository repozytoriumPostow;

	@Mock
	private PostService serwisPostow;

	@Mock
	private PasswordEncoder szyfratorHasel;

	private CsvService serwisCsv;

	@BeforeEach
	void przygotujSerwis() {
		serwisCsv = new CsvService(repozytoriumUzytkownikow, repozytoriumPostow, serwisPostow, szyfratorHasel);
	}

	@Test
	void powinienImportowacUzytkownikowZCsv() {
		String tresc = "nazwa_uzytkownika,email,haslo,rola\n"
				+ "jan,jan@example.com,haslo,USER\n"
				+ "anna,anna@example.com,sekret,ADMIN\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.istniejePoNazwieUzytkownika(anyString())).thenReturn(false);
		when(repozytoriumUzytkownikow.istniejePoAdresieEmail(anyString())).thenReturn(false);
		when(szyfratorHasel.encode(anyString())).thenReturn("hash");
		when(repozytoriumUzytkownikow.save(any(User.class))).thenAnswer(wezwanie -> wezwanie.getArgument(0));

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(2);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(0);
		assertThat(wynik.getBledy()).isEmpty();
	}

	@Test
	void powinienZglaszacBrakAutoraPrzyImporciePostow() {
		String tresc = "tytul,tresc,autorzy\n"
				+ "Nowy post,Tresc,nieznany\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("nieznany")).thenReturn(Optional.empty());

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).isNotEmpty();
	}

	@Test
	void powinienOdrzucicPustyPlikCsv() {
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"pusty.csv",
				"text/csv",
				new byte[0]
		);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getBledy()).isNotEmpty();
	}

	@Test
	void powinienEksportowacPostyDoCsv() {
		PostDto post = new PostDto();
		post.setId(1L);
		post.setTytul("Tytul");
		post.setTresc("Tresc");
		post.setAutorzy(List.of(new UserDto(2L, "autor", "autor@example.com", Role.USER)));
		post.setSredniaOcena(4.0);
		post.setLiczbaKomentarzy(3);
		when(serwisPostow.pobierzPostyDoEksportu()).thenReturn(List.of(post));

		byte[] wynik = serwisCsv.eksportujPostyCsv();
		String tekst = new String(wynik, StandardCharsets.UTF_8);

		assertThat(tekst).contains("id,tytul,tresc,autorzy,srednia_ocena,liczba_komentarzy");
		assertThat(tekst).contains("Tytul");
	}
}
