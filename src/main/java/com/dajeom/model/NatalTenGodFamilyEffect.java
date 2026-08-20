package com.dajeom.model;

import com.dajeom.type.NatalFunctionalActivationLevel;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 십성 Family 하나의
 * 기능적 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 비겁
 * → SELF_ASSERTION
 *
 * 식상
 * → EXPRESSION
 *
 * 재성
 * → RESOURCE_REALITY
 *
 * 관살
 * → RESPONSIBILITY
 *
 * 인성
 * → PROCESSING
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 새로운 십성 세력 점수를 만들지 않는다.
 *
 * 2.
 * 10차 TenGodCompositeAnalysis가
 * 이미 계산한:
 *
 * - Family Power
 * - Family Share
 * - Composite Pattern
 *
 * 을 그대로 사용한다.
 *
 * 3.
 * primaryStructure는
 * 5차 격국의 주격과 정확하게 연결될 때만
 * true다.
 *
 * 4.
 * 격국을 Family Power에 다시 더하지 않는다.
 *
 * 5.
 * 이 단계에서는 아직:
 *
 * - 성격 문장
 * - 길흉
 * - 직업
 * - 재물운
 *
 * 을 결정하지 않는다.
 */
public record NatalTenGodFamilyEffect(

        NatalTenGodFunctionType functionType,

        NatalFunctionalActivationLevel activationLevel,

        int familyPower,

        int familyShareBasisPoints,

        boolean primaryStructure,

        Optional<StructureTenGodType>
        primaryStructureTenGod,

        List<String> meaningfulPatternCodes,

        List<String> strongPatternCodes,

        boolean completeChart

) {

    public NatalTenGodFamilyEffect {

        Objects.requireNonNull(
                functionType,
                "기능축은 필수입니다."
        );

        Objects.requireNonNull(
                activationLevel,
                "활성 수준은 필수입니다."
        );

        if (
                familyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 Family 세력은 음수일 수 없습니다."
            );
        }

        if (
                familyShareBasisPoints < 0
                        || familyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "십성 Family 점유율은 "
                            + "0~10000bp 범위여야 합니다."
            );
        }

        primaryStructureTenGod =
                primaryStructureTenGod == null
                        ? Optional.empty()
                        : primaryStructureTenGod;

        meaningfulPatternCodes =
                meaningfulPatternCodes == null
                        ? List.of()
                        : List.copyOf(
                        meaningfulPatternCodes
                );

        strongPatternCodes =
                strongPatternCodes == null
                        ? List.of()
                        : List.copyOf(
                        strongPatternCodes
                );

        /*
         * Family Power와
         * 활성 상태의 기본 일관성.
         */
        if (
                !activationLevel.present()
                        && familyPower > 0
        ) {

            throw new IllegalArgumentException(
                    "십성 Family 세력이 존재하면 "
                            + "활성 수준이 ABSENT일 수 없습니다."
            );
        }

        if (
                activationLevel.present()
                        && familyPower == 0
        ) {

            throw new IllegalArgumentException(
                    "십성 Family 세력이 0이면 "
                            + "활성 수준은 ABSENT여야 합니다."
            );
        }

        /*
         * 주격 중심성 검증.
         */
        if (
                primaryStructure
        ) {

            StructureTenGodType primary =
                    primaryStructureTenGod
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "주격 중심 기능에는 "
                                                    + "정확한 주격 십성이 필요합니다."
                                    )
                            );

            if (
                    !functionType.contains(
                            primary
                    )
            ) {

                throw new IllegalArgumentException(
                        "주격 십성이 해당 기능축과 "
                                + "일치하지 않습니다."
                );
            }

        } else if (
                primaryStructureTenGod.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "primaryStructure=false이면 "
                            + "주격 십성을 보관할 수 없습니다."
            );
        }

        /*
         * STRONG Pattern은
         * 의미 있는 Pattern에도 포함되어야 한다.
         */
        if (
                !meaningfulPatternCodes
                        .containsAll(
                                strongPatternCodes
                        )
        ) {

            throw new IllegalArgumentException(
                    "강한 Pattern은 "
                            + "유의미 Pattern 목록에도 포함되어야 합니다."
            );
        }
    }

    public boolean present() {

        return activationLevel
                .present();
    }

    public boolean meaningful() {

        return activationLevel
                .meaningful();
    }

    public boolean strong() {

        return activationLevel
                .strong();
    }

    public double familySharePercent() {

        return familyShareBasisPoints
                / 100.0;
    }
}