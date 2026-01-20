package com.example.blog.mapper;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

	public CommentDto mapujNaDto(Comment komentarz) {
		CommentDto daneKomentarza = new CommentDto();
		daneKomentarza.setId(komentarz.getId());
		daneKomentarza.setTresc(komentarz.getTresc());
		daneKomentarza.setUtworzonoDnia(komentarz.getUtworzonoDnia());
		daneKomentarza.setPostId(komentarz.getPost() != null ? komentarz.getPost().getId() : null);
		daneKomentarza.setAutor(mapujUzytkownika(komentarz.getAutor()));
		return daneKomentarza;
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
