package com.example.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<BladOdpowiedzi> obsluzBrakZasobu(
			ResourceNotFoundException wyjatek,
			HttpServletRequest zadanie
	) {
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.NOT_FOUND, wyjatek.getMessage(), zadanie.getRequestURI(), null),
				HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<BladOdpowiedzi> obsluzBrakDostepu(
			ForbiddenException wyjatek,
			HttpServletRequest zadanie
	) {
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.FORBIDDEN, wyjatek.getMessage(), zadanie.getRequestURI(), null),
				HttpStatus.FORBIDDEN
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BladOdpowiedzi> obsluzWalidacje(
			MethodArgumentNotValidException wyjatek,
			HttpServletRequest zadanie
	) {
		Map<String, String> bledyPol = new LinkedHashMap<>();
		for (FieldError bladPola : wyjatek.getBindingResult().getFieldErrors()) {
			bledyPol.put(bladPola.getField(), bladPola.getDefaultMessage());
		}
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.BAD_REQUEST, "Blad walidacji", zadanie.getRequestURI(), bledyPol),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<BladOdpowiedzi> obsluzNiepoprawneDane(
			IllegalArgumentException wyjatek,
			HttpServletRequest zadanie
	) {
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.BAD_REQUEST, wyjatek.getMessage(), zadanie.getRequestURI(), null),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<BladOdpowiedzi> obsluzBrakZasobuStatycznego(
			NoResourceFoundException wyjatek,
			HttpServletRequest zadanie
	) {
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.NOT_FOUND, "Nie znaleziono zasobu statycznego", zadanie.getRequestURI(), null),
				HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BladOdpowiedzi> obsluzWyjatek(
			Exception wyjatek,
			HttpServletRequest zadanie
	) {
		return new ResponseEntity<>(
				utworzBlad(HttpStatus.INTERNAL_SERVER_ERROR, "Blad serwera", zadanie.getRequestURI(), null),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	private BladOdpowiedzi utworzBlad(
			HttpStatus status,
			String wiadomosc,
			String sciezka,
			Map<String, String> pola
	) {
		return new BladOdpowiedzi(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				wiadomosc,
				sciezka,
				pola
		);
	}

	public record BladOdpowiedzi(
			Instant czas,
			int status,
			String blad,
			String wiadomosc,
			String sciezka,
			Map<String, String> pola
	) {
	}
}
