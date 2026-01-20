package com.example.blog.repository;

import com.example.blog.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.nazwaUzytkownika = :nazwaUzytkownika")
	Optional<User> znajdzPoNazwieUzytkownika(@Param("nazwaUzytkownika") String nazwaUzytkownika);

	@Query("select case when count(u) > 0 then true else false end from User u where u.nazwaUzytkownika = :nazwaUzytkownika")
	boolean istniejePoNazwieUzytkownika(@Param("nazwaUzytkownika") String nazwaUzytkownika);

	@Query("select case when count(u) > 0 then true else false end from User u where u.adresEmail = :adresEmail")
	boolean istniejePoAdresieEmail(@Param("adresEmail") String adresEmail);
}
