package com.example.blog.service;

import com.example.blog.exception.ForbiddenException;
import com.example.blog.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BiezacyUzytkownikService {

	private final UserService serwisUzytkownikow;

	public BiezacyUzytkownikService(UserService serwisUzytkownikow) {
		this.serwisUzytkownikow = serwisUzytkownikow;
	}

	public User pobierzAktualnegoUzytkownika() {
		Authentication uwierzytelnienie = SecurityContextHolder.getContext().getAuthentication();
		if (uwierzytelnienie == null
				|| !uwierzytelnienie.isAuthenticated()
				|| "anonymousUser".equals(uwierzytelnienie.getPrincipal())) {
			throw new ForbiddenException("Brak autoryzacji");
		}
		return serwisUzytkownikow.pobierzPoNazwieUzytkownika(uwierzytelnienie.getName());
	}
}
