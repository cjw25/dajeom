package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemClashType;
import com.dajeom.type.VisibleStemPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-2.
 *
 * 원국 전체 천간 충·극 관계 분석.
 *
 * =================================================
 *
 * 보존하는 것:
 *
 * - 천간충
 * - 일반 극제
 * - 극하는 방향
 * - 극받는 방향
 * - 위치
 * - 인접 여부
 * - 일간 관여
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 길흉
 * - 실제 사건
 * - 합과 충 중 무엇이 최종 우세한지
 * - 용신 손상 여부의 최종 판정
 *
 * =================================================
 *
 * 다만 Evidence가 controllerElement /
 * controlledElement를 제공하므로
 *
 * 8-10에서 UsefulGodAnalysis와 연결하여:
 *
 * "기신이 용신을 극한다"
 * "희신이 기신을 제어한다"
 *
 * 같은 구조를 계산할 수 있다.
 */
public record HeavenlyStemControlAnalysis(

        HeavenlyStem dayMaster,

        List<HeavenlyStemControlEvidence> evidences,

        int knownStemCount,

        boolean completeChart

) {

    public HeavenlyStemControlAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        int expectedKnownStemCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownStemCount
                        != expectedKnownStemCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성과 알려진 천간 수가 일치하지 않습니다."
            );
        }

        /*
         * 같은 두 위치 사이에는
         * 하나의 극제 관계만 존재할 수 있다.
         *
         * 방향이 달라도 같은 Pair이므로
         * 위치를 정렬해 중복 검증한다.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                HeavenlyStemControlEvidence evidence
                : evidences
        ) {

            int first =
                    Math.min(
                            evidence
                                    .controllerPosition()
                                    .ordinal(),
                            evidence
                                    .controlledPosition()
                                    .ordinal()
                    );

            int second =
                    Math.max(
                            evidence
                                    .controllerPosition()
                                    .ordinal(),
                            evidence
                                    .controlledPosition()
                                    .ordinal()
                    );

            String key =
                    first + ":" + second;

            if (
                    !positionPairs.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 천간 위치 Pair의 극제 Evidence가 중복되었습니다: "
                                + key
                );
            }

            if (
                    evidence.controllerPosition()
                            == VisibleStemPosition.DAY

                            && evidence.controllerStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "극제 주체 DAY 천간이 일간과 일치하지 않습니다."
                );
            }

            if (
                    evidence.controlledPosition()
                            == VisibleStemPosition.DAY

                            && evidence.controlledStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "피극 DAY 천간이 일간과 일치하지 않습니다."
                );
            }
        }
    }

    public boolean hasControlRelation() {

        return !evidences
                .isEmpty();
    }

    public int relationCount() {

        return evidences
                .size();
    }

    public List<HeavenlyStemControlEvidence> clashes() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::clash
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence> controlOnlyRelations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::controlOnly
                )
                .toList();
    }

    public int clashCount() {

        return clashes()
                .size();
    }

    public int controlOnlyCount() {

        return controlOnlyRelations()
                .size();
    }

    public boolean hasClash() {

        return !clashes()
                .isEmpty();
    }

    public List<HeavenlyStemControlEvidence> adjacentRelations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::adjacent
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence> adjacentClashes() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::clash
                )
                .filter(
                        HeavenlyStemControlEvidence::adjacent
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence> dayMasterRelations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence
                                ::dayMasterInvolved
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence> dayMasterClashes() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::clash
                )
                .filter(
                        HeavenlyStemControlEvidence
                                ::dayMasterInvolved
                )
                .toList();
    }

    /**
     * 일간이 직접 극받는 관계.
     */
    public List<HeavenlyStemControlEvidence>
    dayMasterControlledRelations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence
                                ::dayMasterUnderControl
                )
                .toList();
    }

    /**
     * 일간이 다른 천간을 극하는 관계.
     */
    public List<HeavenlyStemControlEvidence>
    dayMasterControllerRelations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence
                                ::dayMasterExertsControl
                )
                .toList();
    }

    public boolean dayMasterInControlRelation() {

        return !dayMasterRelations()
                .isEmpty();
    }

    public boolean dayMasterClashed() {

        return !dayMasterClashes()
                .isEmpty();
    }

    public boolean dayMasterUnderControl() {

        return !dayMasterControlledRelations()
                .isEmpty();
    }

    public boolean dayMasterExertsControl() {

        return !dayMasterControllerRelations()
                .isEmpty();
    }

    public List<HeavenlyStemControlEvidence>
    relationsInvolving(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        stem
                                )
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence>
    relationsInvolving(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        position
                                )
                )
                .toList();
    }

    /**
     * 해당 오행이 극하거나 극받는
     * 모든 천간 관계.
     */
    public List<HeavenlyStemControlEvidence>
    relationsInvolvingElement(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        element
                                )
                )
                .toList();
    }

    /**
     * 해당 오행이 다른 오행을
     * 실제로 극하는 Evidence.
     */
    public List<HeavenlyStemControlEvidence>
    relationsControlledBy(
            FiveElement controllerElement
    ) {

        if (
                controllerElement == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.controllerElement()
                                        == controllerElement
                )
                .toList();
    }

    /**
     * 해당 오행이 실제로
     * 극받는 Evidence.
     */
    public List<HeavenlyStemControlEvidence>
    relationsControlling(
            FiveElement controlledElement
    ) {

        if (
                controlledElement == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.controlledElement()
                                        == controlledElement
                )
                .toList();
    }

    public List<HeavenlyStemControlEvidence>
    clashesOf(
            HeavenlyStemClashType clashType
    ) {

        if (
                clashType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        HeavenlyStemControlEvidence::clash
                )
                .filter(
                        evidence ->
                                evidence.clashType()
                                        == clashType
                )
                .toList();
    }

    public boolean hasClashType(
            HeavenlyStemClashType clashType
    ) {

        return !clashesOf(
                clashType
        )
                .isEmpty();
    }

    /**
     * 두 위치 사이에 실제 극제 관계가 있는지.
     *
     * 방향과 상관없이 위치 Pair로 조회한다.
     */
    public Optional<HeavenlyStemControlEvidence>
    relationBetween(

            VisibleStemPosition position1,

            VisibleStemPosition position2

    ) {

        if (
                position1 == null
                        || position2 == null
                        || position1 == position2
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        position1
                                )
                                        && evidence.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}