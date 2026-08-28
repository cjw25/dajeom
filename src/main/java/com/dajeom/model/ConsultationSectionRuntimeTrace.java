package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 최종 Category Story를 구성한
 * Packet Runtime Trace 묶음.
 *
 * Story Text와 별도로:
 *
 * - 어떤 Packet이 최종 사용되었는지
 * - 각 Packet이 Writer / Reviewer / deterministic fallback 중 무엇인지
 * - fallback Packet이 무엇인지
 * - 각 Packet의 Claim sourceCode / origin / authority / specificity가 무엇인지
 *
 * 를 런타임에서 역추적할 수 있게 한다.
 */
public record ConsultationSectionRuntimeTrace(

        ConsultationCategory category,

        List<ConsultationPacketRuntimeTrace> packetTraces

) {

    public ConsultationSectionRuntimeTrace {

        Objects.requireNonNull(
                category,
                "Section Runtime Trace category는 필수입니다."
        );

        packetTraces =
                packetTraces == null
                        ? List.of()
                        : packetTraces
                        .stream()
                        .map(
                                trace ->
                                        Objects.requireNonNull(
                                                trace,
                                                "Section Runtime Trace의 Packet Trace는 null일 수 없습니다."
                                        )
                        )
                        .toList();

        if (
                packetTraces.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Section Runtime Trace에는 최소 하나의 Packet Trace가 필요합니다. category="
                            + category
            );
        }

        LinkedHashSet<String> packetIds =
                new LinkedHashSet<>();

        for (
                ConsultationPacketRuntimeTrace trace
                : packetTraces
        ) {

            if (
                    trace.category()
                            != category
            ) {

                throw new IllegalArgumentException(
                        "Section Runtime Trace의 Category와 Packet Trace Category가 일치하지 않습니다. section="
                                + category
                                + ", packetId="
                                + trace.packetId()
                                + ", packetCategory="
                                + trace.category()
                );
            }

            if (
                    !packetIds.add(
                            trace.packetId()
                    )
            ) {

                throw new IllegalArgumentException(
                        "Section Runtime Trace의 Packet ID가 중복되었습니다. category="
                                + category
                                + ", packetId="
                                + trace.packetId()
                );
            }
        }
    }

    public boolean fallbackUsed() {

        return packetTraces
                .stream()
                .anyMatch(
                        ConsultationPacketRuntimeTrace::fallbackUsed
                );
    }

    public List<String> fallbackPacketIds() {

        return packetTraces
                .stream()
                .filter(
                        ConsultationPacketRuntimeTrace::fallbackUsed
                )
                .map(
                        ConsultationPacketRuntimeTrace::packetId
                )
                .toList();
    }

    public Optional<ConsultationPacketRuntimeTrace> packetTraceOf(
            String packetId
    ) {

        if (
                packetId == null
                        || packetId.isBlank()
        ) {

            return Optional.empty();
        }

        String normalized =
                packetId.trim();

        return packetTraces
                .stream()
                .filter(
                        trace ->
                                trace.packetId()
                                        .equals(
                                                normalized
                                        )
                )
                .findFirst();
    }
}