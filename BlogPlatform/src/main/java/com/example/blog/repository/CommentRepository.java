package com.example.blog.repository;

import com.example.blog.model.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("select k from Comment k where k.post.id = :idPosta order by k.utworzonoDnia asc")
	List<Comment> znajdzPoIdPosta(@Param("idPosta") Long idPosta);
}
