package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchPunishmentForm;
import com.dajeom.type.EarthlyBranchPunishmentType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-7.
 *
 * 원국 전체 지지 형 분석.
 *
 * =================================================
 *
 * 포함:
 *
 * - 寅巳申 완전 삼형
 * - 寅巳申 부분 삼형
 * - 丑戌未 완전 삼형
 * - 丑戌未 부분 삼형
 * - 子卯 상형
 * - 辰辰 자형
 * - 午午 자형
 * - 酉酉 자형
 * - 亥亥 자형
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 완전 삼형이 존재해도
 * 내부 Pair Evidence를 삭제하지 않는다.
 *
 * 다만:
 *
 * coveredByCompletePunishment
 *
 * 상태를 통해 이후 중복 계산을 막는다.
 */
public record EarthlyBranchPunishmentAnalysis(

        EarthlyBranch dayBranch,

        EarthlyBranch monthBranch,

        List<EarthlyBranchPunishmentEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchPunishmentAnalysis {

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        int expectedKnownBranchCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownBranchCount
                        != expectedKnownBranchCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성과 알려진 지지 수가 일치하지 않습니다."
            );
        }

        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchPunishmentEvidence evidence
                : evidences
        ) {

            String key =
                    evidence.punishmentType().name()
                            + ":"
                            + evidence.form().name()
                            + ":"
                            + evidence.positions();

            if (
                    !uniqueKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 지지 형 Evidence가 중복되었습니다: "
                                + key
                );
            }

            if (
                    evidence.involves(
                            EarthlyBranchPosition.DAY
                    )
                            && !matchesBranchAtPosition(
                            evidence,
                            EarthlyBranchPosition.DAY,
                            dayBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 지지가 실제 일지와 일치하지 않습니다."
                );
            }

            if (
                    evidence.involves(
                            EarthlyBranchPosition.MONTH
                    )
                            && !matchesBranchAtPosition(
                            evidence,
                            EarthlyBranchPosition.MONTH,
                            monthBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "MONTH 위치 지지가 실제 월지와 일치하지 않습니다."
                );
            }
        }
    }

    private static boolean matchesBranchAtPosition(

            EarthlyBranchPunishmentEvidence evidence,

            EarthlyBranchPosition position,

            EarthlyBranch expectedBranch

    ) {

        if (
                evidence.firstPosition()
                        == position
        ) {

            return evidence.firstBranch()
                    == expectedBranch;
        }

        if (
                evidence.secondPosition()
                        == position
        ) {

            return evidence.secondBranch()
                    == expectedBranch;
        }

        if (
                evidence.thirdPosition()
                        == position
        ) {

            return evidence.thirdBranch()
                    == expectedBranch;
        }

        return true;
    }

    public boolean hasPunishment() {

        return !evidences
                .isEmpty();
    }

    public int punishmentEvidenceCount() {

        return evidences
                .size();
    }

    public List<EarthlyBranchPunishmentEvidence>
    completeThreePunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::completeThreePunishment
                )
                .toList();
    }

    public List<EarthlyBranchPunishmentEvidence>
    partialThreePunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::partialThreePunishment
                )
                .toList();
    }

    public List<EarthlyBranchPunishmentEvidence>
    mutualPunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::mutualPunishment
                )
                .toList();
    }

    public List<EarthlyBranchPunishmentEvidence>
    selfPunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::selfPunishment
                )
                .toList();
    }

    public boolean hasCompleteThreePunishment() {

        return !completeThreePunishments()
                .isEmpty();
    }

    public boolean hasPartialThreePunishment() {

        return !partialThreePunishments()
                .isEmpty();
    }

    public boolean hasMutualPunishment() {

        return !mutualPunishments()
                .isEmpty();
    }

    public boolean hasSelfPunishment() {

        return !selfPunishments()
                .isEmpty();
    }

    /**
     * 완전 삼형에 포함되지 않은
     * 독립 부분 삼형.
     */
    public List<EarthlyBranchPunishmentEvidence>
    standalonePartialThreePunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::standalonePartialThree
                )
                .toList();
    }

    /**
     * 완전 삼형 내부의
     * 종속 Pair Evidence.
     */
    public List<EarthlyBranchPunishmentEvidence>
    coveredPartialThreePunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::subordinateToCompletePunishment
                )
                .toList();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchPunishmentEvidence>
    dayBranchPunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchPunished() {

        return !dayBranchPunishments()
                .isEmpty();
    }

    public int dayBranchPunishmentCount() {

        return dayBranchPunishments()
                .size();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchPunishmentEvidence>
    monthBranchPunishments() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    public boolean monthBranchPunished() {

        return !monthBranchPunishments()
                .isEmpty();
    }

    /*
     * =================================================
     * 유형
     * =================================================
     */

    public List<EarthlyBranchPunishmentEvidence>
    punishmentsOf(
            EarthlyBranchPunishmentType punishmentType
    ) {

        if (
                punishmentType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.punishmentType()
                                        == punishmentType
                )
                .toList();
    }

    public boolean hasPunishmentType(
            EarthlyBranchPunishmentType punishmentType
    ) {

        return !punishmentsOf(
                punishmentType
        )
                .isEmpty();
    }

    public List<EarthlyBranchPunishmentEvidence>
    punishmentsOfForm(
            EarthlyBranchPunishmentForm form
    ) {

        if (
                form == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.form()
                                        == form
                )
                .toList();
    }

    /*
     * =================================================
     * 특정 지지 / 궁위
     * =================================================
     */

    public List<EarthlyBranchPunishmentEvidence>
    punishmentsInvolving(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        branch
                                )
                )
                .toList();
    }

    public List<EarthlyBranchPunishmentEvidence>
    punishmentsInvolving(
            EarthlyBranchPosition position
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

    public int punishmentCountOfPosition(
            EarthlyBranchPosition position
    ) {

        return punishmentsInvolving(
                position
        )
                .size();
    }

    public boolean positionHasMultiplePunishments(
            EarthlyBranchPosition position
    ) {

        return punishmentCountOfPosition(
                position
        ) >= 2;
    }

    /**
     * 완전 삼형은 하나의 독립 관계로 보고,
     *
     * 그 내부의 PARTIAL_THREE는 제외한다.
     *
     * 子卯와 자형은 그대로 독립 관계로 유지한다.
     */
    public List<EarthlyBranchPunishmentEvidence>
    independentPunishments() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.completeThreePunishment()
                                        || evidence.mutualPunishment()
                                        || evidence.selfPunishment()
                                        || evidence.standalonePartialThree()
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}