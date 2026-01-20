package com.example.blog.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PostDto {

	private Long id;
	private String tytul;
	private String tresc;
	private Instant utworzonoDnia;
	private Instant zaktualizowanoDnia;
	private List<UserDto> autorzy = new ArrayList<>();
	private double sredniaOcena;
	private int liczbaKomentarzy;

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

	public List<UserDto> getAutorzy() {
		return autorzy;
	}

	public void setAutorzy(List<UserDto> autorzy) {
		this.autorzy = autorzy;
	}

	public double getSredniaOcena() {
		return sredniaOcena;
	}

	public void setSredniaOcena(double sredniaOcena) {
		this.sredniaOcena = sredniaOcena;
	}

	public int getLiczbaKomentarzy() {
		return liczbaKomentarzy;
	}

	public void setLiczbaKomentarzy(int liczbaKomentarzy) {
		this.liczbaKomentarzy = liczbaKomentarzy;
	}
}
