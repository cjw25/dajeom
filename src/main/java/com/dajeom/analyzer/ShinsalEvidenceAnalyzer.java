package com.dajeom.analyzer;

import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.ShinsalEvidence;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.model.ShinsalSegmentAnalysis;
import com.dajeom.model.ShinsalTimelineAnalysis;
import com.dajeom.type.ShinsalEvidenceCertainty;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-6.
 *
 * ShinsalTimelineAnalysis
 * ->
 * 최종 정규화 ShinsalEvidence
 * ->
 * ShinsalAnalysis
 *
 * =================================================
 *
 * 역할:
 *
 * 1.
 * 원국 Match를 Evidence로 변환.
 *
 * 2.
 * Timeline Segment마다 반복될 수 있는
 * 동일 운 Match를 semantic key로 정규화.
 *
 * 3.
 * 실제 적용 Segment를 연결.
 *
 * 4.
 * 모든 가능한 Segment에서 공통이면 DEFINITE.
 *
 * 5.
 * 일부 Scenario에서만 존재하면 POSSIBLE.
 *
 * =================================================
 *
 * 확률 계산은 하지 않는다.
 */
@Component
public class ShinsalEvidenceAnalyzer {

    /**
     * =================================================
     * 공식 17-6 API
     * =================================================
     */
    public ShinsalAnalysis analyze(
            ShinsalTimelineAnalysis timelineAnalysis
    ) {

        Objects.requireNonNull(
                timelineAnalysis,
                "17-6에는 ShinsalTimelineAnalysis가 필요합니다."
        );

        LuckIntegratedAnalysis luckIntegratedAnalysis =
                timelineAnalysis
                        .luckIntegratedAnalysis();

        /*
         * =================================================
         * 원국
         * =================================================
         */

        List<ShinsalEvidence> natalEvidences =
                buildNatalEvidences(
                        timelineAnalysis
                );

        /*
         * =================================================
         * 운
         * =================================================
         */

        List<ShinsalEvidence> luckEvidences =
                buildLuckEvidences(
                        timelineAnalysis
                );

        return new ShinsalAnalysis(
                luckIntegratedAnalysis,
                timelineAnalysis,
                natalEvidences,
                luckEvidences
        );
    }

    /*
     * =================================================
     * NATAL
     * =================================================
     */

