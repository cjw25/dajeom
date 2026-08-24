package com.dajeom.analyzer;

import com.dajeom.model.ConsultationMeaningPacket;
import com.dajeom.model.ConsultationNarrativeSectionPlan;
import com.dajeom.model.ConsultationSectionAssemblyAnalysis;
import com.dajeom.model.ConsultationSectionAssemblyEntry;
import com.dajeom.type.ConsultationSectionAssemblyViolationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Packet별 검증이 끝난 뒤 Section 전체를 Java가 조립하기 전에
 * 다시 한 번 독립성/순서/반복을 검사한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 의미 유사도 점수 계산
 * - 두 Packet을 하나의 의미로 합치기
 * - 자연어 전환문 생성
 * - 중요도 재판정
 *
 * =================================================
 * 검증하는 것
 * =================================================
 *
 * - 예정된 Packet이 전부 정확히 한 번 존재하는지
 * - Packet 순서가 Section Plan과 동일한지
 * - 서로 다른 Packet이 동일 문단으로 반복되지 않는지
 * - Packet 문단이 이전 Packet을 전제하는 접속 표현으로 시작하지 않는지
 * - 강조 구절이 자기 Packet 본문에 실제 존재하는지
 */
@Component
public class ConsultationSectionAssemblyAnalyzer {

    private static final List<String> CROSS_PACKET_OPENING_CONNECTORS =
            List.of(
                    "또한",
                    "그리고",
                    "한편",
                    "반면",
                    "반대로",
                    "이와 함께",
                    "그와 함께",
                    "더불어",
                    "아울러",
                    "동시에",
                    "마찬가지로",
                    "이에 더해",
                    "이어서"
            );

    public ConsultationSectionAssemblyAnalysis analyze(

            ConsultationNarrativeSectionPlan section,

            List<ConsultationSectionAssemblyEntry> entries

    ) {

        Objects.requireNonNull(
                section,
                "Section Assembly에는 Narrative Section Plan이 필요합니다."
        );

        List<ConsultationSectionAssemblyEntry> normalizedEntries =
                entries == null
                        ? List.of()
                        : entries.stream()
                        .map(entry -> Objects.requireNonNull(
                                entry,
                                "Section Assembly Entry는 null일 수 없습니다."
                        ))
                        .toList();

        EnumMap<ConsultationSectionAssemblyViolationType, List<String>> violations =
                emptyViolations();

        validatePacketSetAndOrder(
                section,
                normalizedEntries,
                violations
        );

        validateTexts(
                normalizedEntries,
                violations
        );

        validateEmphasis(
                normalizedEntries,
                violations
        );

        return new ConsultationSectionAssemblyAnalysis(
                section.category(),
                normalizedEntries,
                violations
        );
    }

    private void validatePacketSetAndOrder(

            ConsultationNarrativeSectionPlan section,

            List<ConsultationSectionAssemblyEntry> entries,

            Map<ConsultationSectionAssemblyViolationType, List<String>> violations

    ) {

        List<String> expectedOrder =
                section
                        .packets()
                        .stream()
                        .map(ConsultationMeaningPacket::packetId)
                        .toList();

        List<String> actualOrder =
                entries
                        .stream()
                        .map(ConsultationSectionAssemblyEntry::packetId)
                        .toList();

        Set<String> expectedSet =
                new LinkedHashSet<>(expectedOrder);

        Map<String, Integer> actualCounts =
                new LinkedHashMap<>();

        for (String packetId : actualOrder) {
            actualCounts.merge(
                    packetId,
                    1,
                    Integer::sum
            );
        }

        for (String expectedId : expectedOrder) {

            if (!actualCounts.containsKey(expectedId)) {
                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.MISSING_PACKET,
                        expectedId
                );
            }
        }

        for (Map.Entry<String, Integer> entry : actualCounts.entrySet()) {

            if (!expectedSet.contains(entry.getKey())) {
                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.UNEXPECTED_PACKET,
                        entry.getKey()
                );
            }

