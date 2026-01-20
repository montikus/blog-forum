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
@Table(name = "wiadomosci")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tresc", nullable = false, columnDefinition = "TEXT")
	private String tresc;

	@Column(name = "sent_at", nullable = false)
	private Instant wyslanoDnia;

	@ManyToOne(optional = false)
	@JoinColumn(name = "nadawca_id", nullable = false)
	private User nadawca;

	@ManyToOne(optional = false)
	@JoinColumn(name = "odbiorca_id", nullable = false)
	private User odbiorca;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	@PrePersist
	void ustawDateWyslania() {
		wyslanoDnia = Instant.now();
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

	public Instant getWyslanoDnia() {
		return wyslanoDnia;
	}

	public void setWyslanoDnia(Instant wyslanoDnia) {
		this.wyslanoDnia = wyslanoDnia;
	}

	public User getNadawca() {
		return nadawca;
	}

	public void setNadawca(User nadawca) {
		this.nadawca = nadawca;
	}

	public User getOdbiorca() {
		return odbiorca;
	}

	public void setOdbiorca(User odbiorca) {
		this.odbiorca = odbiorca;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}
