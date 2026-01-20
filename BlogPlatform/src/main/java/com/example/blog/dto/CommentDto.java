package com.example.blog.dto;

import java.time.Instant;

public class CommentDto {

	private Long id;
	private String tresc;
	private Instant utworzonoDnia;
	private UserDto autor;
	private Long postId;

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

	public UserDto getAutor() {
		return autor;
	}

	public void setAutor(UserDto autor) {
		this.autor = autor;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
}
