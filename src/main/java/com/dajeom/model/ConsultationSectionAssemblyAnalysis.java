package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationSectionAssemblyViolationType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 하나의 Consultation Section 최종 조립 검증 결과.
 *
 * valid 상태에서만 storyText를 만들 수 있다.
 * Java는 Packet 문단 사이에 새로운 접속사나 인과 설명을 추가하지 않고
 * 빈 줄 두 개로만 연결한다.
 */
public record ConsultationSectionAssemblyAnalysis(

        ConsultationCategory category,

        List<ConsultationSectionAssemblyEntry> entries,

        Map<ConsultationSectionAssemblyViolationType, List<String>> violations

) {

    public ConsultationSectionAssemblyAnalysis {

        Objects.requireNonNull(
                category,
                "Section Assembly Analysis category는 필수입니다."
        );

        entries = entries == null
                ? List.of()
                : entries.stream()
                .map(entry -> Objects.requireNonNull(
                        entry,
                        "Section Assembly Entry는 null일 수 없습니다."
                ))
                .toList();

        violations = immutableViolations(
                violations
        );
    }

    public boolean valid() {

        return violations
                .values()
                .stream()
                .allMatch(List::isEmpty);
    }

    public boolean hasViolation(
            ConsultationSectionAssemblyViolationType type
    ) {

        if (type == null) {
            return false;
        }

        return !violations
                .getOrDefault(
                        type,
                        List.of()
                )
                .isEmpty();
    }

    public List<String> violationPacketIds(
            ConsultationSectionAssemblyViolationType type
    ) {

        if (type == null) {
            return List.of();
        }

        return violations.getOrDefault(
                type,
                List.of()
        );
    }

    /**
     * 어떤 위반이든 연관된 Packet ID를 중복 없이 반환한다.
     */
    public List<String> violatingPacketIds() {

        LinkedHashSet<String> result = new LinkedHashSet<>();

        for (List<String> packetIds : violations.values()) {
            result.addAll(packetIds);
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 검증된 Packet 문단만 Java가 순서대로 조립한다.
     *
     * 문단 사이에는 단지 빈 줄만 넣고
     * "또한", "따라서", "반면" 같은 관계 표현을 생성하지 않는다.
     */
    public String storyText() {

        requireValid();

        return String.join(
                "\n\n",
                entries
                        .stream()
                        .map(ConsultationSectionAssemblyEntry::text)
                        .toList()
        ).trim();
    }

    /**
     * 실제 조립된 Section 본문에 존재하는 강조 구절만 보존한다.
     */
    public List<String> emphasis(
            int maxCount
    ) {

        requireValid();

        if (maxCount < 1) {
            return List.of();
        }

        String storyText = storyText();

        LinkedHashSet<String> result = new LinkedHashSet<>();

        for (ConsultationSectionAssemblyEntry entry : entries) {

            for (String phrase : entry.emphasis()) {

                if (result.size() >= maxCount) {
                    return List.copyOf(result);
                }

                if (storyText.contains(phrase)) {
                    result.add(phrase);
                }
            }
        }

        return List.copyOf(
                result
        );
    }

    public String summary() {

        if (valid()) {
            return "";
        }

        List<String> parts = new ArrayList<>();

        for (
                ConsultationSectionAssemblyViolationType type
                : ConsultationSectionAssemblyViolationType.values()
        ) {

            List<String> packetIds =
                    violations.getOrDefault(
                            type,
                            List.of()
                    );

            if (packetIds.isEmpty()) {
                continue;
            }

            parts.add(
                    type.name()
                            + "="
                            + String.join("|", packetIds)
            );
        }

        return String.join(
                ", ",
                parts
        );
    }

    private void requireValid() {

        if (!valid()) {
            throw new IllegalStateException(
                    "검증되지 않은 Section Assembly는 조립할 수 없습니다. category="
                            + category
                            + ", reason="
                            + summary()
            );
        }
    }

    private static Map<ConsultationSectionAssemblyViolationType, List<String>>
    immutableViolations(
            Map<ConsultationSectionAssemblyViolationType, List<String>> source
    ) {

        EnumMap<ConsultationSectionAssemblyViolationType, List<String>> result =
                new EnumMap<>(
                        ConsultationSectionAssemblyViolationType.class
                );

        for (
                ConsultationSectionAssemblyViolationType type
                : ConsultationSectionAssemblyViolationType.values()
        ) {

            List<String> values =
                    source == null
                            ? List.of()
                            : source.getOrDefault(
                            type,
                            List.of()
                    );

            LinkedHashSet<String> normalized = new LinkedHashSet<>();

            for (String raw : values) {

                if (raw == null) {
                    continue;
                }

                String value = raw.trim();

                if (!value.isBlank()) {
                    normalized.add(value);
                }
            }

            result.put(
                    type,
                    List.copyOf(normalized)
            );
        }

        return Map.copyOf(
                result
        );
    }
}