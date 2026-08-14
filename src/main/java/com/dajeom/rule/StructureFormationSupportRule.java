package com.dajeom.rule;

import com.dajeom.type.StructureSupportLevel;
import com.dajeom.type.StructureSupportPatternType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-5.
 *
 * 격 후보별로 어떤 성격(成格)
 * 보조 패턴을 확인해야 하는지 정의한다.
 *
 * 그리고 해당 보조 오행의:
 *
 * - 실제 세력
 * - 전체 오행 비중
 * - 표면 천간 존재
 * - 지장간 투출
 *
 * 을 이용해 SupportLevel을 판정한다.
 *
 * 주의:
 *
 * 합충형파해 및 합화는
 * 아직 계산하지 않는다.
 *
 * 그 영향은 정밀화 8차/9차 이후
 * 격국 구조를 재평가할 때 연결할 수 있다.
 */
@Component
public class StructureFormationSupportRule {

    /**
     * 전체 오행 세력의 18% 이상이면서
     * 실제 천간 표현까지 존재하면
     * 강한 성격 Evidence로 본다.
     */
    private static final int
            STRONG_SUPPORT_SHARE =
            1_800;

    /**
     * 10% 이상이면
     * 기본적으로 실질적인 보조 세력이
     * 존재한다고 본다.
     */
    private static final int
            ACTIVE_SUPPORT_SHARE =
            1_000;

    /**
     * 격 후보별로 확인할
     * 성격 패턴.
     */
    public List<StructureSupportPatternType>
    requiredPatterns(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "격 후보 십성이 필요합니다."
        );

        return switch (
                tenGod
                ) {

            /*
             * 비견/겁재는 일반 내격 성격 분석에서
             * 제외하고 5-8 특수 구조로 넘긴다.
             */
            case COMPANION,
                 ROB_WEALTH ->
                    List.of();

            /*
             * 식신격.
             *
             * 식상의 흐름이 재성으로
             * 이어지는 식신생재를 확인.
             */
            case EATING_GOD ->
                    List.of(
                            StructureSupportPatternType
                                    .OUTPUT_CONNECTED_TO_WEALTH
                    );

            /*
             * 상관격.
             *
             * 상관생재
             * 상관패인
             *
             * 두 방향을 모두 보존한다.
             */
            case HURTING_OFFICER ->
                    List.of(
                            StructureSupportPatternType
                                    .OUTPUT_CONNECTED_TO_WEALTH,

                            StructureSupportPatternType
                                    .HURTING_OFFICER_REGULATED_BY_RESOURCE
                    );

            /*
             * 편재 / 정재.
             *
             * 식상생재 구조 확인.
             */
            case INDIRECT_WEALTH,
                 DIRECT_WEALTH ->
                    List.of(
                            StructureSupportPatternType
                                    .WEALTH_SUPPORTED_BY_OUTPUT
                    );

            /*
             * 편관 / 칠살.
             *
             * 제살
             * 화살
             *
             * 두 방향을 각각 확인한다.
             */
            case SEVEN_KILLINGS ->
                    List.of(
                            StructureSupportPatternType
                                    .KILLING_CONTROLLED_BY_OUTPUT,

                            StructureSupportPatternType
                                    .KILLING_TRANSFORMED_BY_RESOURCE
                    );

            /*
             * 정관.
             *
             * 재생관
             * 관인상생
             */
            case DIRECT_OFFICER ->
                    List.of(
                            StructureSupportPatternType
                                    .OFFICER_SUPPORTED_BY_WEALTH,

                            StructureSupportPatternType
                                    .OFFICER_CONNECTED_TO_RESOURCE
                    );

            /*
             * 편인 / 정인.
             *
             * 관성이 인성을 생하는
             * 관인상생 흐름 확인.
             */
            case INDIRECT_RESOURCE,
                 DIRECT_RESOURCE ->
                    List.of(
                            StructureSupportPatternType
                                    .RESOURCE_SUPPORTED_BY_OFFICER
                    );
        };
    }

    public StructureSupportLevel determineLevel(

            int elementFinalPower,

            int elementShareBasisPoints,

            int visibleSourceCount,

            int hiddenSourceCount,

            int exposedHiddenSourceCount

    ) {

        if (
                elementFinalPower < 0
                        || elementShareBasisPoints < 0
                        || elementShareBasisPoints > 10_000
                        || visibleSourceCount < 0
                        || hiddenSourceCount < 0
                        || exposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "성격 요소 분석값이 올바르지 않습니다."
            );
        }

        if (
                exposedHiddenSourceCount
                        > hiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출 지장간 수가 전체 지장간 수보다 클 수 없습니다."
            );
        }

        if (
                elementFinalPower <= 0
        ) {

            return StructureSupportLevel
                    .NONE;
        }

        boolean expressed =
                visibleSourceCount > 0
                        || exposedHiddenSourceCount > 0;

        /*
         * 충분한 세력
         * +
         * 실제 천간 표현.
         */
        if (
                elementShareBasisPoints
                        >= STRONG_SUPPORT_SHARE

                        && expressed
        ) {

            return StructureSupportLevel
                    .STRONG;
        }

        /*
         * 일정 세력 이상.
         */
        if (
                elementShareBasisPoints
                        >= ACTIVE_SUPPORT_SHARE
        ) {

            return StructureSupportLevel
                    .ACTIVE;
        }

        /*
         * 세력 비중은 작아도
         * 실제 천간으로 표현돼 있다면
         * 완전히 약한 신호로 버리지 않는다.
         */
        if (
                expressed
        ) {

            return StructureSupportLevel
                    .ACTIVE;
        }

        return StructureSupportLevel
                .WEAK;
    }
}