package com.example.blog.dto;

public class RatingDto {

	private Long id;
	private int wartosc;
	private Long postId;
	private Long uzytkownikId;
	private double srednia;

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

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getUzytkownikId() {
		return uzytkownikId;
	}

	public void setUzytkownikId(Long uzytkownikId) {
		this.uzytkownikId = uzytkownikId;
	}

	public double getSrednia() {
		return srednia;
	}

	public void setSrednia(double srednia) {
		this.srednia = srednia;
	}
}
