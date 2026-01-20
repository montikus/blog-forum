package com.example.blog.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostRaportDao {

	private final JdbcTemplate szablonJdbc;

	public PostRaportDao(JdbcTemplate szablonJdbc) {
		this.szablonJdbc = szablonJdbc;
	}

	public int oznaczWiadomosciJakoPrzeczytane(Long idOdbiorcy) {
		String zapytanie = """
				UPDATE wiadomosci
				SET przeczytana = true
				WHERE odbiorca_id = ?
				  AND przeczytana = false
				""";
		return szablonJdbc.update(zapytanie, idOdbiorcy);
	}
}
