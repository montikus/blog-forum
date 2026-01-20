package com.example.blog.exception;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String wiadomosc) {
		super(wiadomosc);
	}
}
