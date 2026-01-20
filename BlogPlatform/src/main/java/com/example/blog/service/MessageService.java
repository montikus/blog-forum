package com.example.blog.service;

import com.example.blog.dao.PostRaportDao;
import com.example.blog.dto.MessageDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.MessageMapper;
import com.example.blog.model.Message;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.MessageRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
	@Transactional
public class MessageService {

	private final MessageRepository repozytoriumWiadomosci;
	private final UserRepository repozytoriumUzytkownikow;
	private final PostRepository repozytoriumPostow;
	private final MessageMapper mapperWiadomosci;
	private final PostRaportDao daoRaportow;

	public MessageService(
			MessageRepository repozytoriumWiadomosci,
			UserRepository repozytoriumUzytkownikow,
			PostRepository repozytoriumPostow,
			MessageMapper mapperWiadomosci,
			PostRaportDao daoRaportow
	) {
		this.repozytoriumWiadomosci = repozytoriumWiadomosci;
		this.repozytoriumUzytkownikow = repozytoriumUzytkownikow;
		this.repozytoriumPostow = repozytoriumPostow;
		this.mapperWiadomosci = mapperWiadomosci;
		this.daoRaportow = daoRaportow;
	}

	public MessageDto wyslijWiadomosc(Long idOdbiorcy, String tresc, User nadawca, Long idPosta) {
		User odbiorca = repozytoriumUzytkownikow.findById(idOdbiorcy)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono odbiorcy"));
		return zapiszWiadomosc(odbiorca, tresc, nadawca, idPosta);
	}

	public MessageDto wyslijWiadomosc(String nazwaOdbiorcy, String tresc, User nadawca, Long idPosta) {
		User odbiorca = repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika(nazwaOdbiorcy)
				.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono odbiorcy"));
		return zapiszWiadomosc(odbiorca, tresc, nadawca, idPosta);
	}

	public Page<MessageDto> pobierzOdebrane(User uzytkownik, Pageable stronicowanie) {
		Page<MessageDto> wiadomosci = repozytoriumWiadomosci.znajdzPoOdbiorcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
						uzytkownik.getNazwaUzytkownika(),
						stronicowanie
				)
				.map(mapperWiadomosci::mapujNaDto);
		daoRaportow.oznaczWiadomosciJakoPrzeczytane(uzytkownik.getId());
		return wiadomosci;
	}

	@Transactional(readOnly = true)
	public Page<MessageDto> pobierzWyslane(User uzytkownik, Pageable stronicowanie) {
		return repozytoriumWiadomosci.znajdzPoNadawcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
						uzytkownik.getNazwaUzytkownika(),
						stronicowanie
				)
				.map(mapperWiadomosci::mapujNaDto);
	}

	private MessageDto zapiszWiadomosc(User odbiorca, String tresc, User nadawca, Long idPosta) {
		Message wiadomosc = new Message();
		wiadomosc.setOdbiorca(odbiorca);
		wiadomosc.setNadawca(nadawca);
		wiadomosc.setTresc(tresc);
		if (idPosta != null) {
			Post post = repozytoriumPostow.findById(idPosta)
					.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono posta"));
			wiadomosc.setPost(post);
		}
		return mapperWiadomosci.mapujNaDto(repozytoriumWiadomosci.save(wiadomosc));
	}
}
