package com.dajeom.rule;

import com.dajeom.type.StructureBalanceLevel;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차-4.
 *
 * 격 후보의 오행 세력과
 * 일간 신강·신약을 결합한다.
 *
 * 중요한 원칙:
 *
 * 1.
 * 신강·신약을 다시 계산하지 않는다.
 *
 * 2.
 * 격 후보의 오행 세력도 다시 계산하지 않는다.
 *
 * 3.
 * 4차 Strength 결과와
 * 5-3 Establishment 결과를 재사용한다.
 *
 * 4.
 * 이 단계에서 길흉·용신을 판단하지 않는다.
 *
 * =================================================
 *
 * 각 십성 그룹의 기본 방향
 *
 * 비겁/인성
 * -> 일간을 지원
 *
 * 식상
 * -> 일간이 생하여 설기
 *
 * 재성
 * -> 일간이 극하고 감당
 *
 * 관살
 * -> 일간을 직접 극제
 *
 * =================================================
 *
 * 따라서 같은 오행 비중이라도
 * 일간 강약에 요구되는 조건이 다르다.
 */
@Component
public class StructureBalanceRule {

    /*
     * 일간 강도 구간.
     *
     * 10000bp = 내부 강도축 100.
     */

    private static final int
            VERY_WEAK_STRENGTH =
            3_500;

    private static final int
            WEAK_BALANCE_BOUNDARY =
            4_500;

    private static final int
            STRONG_BALANCE_BOUNDARY =
            5_500;

    private static final int
            STRONG_STRENGTH =
            6_500;

    /*
     * 오행 비중 기준.
     */

    private static final int
            LIGHT_ELEMENT_SHARE =
            1_000;

    private static final int
            MODERATE_ELEMENT_SHARE =
            1_500;

    private static final int
            NOTICEABLE_ELEMENT_SHARE =
            1_800;

    private static final int
            STRONG_ELEMENT_SHARE =
            2_500;

    private static final int
            VERY_STRONG_ELEMENT_SHARE =
            3_000;

    public StructureBalanceLevel determineLevel(

            StructureTenGodType tenGod,

            StructureEstablishmentLevel
                    establishmentLevel,

            int dayMasterStrengthBasisPoints,

            int elementShareBasisPoints

    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        Objects.requireNonNull(
                establishmentLevel,
                "격 성립도가 필요합니다."
        );

        validateBasisPoints(
                dayMasterStrengthBasisPoints,
                "일간 강도"
        );

        validateBasisPoints(
                elementShareBasisPoints,
                "격 후보 오행 비중"
        );

        /*
         * 5-3에서 이미 비활성 처리된 후보는
         * 강약 적합도를 적극 평가하지 않는다.
         */
        if (
                establishmentLevel.inactive()
        ) {

            return StructureBalanceLevel
                    .NOT_APPLICABLE;
        }

        if (
                tenGod.resourceGroup()
        ) {

            return determineResourceBalance(
                    dayMasterStrengthBasisPoints,
                    elementShareBasisPoints
            );
        }

        if (
                tenGod.companionGroup()
        ) {

            return determineCompanionBalance(
                    dayMasterStrengthBasisPoints,
                    elementShareBasisPoints
            );
        }

        if (
                tenGod.outputGroup()
        ) {

            return determineOutputBalance(
                    dayMasterStrengthBasisPoints,
                    elementShareBasisPoints
            );
        }

        if (
                tenGod.wealthGroup()
        ) {

            return determineWealthBalance(
                    dayMasterStrengthBasisPoints,
                    elementShareBasisPoints
            );
        }

        if (
                tenGod.officerGroup()
        ) {

            return determineOfficerBalance(
                    dayMasterStrengthBasisPoints,
                    elementShareBasisPoints
            );
        }

        throw new IllegalStateException(
                "격 후보의 십성 그룹을 판정할 수 없습니다: "
                        + tenGod
        );
    }

