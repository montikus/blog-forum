package com.example.blog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "oceny", uniqueConstraints = {
		@UniqueConstraint(name = "oceny_post_uzytkownik_uk", columnNames = {"post_id", "uzytkownik_id"})
})
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "wartosc", nullable = false)
	private int wartosc;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "uzytkownik_id", nullable = false)
	private User uzytkownik;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getWartosc() {
		return wartosc;
	}

	public void setWartosc(int wartosc) {
		this.wartosc = wartosc;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public User getUzytkownik() {
		return uzytkownik;
	}

	public void setUzytkownik(User uzytkownik) {
		this.uzytkownik = uzytkownik;
	}
}
