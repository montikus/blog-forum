package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Role;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

	@Mock
	private PostService serwisPostow;

	private PdfService serwisPdf;

	@BeforeEach
	void przygotujSerwis() {
		serwisPdf = new PdfService(serwisPostow);
	}

	@Test
	void powinienGenerowacPdfDlaPojedynczegoPosta() {
		PostDto post = utworzPostDto(1L, "<p>Tekst &amp; opis</p>");
		when(serwisPostow.pobierzPost(1L)).thenReturn(post);

		byte[] wynik = serwisPdf.eksportujPostPdf(1L);

		assertThat(wynik).isNotEmpty();
	}

	@Test
	void powinienGenerowacPdfDlaListyPostow() {
		PostDto pierwszy = utworzPostDto(2L, "Linia 1\nLinia 2");
		PostDto drugi = utworzPostDto(3L, "");
		when(serwisPostow.pobierzPostyDoEksportu()).thenReturn(List.of(pierwszy, drugi));

		byte[] wynik = serwisPdf.eksportujListePostowPdf();

		assertThat(wynik).isNotEmpty();
	}

	@Test
	void powinienGenerowacPdfZDlugimiTekstamiINowymiStronami() {
		StringBuilder dlugaTresc = new StringBuilder("Akapit 1 ");
		for (int i = 0; i < 1200; i++) {
			dlugaTresc.append("slowo ");
		}
		dlugaTresc.append("\n\n");
		for (int i = 0; i < 1200; i++) {
			dlugaTresc.append("kolejne ");
		}

		PostDto pierwszy = utworzPostDto(4L, dlugaTresc.toString());
		PostDto drugi = utworzPostDto(5L, null);
		drugi.setAutorzy(new ArrayList<>());
		when(serwisPostow.pobierzPostyDoEksportu()).thenReturn(List.of(pierwszy, drugi));

		byte[] wynik = serwisPdf.eksportujListePostowPdf();

		assertThat(wynik).isNotEmpty();
	}

	private PostDto utworzPostDto(Long id, String tresc) {
		PostDto post = new PostDto();
		post.setId(id);
		post.setTytul("Tytul " + id);
		post.setTresc(tresc);
		post.setSredniaOcena(4.5);
		post.setLiczbaKomentarzy(2);
		post.setAutorzy(List.of(new UserDto(10L, "autor", "autor@example.com", Role.USER)));
		return post;
	}
}
