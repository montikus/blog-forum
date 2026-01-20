package com.example.blog.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResultDto {

	private int liczbaZaimportowanych;
	private int liczbaPominietych;
	private List<String> bledy = new ArrayList<>();

	public ImportResultDto() {
	}

	public ImportResultDto(int liczbaZaimportowanych, int liczbaPominietych, List<String> bledy) {
		this.liczbaZaimportowanych = liczbaZaimportowanych;
		this.liczbaPominietych = liczbaPominietych;
		if (bledy != null) {
			this.bledy = bledy;
		}
	}

	public int getLiczbaZaimportowanych() {
		return liczbaZaimportowanych;
	}

	public void setLiczbaZaimportowanych(int liczbaZaimportowanych) {
		this.liczbaZaimportowanych = liczbaZaimportowanych;
	}

	public int getLiczbaPominietych() {
		return liczbaPominietych;
	}

	public void setLiczbaPominietych(int liczbaPominietych) {
		this.liczbaPominietych = liczbaPominietych;
	}

	public List<String> getBledy() {
		return bledy;
	}

	public void setBledy(List<String> bledy) {
		this.bledy = bledy;
	}
}
