package com.dajeom.model;

import com.dajeom.type.TenGodCrossValidationFactorType;
import com.dajeom.type.TenGodCrossValidationReasonType;
import com.dajeom.type.TenGodCrossValidationStatus;
import com.dajeom.type.TenGodCorePatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 10차-9.
 *
 * 10-8 Pattern 하나를
 * 격국·신강신약·용신·합화로
 * 재검증한 최종 Pattern 객체.
 *
 * =================================================
 *
 * sourceSignal은 절대 삭제하지 않는다.
 *
 * Pattern이 약화되거나 충돌해도
 * 원래 10-8 Evidence를 그대로 추적할 수 있다.
 */
public record TenGodCrossValidatedPattern(

        TenGodCorePatternSignal sourceSignal,

        TenGodCrossValidationStatus status,

        List<TenGodCrossValidationEvidence> evidences,

        List<TenGodCrossValidationReasonType> finalReasons

) {

    public TenGodCrossValidatedPattern {

        Objects.requireNonNull(
                sourceSignal,
                "10-8 원본 Pattern Signal이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "최종 교차검증 상태가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        finalReasons =
                finalReasons == null
                        ? List.of()
                        : List.copyOf(
                        finalReasons
                );

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "교차검증 Pattern에는 하나 이상의 Factor Evidence가 필요합니다."
            );
        }

        Set<TenGodCrossValidationFactorType> factorTypes =
                new HashSet<>();

        for (
                TenGodCrossValidationEvidence evidence
                : evidences
        ) {

            if (
                    !factorTypes.add(
                            evidence.factorType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 교차검증 Factor가 중복되었습니다: "
                                + evidence.factorType()
                );
            }
        }

        if (
                finalReasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "최종 교차검증 근거가 필요합니다."
            );
        }
    }

    public TenGodCorePatternType patternType() {

        return sourceSignal
                .patternType();
    }

    public String koreanName() {

        return sourceSignal
                .koreanName();
    }

    public boolean supported() {

        return status
                .supported();
    }

    public boolean qualified() {

        return status
                .qualified();
    }

    public boolean weakened() {

        return status
                .weakened();
    }

    public boolean conflicted() {

        return status
                .conflicted();
    }

    public Optional<TenGodCrossValidationEvidence> evidenceOf(
            TenGodCrossValidationFactorType factorType
    ) {

        if (
                factorType == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.factorType()
                                        == factorType
                )
                .findFirst();
    }

    public int supportFactorCount() {

        return (int) evidences
                .stream()
                .filter(
                        TenGodCrossValidationEvidence
                                ::supported
                )
                .count();
    }

    public int qualificationFactorCount() {

        return (int) evidences
                .stream()
                .filter(
                        TenGodCrossValidationEvidence
                                ::qualified
                )
                .count();
    }

    public int weakenedFactorCount() {

        return (int) evidences
                .stream()
                .filter(
                        TenGodCrossValidationEvidence
                                ::weakened
                )
                .count();
    }

    public int conflictedFactorCount() {

        return (int) evidences
                .stream()
                .filter(
                        TenGodCrossValidationEvidence
                                ::conflicted
                )
                .count();
    }

    public boolean hasFinalReason(
            TenGodCrossValidationReasonType reason
    ) {

        return reason != null
                && finalReasons.contains(
                reason
        );
    }
}