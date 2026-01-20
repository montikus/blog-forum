package com.example.blog.repository;

import com.example.blog.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

	@Query("select m from Message m where m.odbiorca.nazwaUzytkownika = :nazwaUzytkownika order by m.wyslanoDnia desc")
	Page<Message> znajdzPoOdbiorcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
			@Param("nazwaUzytkownika") String nazwaUzytkownika,
			Pageable stronicowanie
	);

	@Query("select m from Message m where m.nadawca.nazwaUzytkownika = :nazwaUzytkownika order by m.wyslanoDnia desc")
	Page<Message> znajdzPoNadawcyNazwieUzytkownikaOrderByWyslanoDniaDesc(
			@Param("nazwaUzytkownika") String nazwaUzytkownika,
			Pageable stronicowanie
	);
}
