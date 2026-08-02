package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "saju_analyses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SajuAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "birth_profile_id",
            nullable = false
    )
    private BirthProfile birthProfile;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private SajuAnalysisStatus status;

    @Column(
            name = "progress_percent",
            nullable = false
    )
    private int progressPercent;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private SajuAnalysis(
            BirthProfile birthProfile
    ) {
        this.birthProfile = birthProfile;
        this.status = SajuAnalysisStatus.CALCULATING;
        this.progressPercent = 0;
    }

    public static SajuAnalysis createCalculating(
            BirthProfile birthProfile
    ) {
        return new SajuAnalysis(birthProfile);
    }

    public void updateProgress(
            int progressPercent
    ) {
        if (
                progressPercent < 0
                        || progressPercent > 100
        ) {
            throw new IllegalArgumentException(
                    "분석 진행률은 0부터 100 사이여야 합니다."
            );
        }

        this.progressPercent = progressPercent;
    }

    public void complete() {
        this.status = SajuAnalysisStatus.COMPLETED;
        this.progressPercent = 100;
        this.completedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = SajuAnalysisStatus.FAILED;
        this.progressPercent = 0;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}