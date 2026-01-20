package com.example.blog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "uzytkownicy", uniqueConstraints = {
		@UniqueConstraint(name = "uzytkownicy_username_uk", columnNames = "username"),
		@UniqueConstraint(name = "uzytkownicy_email_uk", columnNames = "email")
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, length = 100)
	private String nazwaUzytkownika;

	@Column(name = "email", nullable = false, length = 255)
	private String adresEmail;

	@Column(name = "haslo_hash", nullable = false, length = 255)
	private String hasloHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "rola", nullable = false, length = 20)
	private Role rola;

	@Column(name = "created_at", nullable = false)
	private Instant utworzonoDnia;

	@PrePersist
	void ustawDomyslneDaty() {
		utworzonoDnia = Instant.now();
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

	public String getHasloHash() {
		return hasloHash;
	}

	public void setHasloHash(String hasloHash) {
		this.hasloHash = hasloHash;
	}

	public Role getRola() {
		return rola;
	}

	public void setRola(Role rola) {
		this.rola = rola;
	}

	public Instant getUtworzonoDnia() {
		return utworzonoDnia;
	}

	public void setUtworzonoDnia(Instant utworzonoDnia) {
		this.utworzonoDnia = utworzonoDnia;
	}
}
