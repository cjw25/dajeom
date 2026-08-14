package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyCategory;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-4.
 *
 * 하나의 병(病) 구조에 대한 Evidence.
 *
 * magnitudeBasisPoints:
 *
 * 패턴마다 의미가 다르다.
 *
 * 예:
 *
 * ELEMENT_OVERCONCENTRATION
 * -> 해당 오행 전체 비중
 *
 * OUTPUT_DRAIN_OVERLOAD
 * -> 식상 유효 부담 비중
 *
 * SUPPORT_OVERCONCENTRATION
 * -> 비겁 + 인성 지원 비중
 *
 * EXTREME_COLD
 * -> 한난 편차 절대값
 *
 * 구조 손상처럼 하나의 bp 값으로
 * 표현하지 않는 패턴은 0을 사용한다.
 *
 * 따라서 서로 다른 병 패턴의 magnitude를
 * 직접 비교해서는 안 된다.
 */
public record PathologyEvidence(

        PathologyPatternType patternType,

        PathologySeverity severity,

        List<FiveElement> relatedElements,

        int magnitudeBasisPoints,

        int thresholdBasisPoints

) {

    public PathologyEvidence {

        Objects.requireNonNull(
                patternType,
                "병 구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                severity,
                "병 구조 심각도가 필요합니다."
        );

        relatedElements =
                relatedElements == null
                        ? List.of()
                        : List.copyOf(
                        relatedElements
                );

        if (
                severity.none()
        ) {

            throw new IllegalArgumentException(
                    "PathologyEvidence에는 NONE 심각도를 저장하지 않습니다."
            );
        }

        if (
                relatedElements
                        .stream()
                        .distinct()
                        .count()
                        != relatedElements.size()
        ) {

            throw new IllegalArgumentException(
                    "병 구조의 관련 오행이 중복되었습니다."
            );
        }

        if (
                patternType.relatedElementRequired()
                        && relatedElements.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "해당 병 구조에는 관련 오행이 필요합니다."
            );
        }

        if (
                magnitudeBasisPoints < 0
                        || magnitudeBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "병 구조 magnitude는 0~10000bp 사이여야 합니다."
            );
        }

        if (
                thresholdBasisPoints < 0
                        || thresholdBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "병 구조 threshold는 0~10000bp 사이여야 합니다."
            );
        }
    }

    public PathologyCategory category() {

        return patternType
                .category();
    }

    public boolean meaningful() {

        return severity
                .meaningful();
    }

    public boolean severe() {

        return severity
                .severe();
    }

    public boolean critical() {

        return severity
                .critical();
    }

    public boolean involves(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return relatedElements
                .contains(
                        element
                );
    }

    public boolean thresholdBased() {

        return thresholdBasisPoints > 0;
    }

    public double magnitudePercent() {

        return magnitudeBasisPoints
                / 100.0;
    }

    public double thresholdPercent() {

        return thresholdBasisPoints
                / 100.0;
    }
}