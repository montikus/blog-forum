package com.example.blog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "komentarze")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tresc", nullable = false, columnDefinition = "TEXT")
	private String tresc;

	@Column(name = "created_at", nullable = false)
	private Instant utworzonoDnia;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "autor_id", nullable = false)
	private User autor;

	@PrePersist
	void ustawDateUtworzenia() {
		utworzonoDnia = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public User getAutor() {
		return autor;
	}

	public void setAutor(User autor) {
		this.autor = autor;
	}
}
