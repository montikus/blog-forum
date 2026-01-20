package com.example.blog.service;

import com.example.blog.dto.UserDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class UserService {

	private final UserRepository repozytoriumUzytkownikow;

	public UserService(UserRepository repozytoriumUzytkownikow) {
		this.repozytoriumUzytkownikow = repozytoriumUzytkownikow;
	}

	@Transactional(readOnly = true)
	public User pobierzUzytkownika(Long idUzytkownika) {
		return repozytoriumUzytkownikow.findById(idUzytkownika)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono uzytkownika"));
	}

	@Transactional(readOnly = true)
	public User pobierzPoNazwieUzytkownika(String nazwaUzytkownika) {
		return repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika(nazwaUzytkownika)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono uzytkownika"));
	}

	@Transactional(readOnly = true)
	public List<UserDto> pobierzWszystkich() {
		return repozytoriumUzytkownikow.findAll().stream()
				.map(this::mapujNaDto)
				.collect(Collectors.toList());
	}

	public User zapiszUzytkownika(User uzytkownik) {
		return repozytoriumUzytkownikow.save(uzytkownik);
	}

	@Transactional(readOnly = true)
	public boolean czyIstniejeNazwaUzytkownika(String nazwaUzytkownika) {
		return repozytoriumUzytkownikow.istniejePoNazwieUzytkownika(nazwaUzytkownika);
	}

	@Transactional(readOnly = true)
	public boolean czyIstniejeAdresEmail(String adresEmail) {
		return repozytoriumUzytkownikow.istniejePoAdresieEmail(adresEmail);
	}

	public UserDto mapujNaDto(User uzytkownik) {
		if (uzytkownik == null) {
			return null;
		}
		return new UserDto(
				uzytkownik.getId(),
				uzytkownik.getNazwaUzytkownika(),
				uzytkownik.getAdresEmail(),
				uzytkownik.getRola()
		);
	}
}
