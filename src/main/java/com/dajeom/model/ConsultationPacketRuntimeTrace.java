package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 최종 Section 조립에 실제 사용된 Packet의
 * 런타임 provenance / renderer 선택 기록.
 *
 * =================================================
 * 최종 Render Source
 * =================================================
 *
 * WRITER
 * = 검증된 Writer 출력이 최종 사용됨.
 *
 * REVIEWER
 * = 검증된 Reviewer 출력이 최종 사용됨.
 *
 * DETERMINISTIC_EMERGENCY
 * = AI 출력 대신 Java 공식 Claim 기반 fallback이 사용됨.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * Claim provenance는 AI Render에서 복사하지 않는다.
 * ConsultationMeaningPacket의 Java 공식 Claim에서 다시 만든다.
 */
public record ConsultationPacketRuntimeTrace(

        String packetId,

        ConsultationCategory category,

        FinalRenderSource finalRenderSource,

        String decisionReason,

        String renderedText,

        List<ConsultationClaimRuntimeTrace> claimTraces

) {

    public ConsultationPacketRuntimeTrace {

        packetId =
                requireText(
                        packetId,
                        "Packet Runtime Trace packetId는 필수입니다."
                );

        Objects.requireNonNull(
                category,
                "Packet Runtime Trace category는 필수입니다."
        );

        Objects.requireNonNull(
                finalRenderSource,
                "Packet Runtime Trace finalRenderSource는 필수입니다."
        );

        decisionReason =
                requireText(
                        decisionReason,
                        "Packet Runtime Trace decisionReason은 필수입니다."
                );

        renderedText =
                requireText(
                        renderedText,
                        "Packet Runtime Trace renderedText는 필수입니다."
                );

        claimTraces =
                claimTraces == null
                        ? List.of()
                        : claimTraces
                        .stream()
                        .map(
                                trace ->
                                        Objects.requireNonNull(
                                                trace,
                                                "Packet Runtime Trace의 Claim Trace는 null일 수 없습니다."
                                        )
                        )
                        .toList();

        if (
                claimTraces.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Packet Runtime Trace에는 최소 하나의 Claim Trace가 필요합니다. packetId="
                            + packetId
            );
        }

        LinkedHashSet<String> claimIds =
                new LinkedHashSet<>();

        for (
                ConsultationClaimRuntimeTrace trace
                : claimTraces
        ) {

            if (
                    !claimIds.add(
                            trace.claimId()
                    )
            ) {

                throw new IllegalArgumentException(
                        "Packet Runtime Trace의 Claim ID가 중복되었습니다. packetId="
                                + packetId
                                + ", claimId="
                                + trace.claimId()
                );
            }
        }
    }

    /**
     * 최종 Packet과 Render 선택 결과에서 trace를 만든다.
     * Claim provenance는 반드시 packet.claims()에서 다시 생성한다.
     */
    public static ConsultationPacketRuntimeTrace from(

            ConsultationMeaningPacket packet,

            FinalRenderSource finalRenderSource,

            String decisionReason,

            String renderedText

    ) {

        Objects.requireNonNull(
                packet,
                "Packet Runtime Trace 생성에는 Meaning Packet이 필요합니다."
        );

        Objects.requireNonNull(
                finalRenderSource,
                "Packet Runtime Trace 생성에는 finalRenderSource가 필요합니다."
        );

        List<ConsultationClaimRuntimeTrace> claimTraces =
                packet
                        .claims()
                        .stream()
                        .map(
                                ConsultationClaimRuntimeTrace::from
                        )
                        .toList();

        return new ConsultationPacketRuntimeTrace(
                packet.packetId(),
                packet.category(),
                finalRenderSource,
                decisionReason,
                renderedText,
                claimTraces
        );
    }

    public boolean fallbackUsed() {

        return finalRenderSource
                == FinalRenderSource.DETERMINISTIC_EMERGENCY;
    }

    public List<String> claimIds() {

        return claimTraces
                .stream()
                .map(
                        ConsultationClaimRuntimeTrace::claimId
                )
                .toList();
    }

    public List<String> sourceCodes() {

        return claimTraces
                .stream()
                .map(
                        ConsultationClaimRuntimeTrace::sourceCode
                )
                .distinct()
                .toList();
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

    public enum FinalRenderSource {

        WRITER,

        REVIEWER,

        DETERMINISTIC_EMERGENCY
    }
}