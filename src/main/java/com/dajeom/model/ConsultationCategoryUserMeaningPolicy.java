package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 Category Deterministic Rule이
 * 사용자 Narrative Meaning으로 직접 올라갈 수 있는지
 * 명시적으로 고정하는 정책.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * Category Deterministic Match의 summary는
 * 내부 deterministic 계산 설명이다.
 *
 * 따라서:
 *
 * internal summary
 * =
 * user meaning
 *
 * 으로 자동 간주하지 않는다.
 *
 * =================================================
 * directOutput
 * =================================================
 *
 * true
 *
 * 해당 Rule의 deterministic status를
 * 사용자 Meaning으로 변환할 수 있다.
 *
 * false
 *
 * 내부 계산 / provenance로는 유지하지만
 * 해당 Rule 자체를 사용자 Meaning Claim으로 만들지 않는다.
 *
 * =================================================
 * userSubject
 * =================================================
 *
 * directOutput=true인 경우에만 사용한다.
 *
 * 명리 전문용어와 내부 코드가 아닌
 * 사용자 언어로 표현된 공식 Meaning Subject다.
 *
 * =================================================
 * auditReason
 * =================================================
 *
 * 왜 직접 출력하거나
 * 왜 차단했는지 Java에서 추적하기 위한 감사 근거.
 *
 * 사용자 Story에는 직접 노출하지 않는다.
 */
public record ConsultationCategoryUserMeaningPolicy(

        ConsultationCategory category,

        String ruleCode,

        boolean directOutput,

        String userSubject,

        String auditReason,

        List<String> forbiddenExtensions

) {

    public ConsultationCategoryUserMeaningPolicy {

        Objects.requireNonNull(
                category,
                "Category User Meaning Policy category는 필수입니다."
        );

        ruleCode =
                requireText(
                        ruleCode,
                        "Category User Meaning Policy ruleCode는 필수입니다."
                );

        userSubject =
                userSubject == null
                        ? ""
                        : userSubject.trim();

        auditReason =
                requireText(
                        auditReason,
                        "Category User Meaning Policy auditReason은 필수입니다."
                );

        forbiddenExtensions =
                normalizeTexts(
                        forbiddenExtensions
                );

        if (
                directOutput
                        &&
                        userSubject.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "directOutput=true인 Category User Meaning Policy에는 "
                            + "userSubject가 필요합니다. ruleCode="
                            + ruleCode
            );
        }

        if (
                !directOutput
        ) {

            userSubject = "";
        }
    }

    public static ConsultationCategoryUserMeaningPolicy allow(

            ConsultationCategory category,

            String ruleCode,

            String userSubject,

            String auditReason,

            List<String> forbiddenExtensions

    ) {

        return new ConsultationCategoryUserMeaningPolicy(
                category,
                ruleCode,
                true,
                userSubject,
                auditReason,
                forbiddenExtensions
        );
    }

    public static ConsultationCategoryUserMeaningPolicy block(

            ConsultationCategory category,

            String ruleCode,

            String auditReason

    ) {

        return new ConsultationCategoryUserMeaningPolicy(
                category,
                ruleCode,
                false,
                "",
                auditReason,
                List.of()
        );
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                String raw
                : source
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String normalized =
                    raw.trim();

            if (
                    !normalized.isBlank()
            ) {

                result.add(
                        normalized
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}