package com.example.blog.repository;

import com.example.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

	@Query("select distinct p from Post p join p.autorzy a where a.nazwaUzytkownika = :nazwaUzytkownika")
	Page<Post> znajdzPoAutorachNazwieUzytkownika(
			@Param("nazwaUzytkownika") String nazwaUzytkownika,
			Pageable stronicowanie
	);
}
