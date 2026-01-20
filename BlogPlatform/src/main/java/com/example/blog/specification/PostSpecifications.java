package com.example.blog.specification;

import com.example.blog.model.Post;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecifications {

	public static Specification<Post> zawieraFraze(String fraza) {
		return (korzen, zapytanie, budowniczy) -> {
			if (fraza == null || fraza.isBlank()) {
				return budowniczy.conjunction();
			}
			String wzorzec = "%" + fraza.toLowerCase(Locale.ROOT) + "%";
			return budowniczy.or(
					budowniczy.like(budowniczy.lower(korzen.get("tytul")), wzorzec),
					budowniczy.like(budowniczy.lower(korzen.get("tresc")), wzorzec)
			);
		};
	}

	public static Specification<Post> maAutora(String nazwaUzytkownika) {
		return (korzen, zapytanie, budowniczy) -> {
			if (nazwaUzytkownika == null || nazwaUzytkownika.isBlank()) {
				return budowniczy.conjunction();
			}
			var autorzy = korzen.join("autorzy");
			return budowniczy.equal(autorzy.get("nazwaUzytkownika"), nazwaUzytkownika);
		};
	}
}
