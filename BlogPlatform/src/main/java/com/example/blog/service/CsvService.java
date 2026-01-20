package com.example.blog.service;

import com.example.blog.dto.ImportResultDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CsvService {

	private static final String[] NAGLOWKI_UZYTKOWNICY = {
			"nazwa_uzytkownika", "email", "haslo", "rola"
	};
	private static final String[] NAGLOWKI_POSTY = {
			"tytul", "tresc", "autorzy"
	};
	private static final String[] NAGLOWKI_EKSPORTU = {
			"id", "tytul", "tresc", "autorzy", "srednia_ocena", "liczba_komentarzy"
	};

	private final UserRepository repozytoriumUzytkownikow;
	private final PostRepository repozytoriumPostow;
	private final PostService serwisPostow;
	private final PasswordEncoder szyfratorHasel;

	public CsvService(
			UserRepository repozytoriumUzytkownikow,
			PostRepository repozytoriumPostow,
			PostService serwisPostow,
			PasswordEncoder szyfratorHasel
	) {
		this.repozytoriumUzytkownikow = repozytoriumUzytkownikow;
		this.repozytoriumPostow = repozytoriumPostow;
		this.serwisPostow = serwisPostow;
		this.szyfratorHasel = szyfratorHasel;
	}

	public ImportResultDto importujUzytkownikow(MultipartFile plik) {
		if (plik == null || plik.isEmpty()) {
			return new ImportResultDto(0, 0, List.of("Plik jest pusty"));
		}

		int liczbaZaimportowanych = 0;
		int liczbaPominietych = 0;
		List<String> bledy = new ArrayList<>();

		CSVFormat format = CSVFormat.DEFAULT.builder()
				.setHeader(NAGLOWKI_UZYTKOWNICY)
				.setSkipHeaderRecord(true)
				.setTrim(true)
				.setIgnoreEmptyLines(true)
				.build();

		try (Reader czytnik = new InputStreamReader(plik.getInputStream(), StandardCharsets.UTF_8);
				CSVParser parser = new CSVParser(czytnik, format)) {
			for (CSVRecord rekord : parser) {
				long numer = rekord.getRecordNumber();
				String nazwaUzytkownika = rekord.get("nazwa_uzytkownika");
				String adresEmail = rekord.get("email");
				String haslo = rekord.get("haslo");
				String rolaTekst = rekord.get("rola");

				if (czyPuste(nazwaUzytkownika) || czyPuste(adresEmail) || czyPuste(haslo)) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": brak wymaganych danych uzytkownika");
					continue;
				}

				if (repozytoriumUzytkownikow.istniejePoNazwieUzytkownika(nazwaUzytkownika)) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": nazwa uzytkownika juz istnieje");
					continue;
				}

				if (repozytoriumUzytkownikow.istniejePoAdresieEmail(adresEmail)) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": email juz istnieje");
					continue;
				}

				Role rola = Role.USER;
				if (!czyPuste(rolaTekst)) {
					try {
						rola = Role.valueOf(rolaTekst.trim().toUpperCase(Locale.ROOT));
					} catch (IllegalArgumentException wyjatek) {
						liczbaPominietych++;
						bledy.add("Wiersz " + numer + ": niepoprawna rola");
						continue;
					}
				}

				User uzytkownik = new User();
				uzytkownik.setNazwaUzytkownika(nazwaUzytkownika.trim());
				uzytkownik.setAdresEmail(adresEmail.trim());
				uzytkownik.setHasloHash(szyfratorHasel.encode(haslo));
				uzytkownik.setRola(rola);
				repozytoriumUzytkownikow.save(uzytkownik);
				liczbaZaimportowanych++;
			}
		} catch (IOException wyjatek) {
			bledy.add("Nie mozna odczytac pliku CSV");
		}

		return new ImportResultDto(liczbaZaimportowanych, liczbaPominietych, bledy);
	}

	public ImportResultDto importujPosty(MultipartFile plik) {
		if (plik == null || plik.isEmpty()) {
			return new ImportResultDto(0, 0, List.of("Plik jest pusty"));
		}

		int liczbaZaimportowanych = 0;
		int liczbaPominietych = 0;
		List<String> bledy = new ArrayList<>();

		CSVFormat format = CSVFormat.DEFAULT.builder()
				.setHeader(NAGLOWKI_POSTY)
				.setSkipHeaderRecord(true)
				.setTrim(true)
				.setIgnoreEmptyLines(true)
				.build();

		try (Reader czytnik = new InputStreamReader(plik.getInputStream(), StandardCharsets.UTF_8);
				CSVParser parser = new CSVParser(czytnik, format)) {
			for (CSVRecord rekord : parser) {
				long numer = rekord.getRecordNumber();
				String tytul = rekord.get("tytul");
				String tresc = rekord.get("tresc");
				String autorzyPole = rekord.get("autorzy");

				if (czyPuste(tytul) || czyPuste(tresc) || czyPuste(autorzyPole)) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": brak wymaganych danych posta");
					continue;
				}

				Set<String> nazwyAutorow = Arrays.stream(autorzyPole.split(";"))
						.map(String::trim)
						.filter(nazwa -> !nazwa.isBlank())
						.collect(Collectors.toCollection(LinkedHashSet::new));

				if (nazwyAutorow.isEmpty()) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": brak autorow");
					continue;
				}

				Set<User> autorzy = new LinkedHashSet<>();
				List<String> nieznani = new ArrayList<>();
				for (String nazwa : nazwyAutorow) {
					repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika(nazwa)
							.ifPresentOrElse(autorzy::add, () -> nieznani.add(nazwa));
				}

				if (!nieznani.isEmpty()) {
					liczbaPominietych++;
					bledy.add("Wiersz " + numer + ": brak uzytkownikow: " + String.join(", ", nieznani));
					continue;
				}

				Post post = new Post();
				post.setTytul(tytul.trim());
				post.setTresc(tresc.trim());
				post.setAutorzy(autorzy);
				repozytoriumPostow.save(post);
				liczbaZaimportowanych++;
			}
		} catch (IOException wyjatek) {
			bledy.add("Nie mozna odczytac pliku CSV");
		}

		return new ImportResultDto(liczbaZaimportowanych, liczbaPominietych, bledy);
	}

	@Transactional(readOnly = true)
	public byte[] eksportujPostyCsv() {
		List<PostDto> posty = serwisPostow.pobierzPostyDoEksportu();
		ByteArrayOutputStream strumienWyjscia = new ByteArrayOutputStream();

		CSVFormat format = CSVFormat.DEFAULT.builder()
				.setHeader(NAGLOWKI_EKSPORTU)
				.setTrim(true)
				.build();

		try (Writer pisarz = new OutputStreamWriter(strumienWyjscia, StandardCharsets.UTF_8);
				CSVPrinter drukarka = new CSVPrinter(pisarz, format)) {
			for (PostDto post : posty) {
				String autorzy = post.getAutorzy().stream()
						.map(UserDto::getNazwaUzytkownika)
						.collect(Collectors.joining(";"));
				drukarka.printRecord(
						post.getId(),
						post.getTytul(),
						post.getTresc(),
						autorzy,
						post.getSredniaOcena(),
						post.getLiczbaKomentarzy()
				);
			}
			drukarka.flush();
		} catch (IOException wyjatek) {
			throw new IllegalStateException("Nie mozna wygenerowac CSV");
		}

		return strumienWyjscia.toByteArray();
	}

	private boolean czyPuste(String wartosc) {
		return wartosc == null || wartosc.isBlank();
	}
}
