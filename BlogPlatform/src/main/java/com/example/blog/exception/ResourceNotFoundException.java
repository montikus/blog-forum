package com.example.blog.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String wiadomosc) {
		super(wiadomosc);
	}
}
