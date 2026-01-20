package com.example.blog.service;

import com.example.blog.dao.StatystykiDao;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatystykiService {

	private final StatystykiDao daoStatystyk;

	public StatystykiService(StatystykiDao daoStatystyk) {
		this.daoStatystyk = daoStatystyk;
	}

	public List<Map<String, Object>> pobierzTopPosty() {
		return daoStatystyk.pobierzTopPosty();
	}

	public List<Map<String, Object>> pobierzStatystykiUzytkownikow() {
		return daoStatystyk.pobierzStatystykiUzytkownikow();
	}
}
