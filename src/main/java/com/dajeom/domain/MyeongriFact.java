package com.dajeom.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Interpretation Engine이 사용하는
 * 순수 명리 사실.
 *
 * 예:
 *
 * factCode:
 * MONTH_COMMAND_SHEN
 *
 * type:
 * MONTH_COMMAND
 *
 * details:
 * - 월지 申
 * - 월령은 申월
 *
 *
 * 또 다른 예:
 *
 * factCode:
 * DAY_STEM_DING_ROOTED_AT_WEI
 *
 * type:
 * STEM_ROOTED
 *
 * details:
 * - 丁이 未 지장간에 존재
 *
 *
 * 여기에는:
 *
 * 점수
 * 확률
 * confidence
 * strength
 *
 * 를 넣지 않는다.
 */
public record MyeongriFact(

        String factCode,

        MyeongriFactType type,

        InterpretationLayer layer,

        String periodKey,

        LocalDateTime startDateTimeInclusive,

        LocalDateTime endDateTimeExclusive,

        List<String> details,

        Set<String> tags

) {

    public MyeongriFact {

        factCode =
                normalizeCode(
                        factCode,
                        "factCode"
                );

        Objects.requireNonNull(
                type,
                "fact type은 필수입니다."
        );

        Objects.requireNonNull(
                layer,
                "InterpretationLayer는 필수입니다."
        );

        Objects.requireNonNull(
                periodKey,
                "periodKey는 필수입니다."
        );

        periodKey =
                periodKey.trim();

        if (periodKey.isEmpty()) {

            throw new IllegalArgumentException(
                    "periodKey는 비어 있을 수 없습니다."
            );
        }

        boolean natal =
                "NATAL".equals(
                        periodKey
                );

        if (natal) {

            if (
                    startDateTimeInclusive != null
                            ||
                            endDateTimeExclusive != null
            ) {

                throw new IllegalArgumentException(
                        "NATAL Fact에는 시작/종료시각을 지정하지 않습니다."
                );
            }

        } else {

            Objects.requireNonNull(
                    startDateTimeInclusive,
                    "운세 Fact에는 시작시각이 필요합니다."
            );

            Objects.requireNonNull(
                    endDateTimeExclusive,
                    "운세 Fact에는 종료시각이 필요합니다."
            );

            if (
                    !endDateTimeExclusive.isAfter(
                            startDateTimeInclusive
                    )
            ) {

                throw new IllegalArgumentException(
                        "Fact 종료시각은 시작시각보다 뒤여야 합니다."
                );
            }
        }

        details =
                normalizeDetails(
                        details
                );

        tags =
                normalizeTags(
                        tags
                );
    }

    public static MyeongriFact natal(

            String factCode,

            MyeongriFactType type,

            InterpretationLayer layer,

            List<String> details,

            Set<String> tags

    ) {

        return new MyeongriFact(
                factCode,
                type,
                layer,
                "NATAL",
                null,
                null,
                details,
                tags
        );
    }

    public boolean appliesAt(
            LocalDateTime targetDateTime
    ) {

        Objects.requireNonNull(
                targetDateTime,
                "조회 기준시각은 필수입니다."
        );

        if (
                "NATAL".equals(
                        periodKey
                )
        ) {

            return true;
        }

        return !targetDateTime.isBefore(
                startDateTimeInclusive
        )
                &&
                targetDateTime.isBefore(
                        endDateTimeExclusive
                );
    }

    private static String normalizeCode(

            String value,

            String fieldName

    ) {

        Objects.requireNonNull(
                value,
                fieldName + "는 필수입니다."
        );

        String normalized =
                value
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        if (
                normalized.isEmpty()
                        ||
                        !normalized.matches(
                                "[A-Z0-9_]+"
                        )
        ) {

            throw new IllegalArgumentException(
                    fieldName
                            + " 형식이 올바르지 않습니다: "
                            + normalized
            );
        }

        return normalized;
    }

    private static List<String> normalizeDetails(
            List<String> source
    ) {

        Objects.requireNonNull(
                source,
                "Fact details는 필수입니다."
        );

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (String value : source) {

            if (value == null) {
                continue;
            }

            String normalized =
                    value.trim();

            if (!normalized.isEmpty()) {

                result.add(
                        normalized
                );
            }
        }

        if (result.isEmpty()) {

            throw new IllegalArgumentException(
                    "Fact에는 최소 하나의 detail이 필요합니다."
            );
        }

        return List.copyOf(
                new ArrayList<>(
                        result
                )
        );
    }

    private static Set<String> normalizeTags(
            Set<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (String value : source) {

            if (value == null) {
                continue;
            }

            String normalized =
                    value
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            );

            if (!normalized.isEmpty()) {

                result.add(
                        normalized
                );
            }
        }

        return Set.copyOf(
                result
        );
    }
}