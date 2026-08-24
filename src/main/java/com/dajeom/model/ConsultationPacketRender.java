package com.dajeom.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 Meaning Packet에 대한 AI 구조화 출력.
 *
 * Packet 안의 Claim을 하나의 자유문단으로 합쳐 반환하지 않고
 * Claim별 Render를 각각 보존한다.
 */
public record ConsultationPacketRender(

        String packetId,

        List<ConsultationClaimRender> claimRenders,

        List<String> emphasis

) {

    public ConsultationPacketRender {

        packetId = requireText(
                packetId,
                "Packet Render packetId는 필수입니다."
        );

        claimRenders = claimRenders == null
                ? List.of()
                : claimRenders.stream()
                .map(
                        render -> Objects.requireNonNull(
                                render,
                                "Claim Render는 null일 수 없습니다."
                        )
                )
                .toList();

        emphasis = normalizeTexts(emphasis);
    }

    public List<String> renderedClaimIds() {

        return claimRenders
                .stream()
                .map(ConsultationClaimRender::claimId)
                .toList();
    }

    public int claimCount() {
        return claimRenders.size();
    }

    /**
     * Claim별 문장은 순서만 보존해 Java가 조립한다.
     * 새로운 접속어나 인과관계를 Java가 추가하지 않는다.
     */
    public String storyText() {

        return String.join(
                " ",
                claimRenders
                        .stream()
                        .map(ConsultationClaimRender::text)
                        .toList()
        ).trim();
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

        return List.copyOf(result);
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
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }
}