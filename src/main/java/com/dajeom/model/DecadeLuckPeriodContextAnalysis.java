package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-6.
 *
 * 하나의 10년 대운에 대한
 * 최종 교차 Context.
 *
 * =================================================
 *
 * 같은 대운 안에서:
 *
 * SUPPORTED
 * +
 * QUALIFIED
 * +
 * WEAKENED
 * +
 * CONFLICTED
 *
 * 근거가 동시에 존재할 수 있다.
 *
 * 하나의 상태로 강제 축약하지 않는다.
 */
public record DecadeLuckPeriodContextAnalysis(

        HeavenlyStem dayMaster,

        DecadeLuckPeriod period,

        DecadeLuckPeriodTenGodAnalysis tenGodAnalysis,

        DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis,

        List<DecadeLuckContextSignal> signals

) {

    public DecadeLuckPeriodContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운 Context에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "대운 Context에는 Period가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "13-4 대운 십성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "13-5 원국 관계 분석이 필요합니다."
        );

        signals =
                signals == null
                        ? List.of()
                        : List.copyOf(
                        signals
                );

        if (
                tenGodAnalysis.dayMaster()
                        != dayMaster

                        || natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "13-4 / 13-5 / 13-6의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                tenGodAnalysis.period()
                        != period

                        || natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "13-6 입력 분석이 동일한 대운 Period를 참조하지 않습니다."
            );
        }

        for (
                DecadeLuckContextSignal signal
                : signals
        ) {

            Objects.requireNonNull(
                    signal,
                    "대운 Context Signal은 null일 수 없습니다."
            );
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    public SexagenaryPillar luckPillar() {

        return period
                .pillar();
    }

    /*
     * =================================================
     * Axis
     * =================================================
     */

    public List<DecadeLuckContextSignal> signalsOf(
            DecadeLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    /*
     * =================================================
     * Status
     * =================================================
     */

    public List<DecadeLuckContextSignal> signalsOf(
            DecadeLuckContextStatus status
    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    public List<DecadeLuckContextSignal> signalsOf(

            DecadeLuckContextAxis axis,

            DecadeLuckContextStatus status

    ) {

        if (
                axis == null
                        || status == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis

                                        && signal.status()
                                        == status
                )
                .toList();
    }

    /*
     * =================================================
     * Palace
     * =================================================
     */

    public List<DecadeLuckContextSignal> signalsOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        position
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * TenGod
     * =================================================
     */

    public List<DecadeLuckContextSignal> signalsOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        tenGodType
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * 상태
     * =================================================
     */

    public boolean hasSupportedEvidence() {

        return !signalsOf(
                DecadeLuckContextStatus.SUPPORTED
        )
                .isEmpty();
    }

    public boolean hasQualifiedEvidence() {

        return !signalsOf(
                DecadeLuckContextStatus.QUALIFIED
        )
                .isEmpty();
    }

    public boolean hasWeakenedEvidence() {

        return !signalsOf(
                DecadeLuckContextStatus.WEAKENED
        )
                .isEmpty();
    }

    public boolean hasConflictedEvidence() {

        return !signalsOf(
                DecadeLuckContextStatus.CONFLICTED
        )
                .isEmpty();
    }

    /**
     * 유리/부담 근거가 같이 존재하는
     * 복합적인 대운인지 확인.
     *
     * 이를 점수 평균으로 없애지 않는다.
     */
    public boolean mixedContext() {

        boolean supportive =
                hasSupportedEvidence()
                        || hasQualifiedEvidence();

        boolean restrictive =
                hasWeakenedEvidence()
                        || hasConflictedEvidence();

        return supportive
                && restrictive;
    }
}