    /**
     * 인성.
     *
     * 신약한 일간을 생조하는 방향이므로
     * 약한 일간에서는 비교적 수용성이 높다.
     *
     * 반대로 이미 신강한데
     * 인성 비중까지 지나치게 크면
     * 과도한 생조 구조로 분리한다.
     */
    private StructureBalanceLevel
    determineResourceBalance(

            int strength,

            int share

    ) {

        if (
                strength < VERY_WEAK_STRENGTH
        ) {

            if (
                    share >= LIGHT_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            return StructureBalanceLevel
                    .STRAINED;
        }

        if (
                strength < STRONG_BALANCE_BOUNDARY
        ) {

            return StructureBalanceLevel
                    .BALANCED;
        }

        if (
                strength < STRONG_STRENGTH
        ) {

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                share >= STRONG_ELEMENT_SHARE
        ) {

            return StructureBalanceLevel
                    .IMBALANCED;
        }

        return StructureBalanceLevel
                .STRAINED;
    }

    /**
     * 비겁.
     *
     * 일간과 같은 오행의 동류 지원.
     *
     * 일반 내격보다는
     * 건록·양인 등 특수 구조 검토 대상으로
     * 넘어갈 가능성이 높지만,
     * 강약 관계 자체는 여기서 분석한다.
     */
    private StructureBalanceLevel
    determineCompanionBalance(

            int strength,

            int share

    ) {

        if (
                strength < VERY_WEAK_STRENGTH
        ) {

            if (
                    share >= LIGHT_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            return StructureBalanceLevel
                    .STRAINED;
        }

        if (
                strength < STRONG_BALANCE_BOUNDARY
        ) {

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                strength < STRONG_STRENGTH
        ) {

            if (
                    share >= STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                share >= NOTICEABLE_ELEMENT_SHARE
        ) {

            return StructureBalanceLevel
                    .IMBALANCED;
        }

        return StructureBalanceLevel
                .STRAINED;
    }

    /**
     * 식상.
     *
     * 일간이 식상을 생하면서
     * 기운을 밖으로 내보내므로
     * 일간이 어느 정도 힘을 가져야 한다.
     */
    private StructureBalanceLevel
    determineOutputBalance(

            int strength,

            int share

    ) {

        if (
                strength >= STRONG_STRENGTH
        ) {

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            return StructureBalanceLevel
                    .BALANCED;
        }

        if (
                strength >= STRONG_BALANCE_BOUNDARY
        ) {

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                strength >= WEAK_BALANCE_BOUNDARY
        ) {

            if (
                    share >= NOTICEABLE_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                strength >= VERY_WEAK_STRENGTH
        ) {

            if (
                    share < LIGHT_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .IMBALANCED;
            }

            return StructureBalanceLevel
                    .STRAINED;
        }

        if (
                share >= MODERATE_ELEMENT_SHARE
        ) {

            return StructureBalanceLevel
                    .IMBALANCED;
        }

        return StructureBalanceLevel
                .STRAINED;
    }

    /**
     * 재성.
     *
     * 일간이 직접 극하고
     * 관리해야 하는 대상이다.
     *
     * 식상보다 더 분명한
     * 일간의 감당력이 필요하다고 본다.
     */
    private StructureBalanceLevel
    determineWealthBalance(

            int strength,

            int share

    ) {

        if (
                strength >= STRONG_STRENGTH
        ) {

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            return StructureBalanceLevel
                    .BALANCED;
        }

        if (
                strength >= STRONG_BALANCE_BOUNDARY
        ) {

            if (
                    share >= STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                strength >= WEAK_BALANCE_BOUNDARY
        ) {

            if (
                    share >= VERY_STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .IMBALANCED;
            }

            if (
                    share >= MODERATE_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                share >= NOTICEABLE_ELEMENT_SHARE
        ) {

            return StructureBalanceLevel
                    .IMBALANCED;
        }

        return StructureBalanceLevel
                .STRAINED;
    }

    /**
     * 관살.
     *
     * 관살은 상대 오행이
     * 일간을 직접 극하는 관계이므로
     * 약한 일간에서는 가장 엄격하게 본다.
     */
    private StructureBalanceLevel
    determineOfficerBalance(

            int strength,

            int share

    ) {

        if (
                strength >= STRONG_STRENGTH
        ) {

            if (
                    share >= STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .WORKABLE;
            }

            return StructureBalanceLevel
                    .BALANCED;
        }

        if (
                strength >= STRONG_BALANCE_BOUNDARY
        ) {

            if (
                    share >= NOTICEABLE_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                strength >= WEAK_BALANCE_BOUNDARY
        ) {

            if (
                    share >= STRONG_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .IMBALANCED;
            }

            if (
                    share >= LIGHT_ELEMENT_SHARE
            ) {

                return StructureBalanceLevel
                        .STRAINED;
            }

            return StructureBalanceLevel
                    .WORKABLE;
        }

        if (
                share >= MODERATE_ELEMENT_SHARE
        ) {

            return StructureBalanceLevel
                    .IMBALANCED;
        }

        return StructureBalanceLevel
                .STRAINED;
    }

    private void validateBasisPoints(

            int value,

            String name

    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    name
                            + "는 0~10000bp 사이여야 합니다."
            );
        }
    }
}