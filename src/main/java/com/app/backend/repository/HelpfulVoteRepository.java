package com.app.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.backend.model.HelpfulVote;

public interface HelpfulVoteRepository extends JpaRepository<HelpfulVote, Long> {

	@Query("select count(v) from HelpfulVote v where v.review.id = :reviewId")
	long countByReviewId(@Param("reviewId") Long reviewId);
}

