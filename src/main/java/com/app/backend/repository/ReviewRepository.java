package com.app.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.backend.model.Review;
import com.app.backend.model.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@EntityGraph(attributePaths = {"user"})
	List<Review> findByProductIdAndStatusOrderByCreatedAtDesc(Long productId, ReviewStatus status);

	Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

	@Query("""
			select count(r)
			from Review r
			where r.product.id = :productId and r.status = com.app.backend.model.ReviewStatus.APPROVED
			""")
	long countApprovedByProductId(@Param("productId") Long productId);

	@Query("""
			select coalesce(avg(r.rating), 0)
			from Review r
			where r.product.id = :productId and r.status = com.app.backend.model.ReviewStatus.APPROVED
			""")
	double averageApprovedRatingByProductId(@Param("productId") Long productId);

	@Query("""
			select r.rating, count(r)
			from Review r
			where r.product.id = :productId and r.status = com.app.backend.model.ReviewStatus.APPROVED
			group by r.rating
			""")
	List<Object[]> countApprovedByRating(@Param("productId") Long productId);

	@EntityGraph(attributePaths = {"product", "user"})
	@Query("""
			select r
			from Review r
			where (:status is null or r.status = :status)
			  and (:productId is null or r.product.id = :productId)
			  and (:rating is null or r.rating = :rating)
			order by r.createdAt desc
			""")
	List<Review> adminList(
			@Param("status") ReviewStatus status,
			@Param("productId") Long productId,
			@Param("rating") Integer rating
	);
}

