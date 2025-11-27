package org.example.clientservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("review_ratings")
public class ReviewRating {

    @Id
    private UUID id;

    @Column("worker_id")
    private String workerId;

    @Column("reviewer_id")
    private String reviewerId;

    private Integer rating;
    private String reviewText;


    @Column("punctuality_score")
    private Integer punctualityScore;

    @Column("quality_score")
    private Integer qualityScore;

    @Column("behaviour_score")
    private Integer behaviourScore;

    @Column("job_id")
    private String jobId;

    @Column("created_at")
    private Instant createdAt;
}