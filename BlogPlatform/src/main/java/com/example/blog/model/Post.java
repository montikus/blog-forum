package com.example.blog.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posty")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tytul", nullable = false, length = 200)
	private String tytul;

	@Lob
	@Column(name = "tresc", nullable = false, columnDefinition = "TEXT")
	private String tresc;

	@Column(name = "created_at", nullable = false)
	private Instant utworzonoDnia;

	@Column(name = "updated_at", nullable = false)
	private Instant zaktualizowanoDnia;

	@ManyToMany
	@JoinTable(
			name = "post_autorzy",
			joinColumns = @JoinColumn(name = "post_id"),
			inverseJoinColumns = @JoinColumn(name = "uzytkownik_id")
	)
	private Set<User> autorzy = new LinkedHashSet<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> komentarze = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Rating> oceny = new ArrayList<>();

	@PrePersist
	void ustawDatyPrzyUtworzeniu() {
		Instant teraz = Instant.now();
		utworzonoDnia = teraz;
		zaktualizowanoDnia = teraz;
	}

	@PreUpdate
	void ustawDateAktualizacji() {
		zaktualizowanoDnia = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTytul() {
		return tytul;
	}

	public void setTytul(String tytul) {
		this.tytul = tytul;
	}

	public String getTresc() {
		return tresc;
	}

	public void setTresc(String tresc) {
		this.tresc = tresc;
	}

	public Instant getUtworzonoDnia() {
		return utworzonoDnia;
	}

	public void setUtworzonoDnia(Instant utworzonoDnia) {
		this.utworzonoDnia = utworzonoDnia;
	}

	public Instant getZaktualizowanoDnia() {
		return zaktualizowanoDnia;
	}

	public void setZaktualizowanoDnia(Instant zaktualizowanoDnia) {
		this.zaktualizowanoDnia = zaktualizowanoDnia;
	}

	public Set<User> getAutorzy() {
		return autorzy;
	}

	public void setAutorzy(Set<User> autorzy) {
		this.autorzy = autorzy;
	}

	public List<Comment> getKomentarze() {
		return komentarze;
	}

	public void setKomentarze(List<Comment> komentarze) {
		this.komentarze = komentarze;
	}

	public List<Rating> getOceny() {
		return oceny;
	}

	public void setOceny(List<Rating> oceny) {
		this.oceny = oceny;
	}
}
