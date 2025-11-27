package org.example.clientservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode; // IMPORT THIS
import lombok.Getter; // IMPORT THIS
import lombok.NoArgsConstructor;
import lombok.Setter; // IMPORT THIS
import lombok.ToString; // IMPORT THIS
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "userId") // This is the key fix
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("client_profiles")
public class ClientProfile implements Persistable<String> {

    @Id
    @Column("user_id")
    private String userId;

    private String name;
    private String phone;
    private List<String> skills;
    private Double latitude;
    private Double longitude;

    @Column("profile_completion_percent")
    private Integer profileCompletionPercent;

    @Column("recommendation_flag")
    private Boolean recommendationFlag;

    @Transient
    private boolean isNew;


    @Override
    public String getId() {
        return this.userId;
    }
    private String email;

    @Column("created_at")
    private Instant createdAt;

    private String source;
    @Override
    public boolean isNew() {
        return this.isNew;
    }
}