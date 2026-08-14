package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationTransformationReasonType;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-3.
 *
 * 하나의 천간합 후보에 대한
 * 최종 합화 성립도 Evidence.
 *
 * =================================================
 *
 * 흐름:
 *
 * 8-1
 * HeavenlyStemCombinationEvidence
 *
 * ↓
 *
 * 9-1
 * HeavenlyStemCombinationCandidate
 *
 * ↓
 *
 * 9-2
 * HeavenlyStemCombinationCompetitionEvidence
 *
 * ↓
 *
 * 9-3
 * HeavenlyStemCombinationTransformationEvidence
 *
 * =================================================
 *
 * 이 Evidence에서 처음으로:
 *
 * TRANSFORMED
 * PARTIALLY_TRANSFORMED
 * CONDITIONAL
 * COMBINED_NOT_TRANSFORMED
 * SUPPRESSED
 *
 * 중 하나를 결정한다.
 *
 * =================================================
 *
 * 하지만 아직 원래 천간 자체를
 * 목표 오행으로 치환하지 않는다.
 *
 * 예:
 *
 * 丁壬合木
 * TRANSFORMED
 *
 * 이라고 해도:
 *
 * 丁 삭제
 * 壬 삭제
 * 木으로 교체
 *
 * 하지 않는다.
 *
 * 원래 기능 보존 여부는 9-9 책임이다.
 */
public record HeavenlyStemCombinationTransformationEvidence(

        HeavenlyStemCombinationCandidate candidate,

        HeavenlyStemCombinationCompetitionEvidence
        competitionEvidence,

        HeavenlyStemCombinationTransformationState state,

        List<HeavenlyStemCombinationTransformationReasonType>
        reasons

) {

    public HeavenlyStemCombinationTransformationEvidence {

        Objects.requireNonNull(
                candidate,
                "9-1 천간합 후보가 필요합니다."
        );

        Objects.requireNonNull(
                competitionEvidence,
                "9-2 경쟁·방해 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "천간 합화 성립 상태가 필요합니다."
        );

        if (
                !competitionEvidence
                        .candidate()
                        .equals(
                                candidate
                        )
        ) {

            throw new IllegalArgumentException(
                    "9-3 후보와 9-2 경쟁 Evidence의 후보가 일치하지 않습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "천간 합화 성립도에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public HeavenlyStemCombinationType combinationType() {

        return candidate
                .combinationType();
    }

    public FiveElement targetElement() {

        return candidate
                .targetElement();
    }

    public VisibleStemPosition firstPosition() {

        return candidate
                .firstPosition();
    }

    public VisibleStemPosition secondPosition() {

        return candidate
                .secondPosition();
    }

    public HeavenlyStem firstStem() {

        return candidate
                .firstStem();
    }

    public HeavenlyStem secondStem() {

        return candidate
                .secondStem();
    }

    public boolean adjacent() {

        return candidate
                .adjacent();
    }

    public boolean dayMasterInvolved() {

        return candidate
                .dayMasterInvolved();
    }

    /*
     * =================================================
     * 9-1
     * =================================================
     */

    public int targetShareBasisPoints() {

        return candidate
                .targetShareBasisPoints();
    }

    public int targetSeasonalRate() {

        return candidate
                .targetSeasonalRate();
    }

    public int monthBranchPresenceWeight() {

        return candidate
                .monthBranchPresenceWeight();
    }

    /*
     * =================================================
     * 9-2
     * =================================================
     */

    public boolean hasCompetition() {

        return competitionEvidence
                .hasCompetition();
    }

    public boolean hasEqualOrStrongerCompetitor() {

        return competitionEvidence
                .hasEqualOrStrongerCompetitor();
    }

    public boolean hasExternalObstruction() {

        return competitionEvidence
                .hasExternalObstruction();
    }

    public boolean adjacentExternalObstructionExists() {

        return competitionEvidence
                .adjacentExternalObstructionExists();
    }

    public int externalObstructionCount() {

        return competitionEvidence
                .externalObstructionCount();
    }

    /*
     * =================================================
     * 9-3 상태
     * =================================================
     */

    public boolean transformed() {

        return state
                .transformed();
    }

    public boolean partiallyTransformed() {

        return state
                .partiallyTransformed();
    }

    public boolean conditional() {

        return state
                .conditional();
    }

    public boolean combinedNotTransformed() {

        return state
                .combinedNotTransformed();
    }

    public boolean suppressed() {

        return state
                .suppressed();
    }

    /**
     * 실제 化 작용이 의미 있게
     * 성립했다고 보는가.
     */
    public boolean meaningfulTransformation() {

        return state
                .meaningfulTransformation();
    }

    public boolean transformationCandidateRemains() {

        return state
                .transformationCandidateRemains();
    }

    /**
     * 合 관계 자체는 8-1에서 이미 탐지되었다.
     *
     * 따라서 COMBINED_NOT_TRANSFORMED도
     * "합 자체가 없다"는 뜻이 아니다.
     */
    public boolean combinationRelationExists() {

        return true;
    }

    public boolean hasReason(
            HeavenlyStemCombinationTransformationReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }

    public boolean involves(
            VisibleStemPosition position
    ) {

        return candidate
                .involves(
                        position
                );
    }

    public boolean involves(
            HeavenlyStem stem
    ) {

        return candidate
                .involves(
                        stem
                );
    }
}