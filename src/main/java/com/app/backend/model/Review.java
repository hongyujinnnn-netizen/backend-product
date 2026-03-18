package com.app.backend.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "user", "images", "moderatedBy"})
@Entity
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private Integer rating;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String comment;

	@Column(name = "reviewer_name", length = 80)
	private String reviewerName;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "verified_purchase", nullable = false)
	private boolean verifiedPurchase;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReviewStatus status;

	@Column(name = "moderation_reason", columnDefinition = "TEXT")
	private String moderationReason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moderated_by")
	private User moderatedBy;

	@Column(name = "moderated_at")
	private Instant moderatedAt;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ReviewImage> images = new ArrayList<>();

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
		if (this.status == null) {
			this.status = ReviewStatus.PENDING;
		}
	}
}

