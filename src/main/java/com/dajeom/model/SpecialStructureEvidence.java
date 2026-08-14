package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.SpecialStructureType;

import java.util.Objects;

/**
 * 정밀화 5차-8.
 *
 * 하나의 특수격/종격 후보에 대한 Evidence.
 *
 * 크게 두 종류가 존재한다.
 *
 * 1.
 * 월지 자체가 만드는 특수구조
 *
 * - 건록
 * - 양인
 * - 기타 비겁 월령
 *
 * 2.
 * 4-7 극단 신강·신약 구조에서
 * 전달받는 종격 후보
 *
 * - 종강
 * - 종아
 * - 종재
 * - 종관살
 * - 혼합 종약
 *
 * 중요한 원칙:
 *
 * 이 Evidence가 존재해도
 * 최종 격국 확정은 아니다.
 */
public record SpecialStructureEvidence(

        SpecialStructureType type,

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        DayMasterStrengthLevel strengthLevel,

        int strengthBasisPoints,

        boolean exactMonthBranchMatch,

        boolean inheritedFromExtremeStrength,

        boolean mainQiSpecialDeferred,

        int meaningfulStandardCandidateCount,

        FollowWeakDirection followWeakDirection

) {

    public SpecialStructureEvidence {

        Objects.requireNonNull(
                type,
                "특수구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        if (
                strengthBasisPoints < 0
                        || strengthBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "일간 강도 지수는 0~10000bp 사이여야 합니다."
            );
        }

        if (
                meaningfulStandardCandidateCount < 0
        ) {

            throw new IllegalArgumentException(
                    "일반 내격 후보 수는 음수가 될 수 없습니다."
            );
        }

        validateSourceType(
                type,
                exactMonthBranchMatch,
                inheritedFromExtremeStrength,
                mainQiSpecialDeferred,
                followWeakDirection
        );
    }

    private static void validateSourceType(

            SpecialStructureType type,

            boolean exactMonthBranchMatch,

            boolean inheritedFromExtremeStrength,

            boolean mainQiSpecialDeferred,

            FollowWeakDirection followWeakDirection

    ) {

        /*
         * ================================================
         * 건록 / 양인
         * ================================================
         */
        if (
                type.jianLu()
                        || type.yangRen()
        ) {

            if (
                    !exactMonthBranchMatch
            ) {

                throw new IllegalArgumentException(
                        "건록/양인 후보는 월지의 정확한 일치를 필요로 합니다."
                );
            }

            if (
                    inheritedFromExtremeStrength
            ) {

                throw new IllegalArgumentException(
                        "건록/양인은 4-7 극단구조에서 상속되는 후보가 아닙니다."
                );
            }

            if (
                    !followWeakDirection.none()
            ) {

                throw new IllegalArgumentException(
                        "건록/양인 후보에는 종약 방향을 지정할 수 없습니다."
                );
            }

            return;
        }

        /*
         * ================================================
         * 기타 비겁 월령
         * ================================================
         */
        if (
                type.companionMonthReview()
        ) {

            if (
                    exactMonthBranchMatch
            ) {

                throw new IllegalArgumentException(
                        "일반 비겁 월령 검토는 건록/양인의 정확한 월지 일치 Evidence가 아닙니다."
                );
            }

            if (
                    inheritedFromExtremeStrength
            ) {

                throw new IllegalArgumentException(
                        "비겁 월령 검토는 4-7 극단구조 후보가 아닙니다."
                );
            }

            if (
                    !mainQiSpecialDeferred
            ) {

                throw new IllegalArgumentException(
                        "비겁 월령 특수검토는 일반 내격에서 보류된 월령 Evidence가 필요합니다."
                );
            }

            if (
                    !followWeakDirection.none()
            ) {

                throw new IllegalArgumentException(
                        "비겁 월령 검토에는 종약 방향을 지정할 수 없습니다."
                );
            }

            return;
        }

        /*
         * ================================================
         * 종격 계열
         * ================================================
         */
        if (
                type.followStructure()
        ) {

            if (
                    exactMonthBranchMatch
            ) {

                throw new IllegalArgumentException(
                        "종격 후보는 건록/양인 월지 일치 Evidence를 사용하지 않습니다."
                );
            }

            if (
                    !inheritedFromExtremeStrength
            ) {

                throw new IllegalArgumentException(
                        "종격 후보는 4-7 극단구조 Evidence에서 전달되어야 합니다."
                );
            }

            if (
                    type.followStrong()
            ) {

                if (
                        !followWeakDirection.none()
                ) {

                    throw new IllegalArgumentException(
                            "종강 후보에는 종약 방향을 지정할 수 없습니다."
                    );
                }

                return;
            }

            if (
                    followWeakDirection.none()
            ) {

                throw new IllegalArgumentException(
                        "종약 계열 후보에는 종약 방향이 필요합니다."
                );
            }

            if (
                    type.followOutput()
                            && !followWeakDirection.output()
            ) {

                throw new IllegalArgumentException(
                        "종아 후보와 종약 방향이 일치하지 않습니다."
                );
            }

            if (
                    type.followWealth()
                            && !followWeakDirection.wealth()
            ) {

                throw new IllegalArgumentException(
                        "종재 후보와 종약 방향이 일치하지 않습니다."
                );
            }

            if (
                    type.followOfficer()
                            && !followWeakDirection.officer()
            ) {

                throw new IllegalArgumentException(
                        "종관살 후보와 종약 방향이 일치하지 않습니다."
                );
            }

            if (
                    type.followWeakMixed()
                            && !followWeakDirection.mixed()
            ) {

                throw new IllegalArgumentException(
                        "혼합 종약 후보와 종약 방향이 일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 의미 있는 일반 내격 후보와
     * 특수격 후보가 동시에 존재하는가.
     */
    public boolean generalStructureCompetition() {

        return meaningfulStandardCandidateCount > 0;
    }

    /**
     * 월지 자체의 정확한 위치로
     * 만들어진 특수격 후보.
     */
    public boolean exactMonthStructureCandidate() {

        return exactMonthBranchMatch
                && type.monthStructureBased();
    }

    /**
     * 4-7의 극단 신강·신약 분석에서
     * 전달받은 종격 후보인가.
     */
    public boolean inheritedFollowCandidate() {

        return inheritedFromExtremeStrength
                && type.followStructure();
    }

    public boolean strongSide() {

        return strengthLevel
                .strongSide();
    }

    public boolean weakSide() {

        return strengthLevel
                .weakSide();
    }

    public boolean balancedZone() {

        return strengthLevel
                .balancedZone();
    }

    public double strengthPercent() {

        return strengthBasisPoints
                / 100.0;
    }
}