package com.example.blog.repository;

import com.example.blog.model.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

	@Query("select r from Rating r where r.post.id = :idPosta and r.uzytkownik.id = :idUzytkownika")
	Optional<Rating> znajdzPoIdPostaIUzytkownika(
			@Param("idPosta") Long idPosta,
			@Param("idUzytkownika") Long idUzytkownika
	);

	@Query("select coalesce(avg(r.wartosc), 0) from Rating r where r.post.id = :idPosta")
	double pobierzSredniaDlaPosta(@Param("idPosta") Long idPosta);
}
