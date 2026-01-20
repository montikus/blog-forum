package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class PostFormDto {

	@NotBlank(message = "Tytul jest wymagany")
	private String tytul;

	@NotBlank(message = "Tresc jest wymagana")
	private String tresc;

	private List<Long> wspolautorzyId = new ArrayList<>();

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

	public List<Long> getWspolautorzyId() {
		return wspolautorzyId;
	}

	public void setWspolautorzyId(List<Long> wspolautorzyId) {
		this.wspolautorzyId = wspolautorzyId;
	}
}
