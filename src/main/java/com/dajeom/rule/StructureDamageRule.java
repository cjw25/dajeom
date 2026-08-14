package com.dajeom.rule;

import com.dajeom.model.StructureFormationSupportEvidence;
import com.dajeom.model.StructureSupportSignal;
import com.dajeom.type.StructureDamageLevel;
import com.dajeom.type.StructureDamagePatternType;
import com.dajeom.type.StructureSupportPatternType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-6.
 *
 * 격 후보별 파격 패턴과
 * 실제 파격 수준을 판정한다.
 *
 * 주의:
 *
 * 이 Rule은 최종적으로
 * "파격이다 / 아니다"를 결정하지 않는다.
 *
 * 해당 파격 Evidence의 강도만 계산한다.
 */
@Component
public class StructureDamageRule {

    /**
     * 파격 source 오행이 전체의 18% 이상이고
     * 실제 천간 표현까지 존재하면
     * 강한 파격 Evidence.
     */
    private static final int
            STRONG_DAMAGE_SHARE =
            1_800;

    /**
     * 10% 이상이면
     * 실질적인 파격 source로 본다.
     */
    private static final int
            ACTIVE_DAMAGE_SHARE =
            1_000;

    /**
     * 후보별 검사할 파격 패턴.
     */
    public List<StructureDamagePatternType>
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
             * 비견/겁재 월령은
             * 일반 팔격 파격 분석에서 제외.
             */
            case COMPANION,
                 ROB_WEALTH ->
                    List.of();

            /*
             * 식신격
             * -> 편인도식
             */
            case EATING_GOD ->
                    List.of(
                            StructureDamagePatternType
                                    .EATING_GOD_BLOCKED_BY_INDIRECT_RESOURCE
                    );

            /*
             * 상관격
             * -> 정관과 충돌
             */
            case HURTING_OFFICER ->
                    List.of(
                            StructureDamagePatternType
                                    .HURTING_OFFICER_MEETS_DIRECT_OFFICER
                    );

            /*
             * 재격
             * -> 비겁탈재
             */
            case INDIRECT_WEALTH,
                 DIRECT_WEALTH ->
                    List.of(
                            StructureDamagePatternType
                                    .WEALTH_CONTESTED_BY_COMPANION
                    );

            /*
             * 칠살격
             *
             * 재생살
             * +
             * 제화 부족
             */
            case SEVEN_KILLINGS ->
                    List.of(
                            StructureDamagePatternType
                                    .SEVEN_KILLINGS_STRENGTHENED_BY_WEALTH,

                            StructureDamagePatternType
                                    .SEVEN_KILLINGS_LACKS_REGULATION
                    );

            /*
             * 정관격
             * -> 상관견관
             */
            case DIRECT_OFFICER ->
                    List.of(
                            StructureDamagePatternType
                                    .DIRECT_OFFICER_HURT_BY_HURTING_OFFICER
                    );

            /*
             * 인성격
             * -> 재극인
             */
            case INDIRECT_RESOURCE,
                 DIRECT_RESOURCE ->
                    List.of(
                            StructureDamagePatternType
                                    .RESOURCE_CONTROLLED_BY_WEALTH
                    );
        };
    }

    /**
     * 실제 공격 source가 있는
     * 일반 파격 패턴의 수준.
     */
    public StructureDamageLevel
    determineSourceDamageLevel(

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
                    "파격 분석값이 올바르지 않습니다."
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

        int sourceCount =
                visibleSourceCount
                        + hiddenSourceCount;

        /*
         * exact TenGod 조건을 포함해
         * 실제 일치 source가 없으면
         * 파격 없음.
         */
        if (
                sourceCount <= 0
        ) {

            return StructureDamageLevel
                    .NONE;
        }

        boolean expressed =
                visibleSourceCount > 0
                        || exposedHiddenSourceCount > 0;

        int expressedSourceCount =
                visibleSourceCount
                        + exposedHiddenSourceCount;

        /*
         * 충분한 세력 + 실제 천간 표현.
         */
        if (
                elementShareBasisPoints
                        >= STRONG_DAMAGE_SHARE

                        && expressed
        ) {

            return StructureDamageLevel
                    .STRONG;
        }

        /*
         * 여러 개의 표현 source가 있으면서
         * 기본 세력도 충분한 경우 역시
         * 강한 파격 신호.
         */
        if (
                expressedSourceCount >= 2

                        && elementShareBasisPoints
                        >= ACTIVE_DAMAGE_SHARE
        ) {

            return StructureDamageLevel
                    .STRONG;
        }

        /*
         * 일정 세력 이상 또는
         * 천간에서 실제 표현.
         */
        if (
                elementShareBasisPoints
                        >= ACTIVE_DAMAGE_SHARE

                        || expressed
        ) {

            return StructureDamageLevel
                    .ACTIVE;
        }

        return StructureDamageLevel
                .WEAK;
    }

    /**
     * 칠살 제화 부족.
     *
     * 이건 공격 source의 존재가 아니라:
     *
     * - 식상제살
     * - 인성화살
     *
     * 두 성격 구조가 충분한지 확인한다.
     */
    public StructureDamageLevel
    determineSevenKillingsRegulationDamage(
            StructureFormationSupportEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence,
                "칠살 성격 Evidence가 필요합니다."
        );

        if (
                evidence.tenGod()
                        != StructureTenGodType.SEVEN_KILLINGS
        ) {

            throw new IllegalArgumentException(
                    "칠살 후보에만 제화 부족 Rule을 적용할 수 있습니다."
            );
        }

        if (
                !evidence.activeCandidate()
        ) {

            return StructureDamageLevel
                    .NONE;
        }

        List<StructureSupportSignal>
                regulationSignals =
                evidence
                        .supportSignals()
                        .stream()
                        .filter(
                                signal ->
                                        signal.patternType()
                                                == StructureSupportPatternType
                                                .KILLING_CONTROLLED_BY_OUTPUT

                                                || signal.patternType()
                                                == StructureSupportPatternType
                                                .KILLING_TRANSFORMED_BY_RESOURCE
                        )
                        .toList();

        boolean meaningfulRegulation =
                regulationSignals
                        .stream()
                        .anyMatch(
                                StructureSupportSignal
                                        ::meaningful
                        );

        /*
         * 식상제살 또는 인성화살 중
         * 하나라도 충분하면
         * 제화 부족 신호 없음.
         */
        if (
                meaningfulRegulation
        ) {

            return StructureDamageLevel
                    .NONE;
        }

        boolean weakRegulationExists =
                regulationSignals
                        .stream()
                        .anyMatch(
                                StructureSupportSignal
                                        ::exists
                        );

        /*
         * 약한 제화 source라도 존재한다면
         * 완전한 무제화 상태보다 한 단계 낮춘다.
         */
        if (
                weakRegulationExists
        ) {

            return StructureDamageLevel
                    .WEAK;
        }

        /*
         * 일간과 칠살 강약 관계 자체까지
         * 부담 상태라면
         * 제화 부족을 강하게 본다.
         */
        if (
                evidence
                        .balanceEvidence()
                        .burdened()
        ) {

            return StructureDamageLevel
                    .STRONG;
        }

        return StructureDamageLevel
                .ACTIVE;
    }
}