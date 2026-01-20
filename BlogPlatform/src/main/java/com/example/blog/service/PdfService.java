package com.example.blog.service;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PdfService {

	private static final float MARGINES = 50f;
	private static final float ROZMIAR_CZCIONKI = 11f;
	private static final float PROWADZENIE = 14f;

	private final PostService serwisPostow;

	public PdfService(PostService serwisPostow) {
		this.serwisPostow = serwisPostow;
	}

	@Transactional(readOnly = true)
	public byte[] eksportujPostPdf(Long idPosta) {
		PostDto post = serwisPostow.pobierzPost(idPosta);
		return generujPdf(List.of(post), "Post " + idPosta);
	}

	@Transactional(readOnly = true)
	public byte[] eksportujListePostowPdf() {
		List<PostDto> posty = serwisPostow.pobierzPostyDoEksportu();
		return generujPdf(posty, "Lista postow");
	}

	private byte[] generujPdf(List<PostDto> posty, String tytul) {
		try (PDDocument dokument = new PDDocument()) {
			PDFont czcionka = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			KontekstPdf kontekst = new KontekstPdf(dokument, czcionka, ROZMIAR_CZCIONKI, MARGINES, PROWADZENIE);
			kontekst.dodajLinie(tytul);
			kontekst.dodajLinie("");

			int licznik = 1;
			for (PostDto post : posty) {
				String autorzy = post.getAutorzy().stream()
						.map(UserDto::getNazwaUzytkownika)
						.collect(Collectors.joining(", "));
				kontekst.dodajLinie(licznik + ". " + post.getTytul());
				kontekst.dodajLinie("Autorzy: " + (autorzy.isBlank() ? "-" : autorzy));
				kontekst.dodajLinie("Srednia ocena: " + String.format(Locale.US, "%.2f", post.getSredniaOcena()));
				kontekst.dodajLinie("Komentarze: " + post.getLiczbaKomentarzy());
				kontekst.dodajLinie("");

				String tresc = usunZnacznikiHtml(post.getTresc());
				List<String> linieTresci = podzielNaLinie(tresc, czcionka, ROZMIAR_CZCIONKI, kontekst.getSzerokoscTekstu());
				for (String linia : linieTresci) {
					kontekst.dodajLinie(linia);
				}
				kontekst.dodajLinie("");
				licznik++;
			}

			kontekst.zakoncz();
			ByteArrayOutputStream strumien = new ByteArrayOutputStream();
			dokument.save(strumien);
			return strumien.toByteArray();
		} catch (IOException wyjatek) {
			throw new IllegalStateException("Nie mozna wygenerowac PDF");
		}
	}

	private List<String> podzielNaLinie(String tekst, PDFont czcionka, float rozmiar, float szerokosc) throws IOException {
		List<String> linie = new ArrayList<>();
		if (tekst == null || tekst.isBlank()) {
			linie.add("-");
			return linie;
		}
		String[] akapity = tekst.split("\\r?\\n");
		for (String akapit : akapity) {
			if (akapit.isBlank()) {
				linie.add("");
				continue;
			}
			String[] slowa = akapit.trim().split("\\s+");
			StringBuilder aktualna = new StringBuilder();
			for (String slowo : slowa) {
				String kandydat = aktualna.length() == 0 ? slowo : aktualna + " " + slowo;
				float szerokoscTekstu = czcionka.getStringWidth(kandydat) / 1000 * rozmiar;
				if (szerokoscTekstu > szerokosc && aktualna.length() > 0) {
					linie.add(aktualna.toString());
					aktualna = new StringBuilder(slowo);
				} else {
					if (aktualna.length() > 0) {
						aktualna.append(" ");
					}
					aktualna.append(slowo);
				}
			}
			if (aktualna.length() > 0) {
				linie.add(aktualna.toString());
			}
			linie.add("");
		}
		return linie;
	}

	private String usunZnacznikiHtml(String tekst) {
		if (tekst == null) {
			return "";
		}
		String bezZnacznikow = tekst.replaceAll("<[^>]*>", " ");
		return bezZnacznikow
				.replace("&nbsp;", " ")
				.replace("&amp;", "&")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.trim();
	}

	private static class KontekstPdf {

		private final PDDocument dokument;
		private final PDFont czcionka;
		private final float rozmiarCzcionki;
		private final float margines;
		private final float prowadzenie;
		private PDPageContentStream strumien;
		private float pozycjaY;
		private float szerokoscTekstu;

		private KontekstPdf(
				PDDocument dokument,
				PDFont czcionka,
				float rozmiarCzcionki,
				float margines,
				float prowadzenie
		) throws IOException {
			this.dokument = dokument;
			this.czcionka = czcionka;
			this.rozmiarCzcionki = rozmiarCzcionki;
			this.margines = margines;
			this.prowadzenie = prowadzenie;
			rozpocznijNowaStrone();
		}

		public float getSzerokoscTekstu() {
			return szerokoscTekstu;
		}

		public void dodajLinie(String tekst) throws IOException {
			if (pozycjaY - prowadzenie < margines) {
				rozpocznijNowaStrone();
			}
			strumien.showText(tekst);
			strumien.newLineAtOffset(0, -prowadzenie);
			pozycjaY -= prowadzenie;
		}

		public void zakoncz() throws IOException {
			if (strumien != null) {
				strumien.endText();
				strumien.close();
			}
		}

		private void rozpocznijNowaStrone() throws IOException {
			if (strumien != null) {
				strumien.endText();
				strumien.close();
			}
			PDPage strona = new PDPage(PDRectangle.A4);
			dokument.addPage(strona);
			PDRectangle format = strona.getMediaBox();
			szerokoscTekstu = format.getWidth() - (2 * margines);
			pozycjaY = format.getHeight() - margines;
			strumien = new PDPageContentStream(dokument, strona);
			strumien.beginText();
			strumien.setFont(czcionka, rozmiarCzcionki);
			strumien.newLineAtOffset(margines, pozycjaY);
		}
	}
}
