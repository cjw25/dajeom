package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-3.
 *
 * 월령 정기·중기·여기 후보들의
 * 격 성립도 분석 결과.
 *
 * orderedEvidences는:
 *
 * 1. 성립도
 * 2. 월령 source 권위
 * 3. 투출 직접성
 * 4. 실제 오행 비중
 * 5. 월령 내부 weight
 *
 * 순으로 정렬된다.
 *
 * 주의:
 *
 * 첫 번째 후보가 곧 최종 주격이라는 뜻은 아니다.
 *
 * 5-4 ~ 5-9에서:
 *
 * - 일간 신강·신약
 * - 성격
 * - 파격
 * - 혼잡
 * - 특수격/종격
 *
 * 을 추가로 검토한다.
 */
public record StructureCandidateEstablishmentAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        StructureEstablishmentEvidence mainQiEvidence,

        List<StructureEstablishmentEvidence> orderedEvidences

) {

    public StructureCandidateEstablishmentAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                mainQiEvidence,
                "월령 정기 격 Evidence가 필요합니다."
        );

        orderedEvidences =
                orderedEvidences == null
                        ? List.of()
                        : List.copyOf(
                        orderedEvidences
                );

        if (
                orderedEvidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "격 성립 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                !mainQiEvidence.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "mainQiEvidence는 반드시 월령 정기여야 합니다."
            );
        }

        long mainQiCount =
                orderedEvidences
                        .stream()
                        .filter(
                                StructureEstablishmentEvidence
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월령 정기 Evidence는 정확히 하나여야 합니다."
            );
        }

        boolean containsMain =
                orderedEvidences
                        .stream()
                        .anyMatch(
                                evidence ->
                                        evidence.candidate()
                                                .hiddenStem()
                                                == mainQiEvidence
                                                .candidate()
                                                .hiddenStem()
                                                && evidence.mainQi()
                        );

        if (
                !containsMain
        ) {

            throw new IllegalArgumentException(
                    "전체 격 Evidence에 월령 정기가 포함되어 있지 않습니다."
            );
        }
    }

    /**
     * 현재 성립 근거가 가장 높은 후보.
     *
     * 최종 주격은 아니다.
     */
    public StructureEstablishmentEvidence
    highestEstablishmentEvidence() {

        return orderedEvidences.get(
                0
        );
    }

    public List<StructureEstablishmentEvidence>
    establishedEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::established
                )
                .toList();
    }

    public List<StructureEstablishmentEvidence>
    supportedEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::supported
                )
                .toList();
    }

    public List<StructureEstablishmentEvidence>
    meaningfulEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::meaningfulCandidate
                )
                .toList();
    }

    public List<StructureEstablishmentEvidence>
    activeEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::activeCandidate
                )
                .toList();
    }

    public List<StructureEstablishmentEvidence>
    standardInnerStructureEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::standardStructureCandidate
                )
                .toList();
    }

    public List<StructureEstablishmentEvidence>
    specialReviewEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::specialStructureCandidate
                )
                .toList();
    }

    public boolean mainQiEstablished() {

        return mainQiEvidence
                .established();
    }

    public boolean mainQiMeaningful() {

        return mainQiEvidence
                .meaningfulCandidate();
    }

    /**
     * 정기보다 중기/여기 쪽의
     * 성립도가 더 높게 나온 구조.
     *
     * 이것도 주격 변경 확정은 아니다.
     */
    public boolean secondaryHasHigherEstablishment() {

        StructureEstablishmentEvidence highest =
                highestEstablishmentEvidence();

        return !highest.mainQi()
                && highest.level().rank()
                > mainQiEvidence.level().rank();
    }

    /**
     * 둘 이상의 일반 내격 후보가
     * 실제 성립 근거를 갖는 구조.
     *
     * 이후 5-7 혼잡도 분석에서 사용한다.
     */
    public boolean multipleMeaningfulStandardCandidates() {

        return standardInnerStructureEvidences()
                .stream()
                .filter(
                        StructureEstablishmentEvidence
                                ::meaningfulCandidate
                )
                .count() >= 2;
    }

    public boolean requiresSpecialStructureReview() {

        return !specialReviewEvidences()
                .isEmpty();
    }

    public Optional<StructureEstablishmentEvidence>
    evidenceOf(
            StructureTenGodType tenGod
    ) {

        if (
                tenGod == null
        ) {

            return Optional.empty();
        }

        return orderedEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGod()
                                        == tenGod
                )
                .findFirst();
    }

    public int countLevel(
            StructureEstablishmentLevel level
    ) {

        if (
                level == null
        ) {

            return 0;
        }

        return (int) orderedEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.level()
                                        == level
                )
                .count();
    }
}