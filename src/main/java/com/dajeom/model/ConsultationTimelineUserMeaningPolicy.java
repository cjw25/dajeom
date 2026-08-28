package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Timeline deterministic conclusion을
 * 사용자 Narrative Meaning으로 직접 올릴 수 있는지
 * Rule 단위로 고정하는 정책.
 *
 * 내부 summary는 계산 설명일 뿐 사용자 Meaning이 아니다.
 *
 * CURRENT_TIMELINE은:
 * 대운 -> 세운 -> 계산된 경우 월운
 * 순서를 보존한다.
 *
 * TIMELINE_YEAR는:
 * 연도 전체 방향 -> 세부 연도 구조
 * 순서를 보존한다.
 */
public record ConsultationTimelineUserMeaningPolicy(

        ConsultationCategory category,

        ConsultationCategoryDeterministicLayer layer,

        String ruleCode,

        TimelineStage stage,

        boolean directOutput,

        boolean sourceDomainAware,

        String userSubject,

        String auditReason,

        List<String> forbiddenExtensions

) {

    public static final String SOURCE_DOMAIN_TOKEN =
            "{domain}";

    public ConsultationTimelineUserMeaningPolicy {

        Objects.requireNonNull(
                category,
                "Timeline User Meaning Policy category는 필수입니다."
        );

        Objects.requireNonNull(
                layer,
                "Timeline User Meaning Policy layer는 필수입니다."
        );

        if (
                layer
                        == ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning Policy에는 NATAL Layer를 사용할 수 없습니다."
            );
        }

        ruleCode =
                requireText(
                        ruleCode,
                        "Timeline User Meaning Policy ruleCode는 필수입니다."
                );

        Objects.requireNonNull(
                stage,
                "Timeline User Meaning Policy stage는 필수입니다."
        );

        userSubject =
                userSubject == null
                        ? ""
                        : userSubject.trim();

        auditReason =
                requireText(
                        auditReason,
                        "Timeline User Meaning Policy auditReason은 필수입니다."
                );

        forbiddenExtensions =
                normalizeTexts(
                        forbiddenExtensions
                );

        if (
                directOutput
                        && userSubject.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "directOutput=true인 Timeline User Meaning Policy에는 userSubject가 필요합니다. ruleCode="
                            + ruleCode
            );
        }

        if (
                sourceDomainAware
                        && directOutput
                        && !userSubject.contains(
                        SOURCE_DOMAIN_TOKEN
                )
        ) {

            throw new IllegalArgumentException(
                    "sourceDomainAware=true인 Timeline User Meaning Policy의 userSubject에는 "
                            + SOURCE_DOMAIN_TOKEN
                            + " 토큰이 필요합니다. ruleCode="
                            + ruleCode
            );
        }

        if (
                !directOutput
        ) {

            sourceDomainAware = false;
            userSubject = "";
        }

        validateStageLayer(
                stage,
                layer
        );
    }

    public static ConsultationTimelineUserMeaningPolicy allow(

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            TimelineStage stage,

            boolean sourceDomainAware,

            String userSubject,

            String auditReason,

            List<String> forbiddenExtensions

    ) {

        return new ConsultationTimelineUserMeaningPolicy(
                category,
                layer,
                ruleCode,
                stage,
                true,
                sourceDomainAware,
                userSubject,
                auditReason,
                forbiddenExtensions
        );
    }

    public static ConsultationTimelineUserMeaningPolicy block(

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            TimelineStage stage,

            String auditReason

    ) {

        return new ConsultationTimelineUserMeaningPolicy(
                category,
                layer,
                ruleCode,
                stage,
                false,
                false,
                "",
                auditReason,
                List.of()
        );
    }

    private static void validateStageLayer(

            TimelineStage stage,

            ConsultationCategoryDeterministicLayer layer

    ) {

        if (
                layer
                        == ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE
                        && !stage.currentTimeline()
        ) {

            throw new IllegalArgumentException(
                    "CURRENT_TIMELINE Policy에는 CURRENT Timeline Stage만 사용할 수 있습니다. stage="
                            + stage
            );
        }

        if (
                layer
                        == ConsultationCategoryDeterministicLayer.TIMELINE_YEAR
                        && !stage.timelineYear()
        ) {

            throw new IllegalArgumentException(
                    "TIMELINE_YEAR Policy에는 Timeline Year Stage만 사용할 수 있습니다. stage="
                            + stage
            );
        }
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
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

    public enum TimelineStage {

        CURRENT_DECADE(
                10,
                "대운 흐름",
                true,
                false
        ),

        CURRENT_YEAR(
                20,
                "세운 흐름",
                true,
                false
        ),

        CURRENT_MONTH(
                30,
                "월운 흐름",
                true,
                false
        ),

        RANGE_YEAR_OVERALL(
                40,
                "연도 전체 흐름",
                false,
                true
        ),

        RANGE_YEAR_ACTIVE_DOMAIN(
                50,
                "연도 작동축",
                false,
                true
        ),

        RANGE_YEAR_PRESSURE_DOMAIN(
                60,
                "연도 부담축",
                false,
                true
        ),

        RANGE_YEAR_CROSS_DOMAIN(
                70,
                "연도 영역 공존 구조",
                false,
                true
        ),

        RANGE_YEAR_TURNING_POINT(
                80,
                "연도 전환 구조",
                false,
                true
        );

        private final int order;
        private final String title;
        private final boolean currentTimeline;
        private final boolean timelineYear;

        TimelineStage(
                int order,
                String title,
                boolean currentTimeline,
                boolean timelineYear
        ) {
            this.order = order;
            this.title = title;
            this.currentTimeline = currentTimeline;
            this.timelineYear = timelineYear;
        }

        public int order() {
            return order;
        }

        public String title() {
            return title;
        }

        public boolean currentTimeline() {
            return currentTimeline;
        }

        public boolean timelineYear() {
            return timelineYear;
        }

        public boolean monthly() {
            return this == CURRENT_MONTH;
        }
    }
}