    private List<ShinsalEvidence>
    buildNatalEvidences(
            ShinsalTimelineAnalysis timelineAnalysis
    ) {

        List<ShinsalEvidence> result =
                new ArrayList<>();

        for (
                ShinsalRuleMatch match
                : timelineAnalysis.natalMatches()
        ) {

            if (
                    match.matchedPoints()
                            .stream()
                            .anyMatch(
                                    point ->
                                            point.layer()
                                                    != ShinsalLayer.NATAL
                            )
            ) {

                throw new IllegalStateException(
                        "natalMatches에 운 Layer Match가 포함되었습니다."
                );
            }

            result.add(
                    new ShinsalEvidence(
                            match.type(),
                            match.basis(),
                            match.ruleCode(),
                            match.conventionId(),
                            ShinsalEvidenceCertainty.DEFINITE,
                            match.referencePoints(),
                            match.matchedPoints(),
                            match.reasons(),
                            List.of(
                                    match
                            ),
                            List.of()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * LUCK
     * =================================================
     */

    private List<ShinsalEvidence>
    buildLuckEvidences(
            ShinsalTimelineAnalysis timelineAnalysis
    ) {

        /*
         * insertion order:
         *
         * Timeline 순서
         * ->
         * RuleEngine Match 순서
         *
         * 를 유지한다.
         */
        Map<EvidenceKey, EvidenceGroup> groups =
                new LinkedHashMap<>();

        for (
                ShinsalSegmentAnalysis segmentAnalysis
                : timelineAnalysis.segmentAnalyses()
        ) {

            LuckIntegratedSegmentAnalysis segment =
                    segmentAnalysis
                            .segmentAnalysis();

            /*
             * 동일 Segment 안에서 같은 semantic key가
             * 두 번 나오는 경우 방어.
             */
            LinkedHashSet<EvidenceKey> keysSeenInSegment =
                    new LinkedHashSet<>();

            for (
                    ShinsalRuleMatch match
                    : segmentAnalysis.luckMatches()
            ) {

                EvidenceKey key =
                        EvidenceKey.of(
                                match
                        );

                if (
                        !keysSeenInSegment.add(
                                key
                        )
                ) {

                    throw new IllegalStateException(
                            "동일 Timeline Segment 안에서 같은 신살 Evidence Key가 중복되었습니다: "
                                    + key
                    );
                }

                EvidenceGroup group =
                        groups.computeIfAbsent(
                                key,
                                ignored ->
                                        new EvidenceGroup(
                                                match
                                        )
                        );

                group.add(
                        match,
                        segment
                );
            }
        }

        int totalSegmentCount =
                timelineAnalysis
                        .segmentAnalyses()
                        .size();

        List<ShinsalEvidence> result =
                new ArrayList<>(
                        groups.size()
                );

        for (
                Map.Entry<
                        EvidenceKey,
                        EvidenceGroup
                        > entry
                : groups.entrySet()
        ) {

            EvidenceGroup group =
                    entry.getValue();

            ShinsalRuleMatch representative =
                    group
                            .representative();

            ShinsalEvidenceCertainty certainty =
                    group
                            .applicableSegments()
                            .size()
                            == totalSegmentCount

                            ? ShinsalEvidenceCertainty.DEFINITE
                            : ShinsalEvidenceCertainty.POSSIBLE;

            result.add(
                    new ShinsalEvidence(
                            representative.type(),
                            representative.basis(),
                            representative.ruleCode(),
                            representative.conventionId(),
                            certainty,
                            representative.referencePoints(),
                            representative.matchedPoints(),
                            group.mergedReasons(),
                            group.sourceMatches(),
                            group.applicableSegments()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Evidence Group
     * =================================================
     */

    private static final class EvidenceGroup {

        private final ShinsalRuleMatch representative;

        private final List<ShinsalRuleMatch> sourceMatches =
                new ArrayList<>();

        private final List<LuckIntegratedSegmentAnalysis>
                applicableSegments =
                new ArrayList<>();

        private final LinkedHashSet<String> reasons =
                new LinkedHashSet<>();

        private EvidenceGroup(
                ShinsalRuleMatch representative
        ) {

            this.representative =
                    Objects.requireNonNull(
                            representative
                    );
        }

        private void add(

                ShinsalRuleMatch match,

                LuckIntegratedSegmentAnalysis segment

        ) {

            Objects.requireNonNull(
                    match
            );

            Objects.requireNonNull(
                    segment
            );

            EvidenceKey representativeKey =
                    EvidenceKey.of(
                            representative
                    );

            EvidenceKey incomingKey =
                    EvidenceKey.of(
                            match
                    );

            if (
                    !representativeKey
                            .equals(
                                    incomingKey
                            )
            ) {

                throw new IllegalArgumentException(
                        "서로 다른 신살 Match를 동일 EvidenceGroup에 합칠 수 없습니다."
                );
            }

            boolean duplicateSegment =
                    applicableSegments
                            .stream()
                            .anyMatch(
                                    existing ->
                                            existing == segment
                            );

            if (
                    duplicateSegment
            ) {

                throw new IllegalArgumentException(
                        "같은 Segment가 동일 신살 EvidenceGroup에 두 번 추가되었습니다."
                );
            }

            sourceMatches.add(
                    match
            );

            applicableSegments.add(
                    segment
            );

            reasons.addAll(
                    match.reasons()
            );
        }

        private ShinsalRuleMatch representative() {

            return representative;
        }

        private List<ShinsalRuleMatch> sourceMatches() {

            return List.copyOf(
                    sourceMatches
            );
        }

        private List<LuckIntegratedSegmentAnalysis>
        applicableSegments() {

            return List.copyOf(
                    applicableSegments
            );
        }

        private List<String> mergedReasons() {

            return List.copyOf(
                    reasons
            );
        }
    }

    /*
     * =================================================
     * Semantic Key
     * =================================================
     *
     * Luck Point는 Segment Context마다 새 객체일 수 있다.
     *
     * 따라서 객체 Identity가 아니라:
     *
     * Layer 위치
     * +
     * 천간/지지 값
     *
     * 을 사용해 동일 실제 Evidence를 판별한다.
     *
     * 예:
     *
     * Segment A의 ANNUAL 2026 午
     * Segment B의 ANNUAL 2026 午
     *
     * 는 서로 다른 ShinsalPoint 객체여도
     * 같은 Evidence Key다.
     */

    private record EvidenceKey(

            ShinsalType type,

            ShinsalRuleBasis basis,

            String ruleCode,

            String conventionId,

            String referenceSignature,

            String matchedSignature

    ) {

        private EvidenceKey {

            Objects.requireNonNull(
                    type
            );

            Objects.requireNonNull(
                    basis
            );

            Objects.requireNonNull(
                    ruleCode
            );

            Objects.requireNonNull(
                    conventionId
            );

            Objects.requireNonNull(
                    referenceSignature
            );

            Objects.requireNonNull(
                    matchedSignature
            );
        }

        private static EvidenceKey of(
                ShinsalRuleMatch match
        ) {

            Objects.requireNonNull(
                    match
            );

            return new EvidenceKey(
                    match.type(),
                    match.basis(),
                    match.ruleCode(),
                    match.conventionId(),
                    pointSignature(
                            match.referencePoints()
                    ),
                    pointSignature(
                            match.matchedPoints()
                    )
            );
        }
    }

    /*
     * =================================================
     * Point Semantic Signature
     * =================================================
     */

    private static String pointSignature(
            List<ShinsalPoint> points
    ) {

        List<String> signatures =
                new ArrayList<>(
                        points.size()
                );

        for (
                ShinsalPoint point
                : points
        ) {

            String value =
                    point.kind()
                            .heavenlyStem()
                            ? point.requireStem().name()
                            : point.requireBranch().name();

            signatures.add(
                    point.locationKey()
                            + ":"
                            + value
            );
        }

        return String.join(
                ",",
                signatures
        );
    }
}