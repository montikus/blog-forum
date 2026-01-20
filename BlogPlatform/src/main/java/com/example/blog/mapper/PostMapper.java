package com.example.blog.mapper;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.PostFormDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

	public PostDto mapujNaDto(Post post, double sredniaOcena, int liczbaKomentarzy) {
		PostDto danePosta = new PostDto();
		danePosta.setId(post.getId());
		danePosta.setTytul(post.getTytul());
		danePosta.setTresc(post.getTresc());
		danePosta.setUtworzonoDnia(post.getUtworzonoDnia());
		danePosta.setZaktualizowanoDnia(post.getZaktualizowanoDnia());
		danePosta.setAutorzy(mapujAutorow(post.getAutorzy()));
		danePosta.setSredniaOcena(sredniaOcena);
		danePosta.setLiczbaKomentarzy(liczbaKomentarzy);
		return danePosta;
	}

	public PostDto mapujNaDto(Post post) {
		int liczbaKomentarzy = post.getKomentarze() != null ? post.getKomentarze().size() : 0;
		return mapujNaDto(post, 0, liczbaKomentarzy);
	}

	public Post mapujNaEncje(PostFormDto formularz) {
		Post post = new Post();
		post.setTytul(formularz.getTytul());
		post.setTresc(formularz.getTresc());
		return post;
	}

	private List<UserDto> mapujAutorow(Set<User> autorzy) {
		List<UserDto> wynik = new ArrayList<>();
		if (autorzy == null) {
			return wynik;
		}
		for (User autor : autorzy) {
			wynik.add(mapujUzytkownika(autor));
		}
		return wynik;
	}

	private UserDto mapujUzytkownika(User uzytkownik) {
		if (uzytkownik == null) {
			return null;
		}
		return new UserDto(
				uzytkownik.getId(),
				uzytkownik.getNazwaUzytkownika(),
				uzytkownik.getAdresEmail(),
				uzytkownik.getRola()
		);
	}
}
