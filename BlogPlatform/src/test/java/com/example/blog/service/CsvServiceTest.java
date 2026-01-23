package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.Mockito;

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
	void powinienOdrzucacBrakNaglowkowUzytkownicy() {
		String tresc = "nazwa_uzytkownika,email,haslo\n"
				+ "jan,jan@example.com,haslo\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getBledy()).contains("Brak wymaganych naglowkow CSV");
	}

	@Test
	void powinienPominacUzytkownikaZGdyBrakWymaganychPol() {
		String tresc = "nazwa_uzytkownika,email,haslo,rola\n"
				+ ",jan@example.com,haslo,USER\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("brak wymaganych danych uzytkownika"));
	}

	@Test
	void powinienPominacUzytkownikaZDublowanaNazwa() {
		String tresc = "nazwa_uzytkownika,email,haslo,rola\n"
				+ "jan,jan@example.com,haslo,USER\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.istniejePoNazwieUzytkownika("jan")).thenReturn(true);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("nazwa uzytkownika juz istnieje"));
		verify(repozytoriumUzytkownikow, never()).save(any(User.class));
	}

	@Test
	void powinienPominacUzytkownikaZDublowanyEmail() {
		String tresc = "nazwa_uzytkownika,email,haslo,rola\n"
				+ "jan,jan@example.com,haslo,USER\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.istniejePoNazwieUzytkownika("jan")).thenReturn(false);
		when(repozytoriumUzytkownikow.istniejePoAdresieEmail("jan@example.com")).thenReturn(true);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("email juz istnieje"));
		verify(repozytoriumUzytkownikow, never()).save(any(User.class));
	}

	@Test
	void powinienPominacUzytkownikaZNiepoprawnaRola() {
		String tresc = "nazwa_uzytkownika,email,haslo,rola\n"
				+ "jan,jan@example.com,haslo,INVALID\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"uzytkownicy.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.istniejePoNazwieUzytkownika(anyString())).thenReturn(false);
		when(repozytoriumUzytkownikow.istniejePoAdresieEmail(anyString())).thenReturn(false);

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("niepoprawna rola"));
	}

	@Test
	void powinienZglaszacBrakAutoraPrzyImporciePostow() {
		String tresc = "id,tytul,tresc,autorzy,srednia_ocena,liczba_komentarzy\n"
				+ "1,Nowy post,Tresc,nieznany,0,0\n";
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
	void powinienOdrzucacBrakNaglowkowPosty() {
		String tresc = "tytul,tresc\n"
				+ "Nowy post,Tresc\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getBledy()).contains("Brak wymaganych naglowkow CSV");
	}

	@Test
	void powinienPominacPostZGdyBrakWymaganychPol() {
		String tresc = "tytul,tresc,autorzy\n"
				+ ",Tresc,jan\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("brak wymaganych danych posta"));
	}

	@Test
	void powinienPominacPostGdyBrakAutorow() {
		String tresc = "tytul,tresc,autorzy\n"
				+ "Post,Tresc, ; \n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("brak autorow"));
	}

	@Test
	void powinienPominacPostGdyNieZnajdzieWspolautorow() {
		String tresc = "tytul,tresc,autorzy\n"
				+ "Post,Tresc,jan;ola\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("jan"))
				.thenReturn(Optional.of(utworzUzytkownika("jan")));
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("ola"))
				.thenReturn(Optional.empty());

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getLiczbaPominietych()).isEqualTo(1);
		assertThat(wynik.getBledy()).anyMatch(blad -> blad.contains("brak uzytkownikow: ola"));
	}

	@Test
	void powinienImportowacPostZGdyWszyscyAutorzyIstnieja() {
		String tresc = "tytul,tresc,autorzy\n"
				+ "Post,Tresc,jan;ola\n";
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posty.csv",
				"text/csv",
				tresc.getBytes(StandardCharsets.UTF_8)
		);
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("jan"))
				.thenReturn(Optional.of(utworzUzytkownika("jan")));
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("ola"))
				.thenReturn(Optional.of(utworzUzytkownika("ola")));
		ArgumentCaptor<com.example.blog.model.Post> captor = ArgumentCaptor.forClass(com.example.blog.model.Post.class);

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(1);
		verify(repozytoriumPostow, times(1)).save(captor.capture());
		assertThat(captor.getValue().getAutorzy()).hasSize(2);
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
	void powinienZglaszacBladGdyNieDaSieOdczytacPlikuUzytkownicy() throws Exception {
		MultipartFile plik = Mockito.mock(MultipartFile.class);
		when(plik.isEmpty()).thenReturn(false);
		when(plik.getInputStream()).thenThrow(new IOException("boom"));

		ImportResultDto wynik = serwisCsv.importujUzytkownikow(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getBledy()).contains("Nie mozna odczytac pliku CSV");
	}

	@Test
	void powinienZglaszacBladGdyNieDaSieOdczytacPlikuPosty() throws Exception {
		MultipartFile plik = Mockito.mock(MultipartFile.class);
		when(plik.isEmpty()).thenReturn(false);
		when(plik.getInputStream()).thenThrow(new IOException("boom"));

		ImportResultDto wynik = serwisCsv.importujPosty(plik);

		assertThat(wynik.getLiczbaZaimportowanych()).isEqualTo(0);
		assertThat(wynik.getBledy()).contains("Nie mozna odczytac pliku CSV");
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

	@Test
	void powinienEksportowacUzytkownikowDoCsv() {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika("jan");
		uzytkownik.setAdresEmail("jan@example.com");
		uzytkownik.setRola(Role.USER);
		when(repozytoriumUzytkownikow.findAll()).thenReturn(List.of(uzytkownik));

		byte[] wynik = serwisCsv.eksportujUzytkownikowCsv();
		String tekst = new String(wynik, StandardCharsets.UTF_8);

		assertThat(tekst).contains("nazwa_uzytkownika,email,haslo,rola");
		assertThat(tekst).contains("jan,jan@example.com,,USER");
	}

	private User utworzUzytkownika(String nazwa) {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika(nazwa);
		uzytkownik.setAdresEmail(nazwa + "@example.com");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.USER);
		return uzytkownik;
	}
}