            if (entry.getValue() > 1) {
                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.DUPLICATE_PACKET,
                        entry.getKey()
                );
            }
        }

        if (
                expectedOrder.size() == actualOrder.size()
                        && expectedSet.equals(new LinkedHashSet<>(actualOrder))
                        && !expectedOrder.equals(actualOrder)
        ) {

            for (String packetId : actualOrder) {
                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.PACKET_ORDER_CHANGED,
                        packetId
                );
            }
        }
    }

    private void validateTexts(

            List<ConsultationSectionAssemblyEntry> entries,

            Map<ConsultationSectionAssemblyViolationType, List<String>> violations

    ) {

        Map<String, List<String>> packetIdsByNormalizedText =
                new LinkedHashMap<>();

        for (ConsultationSectionAssemblyEntry entry : entries) {

            String text = entry.text().trim();

            if (text.isBlank()) {

                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.EMPTY_PACKET_TEXT,
                        entry.packetId()
                );

                continue;
            }

            if (startsWithCrossPacketConnector(text)) {

                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.CROSS_PACKET_OPENING_CONNECTOR,
                        entry.packetId()
                );
            }

            String normalized = normalizeTextKey(text);

            /*
             * 지나치게 짧은 문장은 우연히 같을 수 있으므로
             * 긴 문단만 동일문장 반복 검증에 사용한다.
             */
            if (normalized.length() >= 20) {

                packetIdsByNormalizedText
                        .computeIfAbsent(
                                normalized,
                                ignored -> new ArrayList<>()
                        )
                        .add(
                                entry.packetId()
                        );
            }
        }

        for (List<String> packetIds : packetIdsByNormalizedText.values()) {

            if (packetIds.size() < 2) {
                continue;
            }

            for (String packetId : packetIds) {
                add(
                        violations,
                        ConsultationSectionAssemblyViolationType.REPEATED_PACKET_TEXT,
                        packetId
                );
            }
        }
    }

    private void validateEmphasis(

            List<ConsultationSectionAssemblyEntry> entries,

            Map<ConsultationSectionAssemblyViolationType, List<String>> violations

    ) {

        for (ConsultationSectionAssemblyEntry entry : entries) {

            for (String phrase : entry.emphasis()) {

                if (!entry.text().contains(phrase)) {

                    add(
                            violations,
                            ConsultationSectionAssemblyViolationType.EMPHASIS_NOT_IN_PACKET_TEXT,
                            entry.packetId()
                    );
                }
            }
        }
    }

    private boolean startsWithCrossPacketConnector(
            String text
    ) {

        String normalized = text.stripLeading();

        for (String connector : CROSS_PACKET_OPENING_CONNECTORS) {

            if (
                    normalized.equals(connector)
                            || normalized.startsWith(connector + " ")
                            || normalized.startsWith(connector + ",")
                            || normalized.startsWith(connector + ".")
                            || normalized.startsWith(connector + "는")
                            || normalized.startsWith(connector + "도")
            ) {
                return true;
            }
        }

        return false;
    }

    private String normalizeTextKey(
            String value
    ) {

        return value
                .replaceAll(
                        "\\s+",
                        ""
                )
                .replaceAll(
                        "[\\p{Punct}]",
                        ""
                )
                .trim();
    }

    private EnumMap<ConsultationSectionAssemblyViolationType, List<String>>
    emptyViolations() {

        EnumMap<ConsultationSectionAssemblyViolationType, List<String>> result =
                new EnumMap<>(
                        ConsultationSectionAssemblyViolationType.class
                );

        for (
                ConsultationSectionAssemblyViolationType type
                : ConsultationSectionAssemblyViolationType.values()
        ) {
            result.put(
                    type,
                    new ArrayList<>()
            );
        }

        return result;
    }

    private void add(

            Map<ConsultationSectionAssemblyViolationType, List<String>> violations,

            ConsultationSectionAssemblyViolationType type,

            String packetId

    ) {

        violations
                .computeIfAbsent(
                        type,
                        ignored -> new ArrayList<>()
                )
                .add(
                        packetId
                );
    }
}