package com.example.blog.controller.api;

import com.example.blog.dto.ImportResultDto;
import com.example.blog.service.AdminService;
import com.example.blog.service.CsvService;
import com.example.blog.service.PdfService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

	private final AdminService serwisAdmina;
	private final CsvService serwisCsv;
	private final PdfService serwisPdf;

	public AdminRestController(AdminService serwisAdmina, CsvService serwisCsv, PdfService serwisPdf) {
		this.serwisAdmina = serwisAdmina;
		this.serwisCsv = serwisCsv;
		this.serwisPdf = serwisPdf;
	}

	@DeleteMapping("/users/{idUzytkownika}")
	public ResponseEntity<Void> usunUzytkownika(@PathVariable Long idUzytkownika) {
		serwisAdmina.usunUzytkownika(idUzytkownika);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/posts/{idPosta}")
	public ResponseEntity<Void> usunPost(@PathVariable Long idPosta) {
		serwisAdmina.usunPost(idPosta);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(path = "/import/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ImportResultDto importujUzytkownikow(@RequestPart("plik") @NotNull MultipartFile plik) {
		return serwisCsv.importujUzytkownikow(plik);
	}

	@PostMapping(path = "/import/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ImportResultDto importujPosty(@RequestPart("plik") @NotNull MultipartFile plik) {
		return serwisCsv.importujPosty(plik);
	}

	@GetMapping("/export/posts/csv")
	public ResponseEntity<byte[]> eksportujPostyCsv() {
		byte[] dane = serwisCsv.eksportujPostyCsv();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=posty.csv")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(dane);
	}

	@GetMapping("/export/users/csv")
	public ResponseEntity<byte[]> eksportujUzytkownikowCsv() {
		byte[] dane = serwisCsv.eksportujUzytkownikowCsv();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=uzytkownicy.csv")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(dane);
	}

	@GetMapping("/export/posts/pdf")
	public ResponseEntity<byte[]> eksportujPostyPdf() {
		byte[] dane = serwisPdf.eksportujListePostowPdf();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=posty.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(dane);
	}

	@GetMapping("/export/posts/{idPosta}/pdf")
	public ResponseEntity<byte[]> eksportujPostPdf(@PathVariable Long idPosta) {
		byte[] dane = serwisPdf.eksportujPostPdf(idPosta);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=post-" + idPosta + ".pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(dane);
	}
}
