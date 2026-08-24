package com.dajeom.analyzer;

import com.dajeom.model.ConsultationClaimCoverageAnalysis;
import com.dajeom.model.ConsultationClaimRender;
import com.dajeom.model.ConsultationMeaningClaim;
import com.dajeom.model.ConsultationMeaningPacket;
import com.dajeom.model.ConsultationPacketRender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 실제 Claim별 Render 구조를 검증한다.
 *
 * 검증 내용:
 * - Packet ID 동일성
 * - 모든 공식 Claim이 정확히 한 번 존재하는지
 * - 다른 Claim ID가 들어오지 않았는지
 * - sourceClaimText가 Java 공식 원문과 동일한지
 * - Meaning Origin Echo가 Java 공식 Origin과 동일한지
 * - Meaning Authority Echo가 Java 공식 Authority와 동일한지
 * - 서로 다른 Claim을 같은 문장 하나로 뭉개 반복하지 않았는지
 */
@Component
public class ConsultationClaimCoverageAnalyzer {

    public ConsultationClaimCoverageAnalysis analyze(
            ConsultationMeaningPacket packet,
            ConsultationPacketRender render
    ) {

        Objects.requireNonNull(
                packet,
                "Claim Coverage에는 Meaning Packet이 필요합니다."
        );

        Objects.requireNonNull(
                render,
                "Claim Coverage에는 Packet Render가 필요합니다."
        );

        Map<String, ConsultationMeaningClaim> expected =
                new LinkedHashMap<>();

        for (ConsultationMeaningClaim claim : packet.claims()) {
            expected.put(claim.claimId(), claim);
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        LinkedHashSet<String> unexpected = new LinkedHashSet<>();
        LinkedHashSet<String> sourceMismatch = new LinkedHashSet<>();
        LinkedHashSet<String> originMismatch = new LinkedHashSet<>();
        LinkedHashSet<String> authorityMismatch = new LinkedHashSet<>();

        for (ConsultationClaimRender claimRender : render.claimRenders()) {

            counts.merge(
                    claimRender.claimId(),
                    1,
                    Integer::sum
            );

            ConsultationMeaningClaim canonical =
                    expected.get(claimRender.claimId());

            if (canonical == null) {
                unexpected.add(claimRender.claimId());
                continue;
            }

            if (!canonical.text().equals(claimRender.sourceClaimText())) {
                sourceMismatch.add(claimRender.claimId());
            }

            if (canonical.origin() != claimRender.origin()) {
                originMismatch.add(claimRender.claimId());
            }

            if (canonical.authority() != claimRender.authority()) {
                authorityMismatch.add(claimRender.claimId());
            }
        }

        List<String> missing = new ArrayList<>();
        List<String> duplicate = new ArrayList<>();

        for (String claimId : expected.keySet()) {
            int count = counts.getOrDefault(claimId, 0);

            if (count == 0) {
                missing.add(claimId);
            } else if (count > 1) {
                duplicate.add(claimId);
            }
        }

        List<String> repeatedTextClaimIds =
                repeatedTextClaimIds(render.claimRenders());

        return new ConsultationClaimCoverageAnalysis(
                packet.packetId(),
                packet.packetId().equals(render.packetId()),
                packet.claims().size(),
                render.claimRenders().size(),
                List.copyOf(missing),
                List.copyOf(unexpected),
                List.copyOf(duplicate),
                List.copyOf(sourceMismatch),
                List.copyOf(originMismatch),
                List.copyOf(authorityMismatch),
                repeatedTextClaimIds
        );
    }

    private List<String> repeatedTextClaimIds(
            List<ConsultationClaimRender> renders
    ) {

        Map<String, List<String>> idsByText =
                new LinkedHashMap<>();

        for (ConsultationClaimRender render : renders) {

            String key = normalizeMeaningText(render.text());

            if (key.length() < 12) {
                continue;
            }

            idsByText
                    .computeIfAbsent(
                            key,
                            ignored -> new ArrayList<>()
                    )
                    .add(render.claimId());
        }

        Set<String> result = new LinkedHashSet<>();

        for (List<String> claimIds : idsByText.values()) {

            if (claimIds.size() < 2) {
                continue;
            }

            result.addAll(claimIds);
        }

        return List.copyOf(result);
    }

    private String normalizeMeaningText(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replaceAll("\\s+", "")
                .replaceAll("[\\p{Punct}]", "")
                .trim();
    }
}