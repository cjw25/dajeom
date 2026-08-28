package com.dajeom.model;

import com.dajeom.type.ConsultationNarrativePriority;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Java가 최종 Section을 조립하기 직전 보존하는
 * 하나의 검증 완료 Packet 문단.
 *
 * 이 객체는 서로 다른 Packet 사이의 의미를 연결하지 않는다.
 *
 * =================================================
 * 135-2 Timeline Year Assembly Contract
 * =================================================
 *
 * timelineYear
 *
 * =
 *
 * ConsultationMeaningPacket에서 전달된
 * 공식 명리연도 표시 Group.
 *
 * null이면 일반 Packet이다.
 *
 * non-null이면 같은 timelineYear를 가진
 * 인접 Packet과 동일한 연도 표시 Group으로
 * 조립될 수 있다.
 *
 * 중요한 원칙:
 *
 * 같은 연도라는 사실은
 * 단지 표시 Group만 공유한다는 뜻이다.
 *
 * 서로 다른 Packet 사이의:
 *
 * - 원인
 * - 결과
 * - 선후
 * - 강화
 * - 약화
 * - 의미 합성
 *
 * 을 허용하지 않는다.
 */
public record ConsultationSectionAssemblyEntry(

        String packetId,

        ConsultationNarrativePriority priority,

        Integer timelineYear,

        String text,

        List<String> emphasis

) {

    /**
     * =================================================
     * Legacy Constructor
     * =================================================
     *
     * Timeline Year가 없는 기존 Assembly Entry 생성은
     * 그대로 사용할 수 있다.
     */
    public ConsultationSectionAssemblyEntry(

            String packetId,

            ConsultationNarrativePriority priority,

            String text,

            List<String> emphasis

    ) {

        this(
                packetId,
                priority,
                null,
                text,
                emphasis
        );
    }

    public ConsultationSectionAssemblyEntry {

        packetId = requireText(
                packetId,
                "Section Assembly Entry packetId는 필수입니다."
        );

        Objects.requireNonNull(
                priority,
                "Section Assembly Entry priority는 필수입니다."
        );

        if (
                timelineYear != null
                        &&
                        (
                                timelineYear < 1
                                        ||
                                        timelineYear > 9_999
                        )
        ) {

            throw new IllegalArgumentException(
                    "Section Assembly Entry timelineYear 범위가 올바르지 않습니다: "
                            + timelineYear
            );
        }

        text = requireText(
                text,
                "Section Assembly Entry text는 필수입니다."
        );

        emphasis = normalizeTexts(
                emphasis
        );
    }

    public boolean hasTimelineYear() {

        return timelineYear != null;
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

            String value =
                    raw.trim();

            if (
                    !value.isBlank()
            ) {

                result.add(
                        value
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