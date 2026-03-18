package com.app.backend.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.dto.CreateReviewRequest;
import com.app.backend.dto.ModerateReviewRequest;
import com.app.backend.dto.ReviewResponse;
import com.app.backend.dto.ReviewSummaryResponse;
import com.app.backend.model.Order;
import com.app.backend.model.OrderItem;
import com.app.backend.model.Product;
import com.app.backend.model.Review;
import com.app.backend.model.ReviewStatus;
import com.app.backend.model.User;
import com.app.backend.repository.HelpfulVoteRepository;
import com.app.backend.repository.OrderRepository;
import com.app.backend.repository.ProductRepository;
import com.app.backend.repository.ReviewRepository;
import com.app.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final HelpfulVoteRepository helpfulVoteRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	@Transactional(readOnly = true)
	public List<ReviewResponse> listApprovedByProduct(Long productId) {
		List<Review> reviews = reviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(productId, ReviewStatus.APPROVED);
		return reviews.stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public ReviewSummaryResponse getApprovedSummary(Long productId) {
		double avg = reviewRepository.averageApprovedRatingByProductId(productId);
		long count = reviewRepository.countApprovedByProductId(productId);

		Map<Integer, Long> byRating = new HashMap<>();
		for (Object[] row : reviewRepository.countApprovedByRating(productId)) {
			Integer rating = (Integer) row[0];
			Long c = (Long) row[1];
			byRating.put(rating, c);
		}

		return new ReviewSummaryResponse(
				productId,
				avg,
				count,
				byRating.getOrDefault(1, 0L),
				byRating.getOrDefault(2, 0L),
				byRating.getOrDefault(3, 0L),
				byRating.getOrDefault(4, 0L),
				byRating.getOrDefault(5, 0L)
		);
	}

	@Transactional
	public ReviewResponse create(Long productId, CreateReviewRequest request, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		reviewRepository.findByProductIdAndUserId(productId, user.getId()).ifPresent(existing -> {
			throw new IllegalArgumentException("You have already reviewed this product");
		});

		boolean verified = isVerifiedPurchase(user.getUsername(), productId);

		Review review = Review.builder()
				.product(product)
				.user(user)
				.rating(request.rating())
				.title(request.title().trim())
				.comment(request.comment().trim())
				.reviewerName((request.reviewerName() == null || request.reviewerName().trim().isEmpty())
						? user.getUsername()
						: request.reviewerName().trim())
				.verifiedPurchase(verified)
				.status(ReviewStatus.PENDING)
				.build();

		applyModerationRules(review);

		Review saved = reviewRepository.save(review);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponse> adminList(ReviewStatus status, Long productId, Integer rating) {
		List<Review> items = reviewRepository.adminList(status, productId, rating);
		return items.stream().map(this::toResponse).toList();
	}

	@Transactional
	public ReviewResponse moderate(Long reviewId, ModerateReviewRequest request, String adminUsername) {
		User admin = userRepository.findByUsername(adminUsername)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		if (admin.getRole() == null || !admin.getRole().contains("ADMIN")) {
			throw new IllegalArgumentException("Forbidden");
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));

		ReviewStatus status = request.status();
		if (status == ReviewStatus.PENDING) {
			throw new IllegalArgumentException("Cannot set review back to PENDING");
		}

		review.setStatus(status);
		review.setModerationReason(request.reason());
		review.setModeratedBy(admin);
		review.setModeratedAt(Instant.now());

		return toResponse(reviewRepository.save(review));
	}

	private void applyModerationRules(Review review) {
		String text = (review.getTitle() + " " + review.getComment()).toLowerCase();
		if (text.contains("http://") || text.contains("https://")) {
			review.setStatus(ReviewStatus.PENDING);
			review.setModerationReason("Contains link");
		}

		if (text.contains("spam") || text.contains("scam")) {
			review.setStatus(ReviewStatus.PENDING);
			review.setModerationReason("Possible spam keywords");
		}
	}

	private boolean isVerifiedPurchase(String username, Long productId) {
		List<Order> orders = orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
		for (Order order : orders) {
			for (OrderItem item : order.getItems()) {
				if (item.getProduct() != null && productId.equals(item.getProduct().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	private ReviewResponse toResponse(Review review) {
		long helpfulCount = helpfulVoteRepository.countByReviewId(review.getId());
		return new ReviewResponse(
				review.getId(),
				review.getProduct() != null ? review.getProduct().getId() : null,
				review.getRating(),
				review.getTitle(),
				review.getComment(),
				review.getReviewerName(),
				review.getCreatedAt(),
				review.isVerifiedPurchase(),
				review.getStatus(),
				helpfulCount
		);
	}
}

