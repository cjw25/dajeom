package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PassageConflictType;
import com.dajeom.type.PassageNeedLevel;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-6.
 *
 * 五行의 다섯 상극 관계 전체에 대해
 * 통관 필요성을 분석한 결과.
 *
 * 모든 상극 관계를 정확히 한 번씩 보존한다.
 *
 * 즉 evidences에는 항상:
 *
 * 木剋土
 * 火剋金
 * 土剋水
 * 金剋木
 * 水剋火
 *
 * 5개가 존재한다.
 *
 * 필요하지 않은 관계도
 * needLevel = NONE으로 보존한다.
 *
 * 이렇게 해야 6-7에서
 * 후보가 왜 선택되지 않았는지도
 * 설명할 수 있다.
 */
public record PassageNeedAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        List<PassageEvidence> evidences,

        PassageNeedLevel overallNeed,

        boolean completeChart

) {

    public PassageNeedAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                overallNeed,
                "전체 통관 필요도가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                evidences.size()
                        != PassageConflictType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "통관 분석에는 五行의 상극 관계 5개가 모두 필요합니다."
            );
        }

        EnumSet<PassageConflictType> conflictTypes =
                EnumSet.noneOf(
                        PassageConflictType.class
                );

        for (
                PassageEvidence evidence
                : evidences
        ) {

            if (
                    !conflictTypes.add(
                            evidence.conflictType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 상극 관계 Evidence가 중복되었습니다: "
                                + evidence.conflictType()
                );
            }
        }

        PassageNeedLevel calculatedOverall =
                evidences
                        .stream()
                        .map(
                                PassageEvidence
                                        ::needLevel
                        )
                        .max(
                                Comparator.comparingInt(
                                        PassageNeedLevel
                                                ::rank
                                )
                        )
                        .orElse(
                                PassageNeedLevel.NONE
                        );

        if (
                calculatedOverall
                        != overallNeed
        ) {

            throw new IllegalArgumentException(
                    "전체 통관 필요도가 Evidence와 일치하지 않습니다."
            );
        }
    }

    public boolean passageRequired() {

        return overallNeed
                .required();
    }

    public boolean meaningfulPassageRequired() {

        return overallNeed
                .meaningful();
    }

    public boolean highPriorityPassageRequired() {

        return overallNeed
                .highPriority();
    }

    public boolean criticalPassageRequired() {

        return overallNeed
                .critical();
    }

    public List<PassageEvidence>
    requiredEvidences() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::passageRequired
                )
                .toList();
    }

    public List<PassageEvidence>
    meaningfulEvidences() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::meaningfulPassageRequired
                )
                .toList();
    }

    public List<PassageEvidence>
    highPriorityEvidences() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::highPriority
                )
                .toList();
    }

    public List<PassageEvidence>
    evidencesInvolvingDayMaster() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::dayMasterInConflict
                )
                .toList();
    }

    public List<PassageEvidence>
    disadvantagedDayMasterEvidences() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::dayMasterDisadvantaged
                )
                .toList();
    }

    public List<PassageEvidence>
    remedyAlignedEvidences() {

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::remedyDirectionAligned
                )
                .toList();
    }

    public Optional<PassageEvidence>
    evidenceOf(
            PassageConflictType conflictType
    ) {

        if (
                conflictType == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.conflictType()
                                        == conflictType
                )
                .findFirst();
    }

    public List<PassageEvidence>
    evidencesUsingBridge(
            FiveElement bridgeElement
    ) {

        if (
                bridgeElement == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.bridgeElement()
                                        == bridgeElement
                )
                .toList();
    }

    public boolean hasMultipleMeaningfulConflicts() {

        return meaningfulEvidences()
                .size() >= 2;
    }

    public boolean hasDayMasterConflict() {

        return !evidencesInvolvingDayMaster()
                .isEmpty();
    }

    public boolean hasDisadvantagedDayMasterConflict() {

        return !disadvantagedDayMasterEvidences()
                .isEmpty();
    }

    public Optional<PassageEvidence>
    strongestEvidence() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                evidence ->
                                        evidence
                                                .needLevel()
                                                .rank()
                        )
                );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}