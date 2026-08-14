package com.dajeom.rule;

import com.dajeom.model.StructureMixingSignal;
import com.dajeom.type.StructureMixingSeverity;
import com.dajeom.type.StructureMixingSignalType;
import com.dajeom.type.StructurePurityLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-7.
 *
 * 격국의 순도·혼잡도를 판정하기 위한
 * 다점 내부 Rule.
 *
 * 주의:
 *
 * 이 규칙은 모든 명리 유파의
 * 절대적인 격국 공식이 아니다.
 *
 * 다점 엔진에서:
 *
 * - 후보 경쟁
 * - 복수 투출
 * - 정편 혼재
 * - 성격/파격 공존
 * - 강한 파격
 *
 * 을 명시적으로 추적하기 위한
 * 구조 판정 정책이다.
 */
@Component
public class StructurePurityRule {

    /**
     * 의미 있는 일반 내격 후보가
     * 둘 이상 존재할 때의 혼잡 수준.
     *
     * ESTABLISHED 후보가 둘 이상이면
     * 중심 격 경쟁이 매우 뚜렷하므로 HIGH.
     *
     * 의미 있는 후보가 3개 이상이어도 HIGH.
     */
    public Optional<StructureMixingSeverity>
    candidateCompetitionSeverity(

            int meaningfulCandidateCount,

            int establishedCandidateCount

    ) {

        if (
                meaningfulCandidateCount < 2
        ) {

            return Optional.empty();
        }

        if (
                establishedCandidateCount >= 2
                        || meaningfulCandidateCount >= 3
        ) {

            return Optional.of(
                    StructureMixingSeverity.HIGH
            );
        }

        return Optional.of(
                StructureMixingSeverity.MODERATE
        );
    }

    /**
     * 월령 지장간 후보의 복수 투출.
     */
    public Optional<StructureMixingSeverity>
    multipleExposureSeverity(
            int exposedCandidateCount
    ) {

        if (
                exposedCandidateCount < 2
        ) {

            return Optional.empty();
        }

        if (
                exposedCandidateCount >= 3
        ) {

            return Optional.of(
                    StructureMixingSeverity.HIGH
            );
        }

        return Optional.of(
                StructureMixingSeverity.MODERATE
        );
    }

    /**
     * 정기 미투출 상태에서
     * 부기가 천간에 표현되는 구조.
     */
    public Optional<StructureMixingSeverity>
    secondaryExpressionSeverity(
            boolean secondaryExposedWhileMainHidden
    ) {

        if (
                !secondaryExposedWhileMainHidden
        ) {

            return Optional.empty();
        }

        return Optional.of(
                StructureMixingSeverity.MODERATE
        );
    }

    /**
     * 같은 십성 그룹 안에서
     * 정·편이 동시에 천간에 표현되는 경우.
     *
     * 관살혼잡은 일반 정편 혼재보다
     * 더 강한 구조적인 혼잡으로 본다.
     */
    public StructureMixingSeverity
    sameFamilyMixingSeverity(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        if (
                tenGod.officerGroup()
        ) {

            return StructureMixingSeverity
                    .HIGH;
        }

        return StructureMixingSeverity
                .MODERATE;
    }

    public StructureMixingSignalType
    sameFamilyMixingSignalType(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        if (
                tenGod.officerGroup()
        ) {

            return StructureMixingSignalType
                    .OFFICER_KILLING_MIXING;
        }

        return StructureMixingSignalType
                .SAME_FAMILY_POLARITY_MIXING;
    }

    /**
     * 같은 십성 그룹에서
     * 반대 음양의 정확한 십성을 반환한다.
     */
    public StructureTenGodType counterpartOf(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        return switch (
                tenGod
                ) {

            case COMPANION ->
                    StructureTenGodType.ROB_WEALTH;

            case ROB_WEALTH ->
                    StructureTenGodType.COMPANION;

            case EATING_GOD ->
                    StructureTenGodType.HURTING_OFFICER;

            case HURTING_OFFICER ->
                    StructureTenGodType.EATING_GOD;

            case INDIRECT_WEALTH ->
                    StructureTenGodType.DIRECT_WEALTH;

            case DIRECT_WEALTH ->
                    StructureTenGodType.INDIRECT_WEALTH;

            case SEVEN_KILLINGS ->
                    StructureTenGodType.DIRECT_OFFICER;

            case DIRECT_OFFICER ->
                    StructureTenGodType.SEVEN_KILLINGS;

            case INDIRECT_RESOURCE ->
                    StructureTenGodType.DIRECT_RESOURCE;

            case DIRECT_RESOURCE ->
                    StructureTenGodType.INDIRECT_RESOURCE;
        };
    }

    /**
     * 성격과 파격이 동시에 의미 있게
     * 작동하는 경우.
     */
    public Optional<StructureMixingSeverity>
    supportDamageCoexistenceSeverity(
            boolean coexist
    ) {

        if (
                !coexist
        ) {

            return Optional.empty();
        }

        return Optional.of(
                StructureMixingSeverity.MODERATE
        );
    }

    /**
     * 5-6에서 STRONG 파격이
     * 확인된 경우.
     */
    public Optional<StructureMixingSeverity>
    strongDamageSeverity(
            boolean strongDamage
    ) {

        if (
                !strongDamage
        ) {

            return Optional.empty();
        }

        return Optional.of(
                StructureMixingSeverity.HIGH
        );
    }

    /**
     * 최종 순도 단계.
     *
     * HIGH 2개 이상
     * 또는
     * HIGH 1개 + MODERATE 2개 이상
     *
     * -> HIGHLY_MIXED
     *
     * HIGH 1개
     * 또는
     * MODERATE 2개 이상
     *
     * -> MIXED
     *
     * MODERATE 1개
     *
     * -> MOSTLY_PURE
     *
     * 신호 없음
     *
     * -> PURE
     */
    public StructurePurityLevel determinePurity(

            boolean evaluationApplicable,

            List<StructureMixingSignal> signals

    ) {

        Objects.requireNonNull(
                signals,
                "혼잡 Signal 목록이 필요합니다."
        );

        if (
                !evaluationApplicable
        ) {

            return StructurePurityLevel
                    .NOT_APPLICABLE;
        }

        int highCount =
                (int) signals
                        .stream()
                        .filter(
                                StructureMixingSignal
                                        ::high
                        )
                        .count();

        int moderateCount =
                (int) signals
                        .stream()
                        .filter(
                                StructureMixingSignal
                                        ::moderate
                        )
                        .count();

        if (
                highCount >= 2

                        || (
                        highCount >= 1
                                && moderateCount >= 2
                )
        ) {

            return StructurePurityLevel
                    .HIGHLY_MIXED;
        }

        if (
                highCount >= 1
                        || moderateCount >= 2
        ) {

            return StructurePurityLevel
                    .MIXED;
        }

        if (
                moderateCount == 1
        ) {

            return StructurePurityLevel
                    .MOSTLY_PURE;
        }

        return StructurePurityLevel
                .PURE;
    }
}