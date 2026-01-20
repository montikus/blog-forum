package com.example.blog.mapper;

import com.example.blog.dto.MessageDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Message;
import com.example.blog.model.User;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

	public MessageDto mapujNaDto(Message wiadomosc) {
		MessageDto daneWiadomosci = new MessageDto();
		daneWiadomosci.setId(wiadomosc.getId());
		daneWiadomosci.setTresc(wiadomosc.getTresc());
		daneWiadomosci.setWyslanoDnia(wiadomosc.getWyslanoDnia());
		daneWiadomosci.setPostId(wiadomosc.getPost() != null ? wiadomosc.getPost().getId() : null);
		daneWiadomosci.setNadawca(mapujUzytkownika(wiadomosc.getNadawca()));
		daneWiadomosci.setOdbiorca(mapujUzytkownika(wiadomosc.getOdbiorca()));
		return daneWiadomosci;
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
