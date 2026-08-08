package com.dajeom.model;

import com.dajeom.type.MediationStatus;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체의 통관 분석 결과.
 */
public record MediationAnalysis(

        List<MediationCase> cases,

        String ruleVersion

) {

    public MediationAnalysis {

        Objects.requireNonNull(
                cases,
                "통관 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        cases =
                List.copyOf(
                        cases
                );
    }

    public boolean hasConflict() {

        return !cases.isEmpty();
    }

    public boolean hasActiveMediation() {

        return cases
                .stream()
                .anyMatch(
                        mediationCase ->
                                mediationCase.status()
                                        == MediationStatus.ACTIVE
                );
    }

    public boolean hasMissingMediation() {

        return cases
                .stream()
                .anyMatch(
                        mediationCase ->
                                mediationCase.status()
                                        == MediationStatus.ABSENT
                );
    }

    public boolean requiresReview() {

        return cases
                .stream()
                .anyMatch(
                        mediationCase ->
                                mediationCase.status()
                                        != MediationStatus.ACTIVE
                );
    }
}