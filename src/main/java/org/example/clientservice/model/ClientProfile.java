package org.example.clientservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("client_profiles")
public class ClientProfile {

    @Id
    private UUID id;

    @Column("user_id")
    private String userId;

    private String name;
    private String email;
    private String phone;

    private List<String> skills;
    private Double latitude;
    private Double longitude;



    @Column("ai_generated_summary")
    private String aiGeneratedSummary;

    @Column("average_rating")
    private Double averageRating;

    @Column("total_reviews")
    private Integer totalReviews;

    @Column("job_success_rate")
    private Double jobSuccessRate;

    @Column("experience_level")
    private String experienceLevel;

    @Column("recommended_wage_per_hour")
    private Double recommendedWagePerHour;

    @Column("profile_strength_score")
    private Integer profileStrengthScore;

    @Column("top_review_keywords")
    private List<String> topReviewKeywords;

    @Column("last_ai_update")
    private Instant lastAiUpdate;

    @Column("profile_picture_url")
    private String profilePictureUrl;

    @Column("profile_completion_percent")
    private Integer profileCompletionPercent;

    @Column("recommendation_flag")
    private Boolean recommendationFlag;

    @Column("created_at")
    private Instant createdAt;

    private String source;
}