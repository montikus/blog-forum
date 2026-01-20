package com.example.blog.dto;

import java.time.Instant;

public class MessageDto {

	private Long id;
	private String tresc;
	private Instant wyslanoDnia;
	private UserDto nadawca;
	private UserDto odbiorca;
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

	public Instant getWyslanoDnia() {
		return wyslanoDnia;
	}

	public void setWyslanoDnia(Instant wyslanoDnia) {
		this.wyslanoDnia = wyslanoDnia;
	}

	public UserDto getNadawca() {
		return nadawca;
	}

	public void setNadawca(UserDto nadawca) {
		this.nadawca = nadawca;
	}

	public UserDto getOdbiorca() {
		return odbiorca;
	}

	public void setOdbiorca(UserDto odbiorca) {
		this.odbiorca = odbiorca;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
}
