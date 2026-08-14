package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureExposurePriority;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-2.
 *
 * 월령 지장간 격 후보들의
 * 투출 우선순위 분석 결과.
 *
 * orderedEvidences는:
 *
 * "천간에 얼마나 명확하게 표현됐는가"
 *
 * 기준으로 정렬한다.
 *
 * 주의:
 *
 * orderedEvidences.get(0)
 *
 * 이 곧 최종 주격이라는 뜻은 아니다.
 *
 * 특히 정기 미투출 + 중기 투출 구조에서는
 * 중기의 투출 근거가 더 직접적일 수 있지만,
 * 월령 정기의 원천 권위 역시 별도로 유지한다.
 *
 * 실제 격 성립은 5-3에서 판정한다.
 */
public record StructureExposurePriorityAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        StructureExposurePriorityEvidence mainQiEvidence,

        List<StructureExposurePriorityEvidence> orderedEvidences

) {

    public StructureExposurePriorityAnalysis {

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
                "월령 정기 Evidence가 필요합니다."
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
                    "투출 우선순위 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                !mainQiEvidence.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "mainQiEvidence는 월령 정기여야 합니다."
            );
        }

        long mainQiCount =
                orderedEvidences
                        .stream()
                        .filter(
                                StructureExposurePriorityEvidence
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

        boolean containsMainQiEvidence =
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
                !containsMainQiEvidence
        ) {

            throw new IllegalArgumentException(
                    "정렬된 Evidence에 월령 정기가 포함되어 있지 않습니다."
            );
        }
    }

    /**
     * 투출 표현력만 기준으로
     * 가장 높은 후보.
     *
     * 최종 주격이 아니다.
     */
    public StructureExposurePriorityEvidence
    highestExposureEvidence() {

        return orderedEvidences.get(
                0
        );
    }

    public StructureExposurePriority
    highestExposurePriority() {

        return highestExposureEvidence()
                .exposurePriority();
    }

    public List<StructureExposurePriorityEvidence>
    exposedEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureExposurePriorityEvidence
                                ::exposed
                )
                .toList();
    }

    public List<StructureExposurePriorityEvidence>
    hiddenOnlyEvidences() {

        return orderedEvidences
                .stream()
                .filter(
                        StructureExposurePriorityEvidence
                                ::hiddenOnly
                )
                .toList();
    }

    public Optional<StructureExposurePriorityEvidence>
    highestSecondaryExposureEvidence() {

        return orderedEvidences
                .stream()
                .filter(
                        evidence ->
                                !evidence.mainQi()
                )
                .findFirst();
    }

    /**
     * 정기가 실제 천간에 투출했는가.
     */
    public boolean mainQiExposed() {

        return mainQiEvidence.exposed();
    }

    /**
     * 정기가 월간으로 직접 올라왔는가.
     */
    public boolean mainQiExposedAtMonthStem() {

        return mainQiEvidence
                .monthStemExposed();
    }

    /**
     * 정기는 숨겨져 있지만
     * 중기/여기 가운데 하나가
     * 실제 천간에 투출한 구조.
     *
     * 이 경우 5-3에서
     * "투출된 부기 후보가 실제 격 성립에
     * 어느 정도 관여할 수 있는가"
     * 를 별도로 검토해야 한다.
     */
    public boolean secondaryExposedWhileMainHidden() {

        if (
                mainQiExposed()
        ) {

            return false;
        }

        return orderedEvidences
                .stream()
                .anyMatch(
                        evidence ->
                                !evidence.mainQi()
                                        && evidence.exposed()
                );
    }

    /**
     * 월령 정기가 숨겨진 상태에서
     * 투출된 중기/여기가
     * Exposure 순위상 정기보다 위에 있는가.
     *
     * 이것은 주격이 바뀌었다는 뜻이 아니다.
     */
    public boolean secondaryOutranksHiddenMainByExposure() {

        if (
                mainQiExposed()
        ) {

            return false;
        }

        StructureExposurePriorityEvidence highest =
                highestExposureEvidence();

        return !highest.mainQi()
                && highest.exposed();
    }

    /**
     * 둘 이상의 월령 지장간이
     * 동시에 천간으로 투출한 구조.
     *
     * 5-7 혼잡도 판단에 사용 가능하다.
     */
    public boolean multipleCandidatesExposed() {

        return exposedEvidences()
                .size() >= 2;
    }

    /**
     * 정기가 월간으로 직접 올라와
     * 월령과 월간의 연결이 매우 명확한가.
     */
    public boolean directMainQiMonthExpression() {

        return mainQiEvidence
                .mainQiMonthStemExpression();
    }
}