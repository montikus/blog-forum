package com.example.blog.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class StatystykiDao {

	private static final int DOMYSLNY_LIMIT = 5;

	private final JdbcTemplate szablonJdbc;

	public StatystykiDao(JdbcTemplate szablonJdbc) {
		this.szablonJdbc = szablonJdbc;
	}

	public List<Map<String, Object>> pobierzTopPosty() {
		String zapytanie = """
				SELECT p.tytul AS tytul,
					   COALESCE(AVG(o.wartosc), 0) AS srednia_ocena,
					   COUNT(DISTINCT k.id) AS liczba_komentarzy
				FROM posty p
				LEFT JOIN oceny o ON o.post_id = p.id
				LEFT JOIN komentarze k ON k.post_id = p.id
				GROUP BY p.id, p.tytul
				ORDER BY srednia_ocena DESC, liczba_komentarzy DESC, p.id ASC
				LIMIT ?
				""";
		return szablonJdbc.query(zapytanie, mapujTopPosty(), DOMYSLNY_LIMIT);
	}

	public List<Map<String, Object>> pobierzStatystykiUzytkownikow() {
		String zapytanie = """
				SELECT u.username AS nazwa_uzytkownika,
					   COUNT(DISTINCT pa.post_id) AS liczba_postow,
					   COUNT(DISTINCT k.id) AS liczba_komentarzy
				FROM uzytkownicy u
				LEFT JOIN post_autorzy pa ON pa.uzytkownik_id = u.id
				LEFT JOIN komentarze k ON k.autor_id = u.id
				GROUP BY u.id, u.username
				ORDER BY liczba_postow DESC, liczba_komentarzy DESC, u.username
				""";
		return szablonJdbc.query(zapytanie, mapujStatystykiUzytkownikow());
	}

	private RowMapper<Map<String, Object>> mapujTopPosty() {
		return (ResultSet wynik, int numerWiersza) -> {
			Map<String, Object> wiersz = new LinkedHashMap<>();
			wiersz.put("tytul", wynik.getString("tytul"));
			wiersz.put("srednia_ocena", wynik.getBigDecimal("srednia_ocena"));
			wiersz.put("liczba_komentarzy", wynik.getLong("liczba_komentarzy"));
			return wiersz;
		};
	}

	private RowMapper<Map<String, Object>> mapujStatystykiUzytkownikow() {
		return (ResultSet wynik, int numerWiersza) -> {
			Map<String, Object> wiersz = new LinkedHashMap<>();
			wiersz.put("nazwa_uzytkownika", wynik.getString("nazwa_uzytkownika"));
			wiersz.put("liczba_postow", wynik.getLong("liczba_postow"));
			wiersz.put("liczba_komentarzy", wynik.getLong("liczba_komentarzy"));
			return wiersz;
		};
	}
}
