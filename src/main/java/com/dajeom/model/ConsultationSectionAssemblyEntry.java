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
 */
public record ConsultationSectionAssemblyEntry(

        String packetId,

        ConsultationNarrativePriority priority,

        String text,

        List<String> emphasis

) {

    public ConsultationSectionAssemblyEntry {

        packetId = requireText(
                packetId,
                "Section Assembly Entry packetId는 필수입니다."
        );

        Objects.requireNonNull(
                priority,
                "Section Assembly Entry priority는 필수입니다."
        );

        text = requireText(
                text,
                "Section Assembly Entry text는 필수입니다."
        );

        emphasis = normalizeTexts(
                emphasis
        );
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (source == null || source.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> result = new LinkedHashSet<>();

        for (String raw : source) {

            if (raw == null) {
                continue;
            }

            String value = raw.trim();

            if (!value.isBlank()) {
                result.add(value);
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

        String normalized = value.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}