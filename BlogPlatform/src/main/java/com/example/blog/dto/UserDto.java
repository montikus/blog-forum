package com.example.blog.dto;

import com.example.blog.model.Role;

public class UserDto {

	private Long id;
	private String nazwaUzytkownika;
	private String adresEmail;
	private Role rola;

	public UserDto() {
	}

	public UserDto(Long id, String nazwaUzytkownika, String adresEmail, Role rola) {
		this.id = id;
		this.nazwaUzytkownika = nazwaUzytkownika;
		this.adresEmail = adresEmail;
		this.rola = rola;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNazwaUzytkownika() {
		return nazwaUzytkownika;
	}

	public void setNazwaUzytkownika(String nazwaUzytkownika) {
		this.nazwaUzytkownika = nazwaUzytkownika;
	}

	public String getAdresEmail() {
		return adresEmail;
	}

	public void setAdresEmail(String adresEmail) {
		this.adresEmail = adresEmail;
	}

	public Role getRola() {
		return rola;
	}

	public void setRola(Role rola) {
		this.rola = rola;
	}
}
