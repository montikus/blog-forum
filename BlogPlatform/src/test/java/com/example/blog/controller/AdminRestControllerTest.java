package com.example.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blog.dto.ImportResultDto;
import com.example.blog.service.AdminService;
import com.example.blog.service.CsvService;
import com.example.blog.service.PdfService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class AdminRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdminService serwisAdmina;

	@MockBean
	private CsvService serwisCsv;

	@MockBean
	private PdfService serwisPdf;

	@Test
	void powinienUsuwacUzytkownika() throws Exception {
		mockMvc.perform(delete("/api/v1/admin/users/{idUzytkownika}", 5L))
				.andExpect(status().isNoContent());

		verify(serwisAdmina).usunUzytkownika(5L);
	}

	@Test
	void powinienUsuwacPosta() throws Exception {
		mockMvc.perform(delete("/api/v1/admin/posts/{idPosta}", 7L))
				.andExpect(status().isNoContent());

		verify(serwisAdmina).usunPost(7L);
	}

	@Test
	void powinienImportowacUzytkownikow() throws Exception {
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"users.csv",
				"text/csv",
				"nazwa_uzytkownika,email,haslo,rola\n".getBytes(StandardCharsets.UTF_8)
		);
		when(serwisCsv.importujUzytkownikow(any())).thenReturn(new ImportResultDto(1, 0, List.of()));

		mockMvc.perform(multipart("/api/v1/admin/import/users").file(plik))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.liczbaZaimportowanych").value(1))
				.andExpect(jsonPath("$.liczbaPominietych").value(0));
	}

	@Test
	void powinienImportowacPosty() throws Exception {
		MockMultipartFile plik = new MockMultipartFile(
				"plik",
				"posts.csv",
				"text/csv",
				"tytul,tresc,autorzy\n".getBytes(StandardCharsets.UTF_8)
		);
		when(serwisCsv.importujPosty(any())).thenReturn(new ImportResultDto(2, 1, List.of("err")));

		mockMvc.perform(multipart("/api/v1/admin/import/posts").file(plik))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.liczbaZaimportowanych").value(2))
				.andExpect(jsonPath("$.liczbaPominietych").value(1))
				.andExpect(jsonPath("$.bledy[0]").value("err"));
	}

	@Test
	void powinienEksportowacPostyCsv() throws Exception {
		when(serwisCsv.eksportujPostyCsv()).thenReturn("a,b\n".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(get("/api/v1/admin/export/posts/csv"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=posty.csv"))
				.andExpect(header().string("Content-Type", "text/csv"));
	}

	@Test
	void powinienEksportowacUzytkownikowCsv() throws Exception {
		when(serwisCsv.eksportujUzytkownikowCsv()).thenReturn("a,b\n".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(get("/api/v1/admin/export/users/csv"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=uzytkownicy.csv"))
				.andExpect(header().string("Content-Type", "text/csv"));
	}

	@Test
	void powinienEksportowacPostyPdf() throws Exception {
		when(serwisPdf.eksportujListePostowPdf()).thenReturn("pdf".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(get("/api/v1/admin/export/posts/pdf"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=posty.pdf"))
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));
	}

	@Test
	void powinienEksportowacPostPdf() throws Exception {
		when(serwisPdf.eksportujPostPdf(12L)).thenReturn("pdf".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(get("/api/v1/admin/export/posts/{idPosta}/pdf", 12L))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=post-12.pdf"))
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));
	}
}
