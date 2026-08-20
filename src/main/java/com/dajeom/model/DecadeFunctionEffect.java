package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 특정 대운 Period가
 * 하나의 원국 기능축에 제공하는
 * 직접적인 기능 Evidence.
 *
 * =================================================
 *
 * 기능축:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * =================================================
 *
 * 이 객체는 아직:
 *
 * - 좋다
 * - 나쁘다
 * - 강화됐다
 * - 약화됐다
 *
 * 를 최종 판정하지 않는다.
 *
 * =================================================
 *
 * 보존하는 것:
 *
 * 1.
 * 대운에서 실제 들어온 십성 Evidence
 *
 * 2.
 * 13-6 Context 중
 * 해당 기능축의 십성을
 * 명시적으로 포함하는 Signal
 *
 * 3.
 * 원국에서 이미 존재하던
 * 해당 기능 관련 Cross Pattern
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * ContextSignal의 SUPPORTED / WEAKENED 등을
 * 곧바로 기능의 좋고 나쁨으로
 * 변환하지 않는다.
 *
 * 여러 상태를 그대로 병렬 보존한다.
 */
public record DecadeFunctionEffect(

        DecadeLuckPeriod period,

        NatalTenGodFunctionType functionType,

        List<DecadeLuckTenGodEvidence>
        directTenGodEvidences,

        List<DecadeLuckContextSignal>
        contextSignals,

        List<NatalCrossFunctionalPattern>
        natalCrossPatterns,

        boolean completeChart

) {

    public DecadeFunctionEffect {

        Objects.requireNonNull(
                period,
                "DecadeFunctionEffect에는 대운 Period가 필요합니다."
        );

        Objects.requireNonNull(
                functionType,
                "DecadeFunctionEffect에는 기능축이 필요합니다."
        );

        directTenGodEvidences =
                directTenGodEvidences == null
                        ? List.of()
                        : List.copyOf(
                        directTenGodEvidences
                );

        contextSignals =
                contextSignals == null
                        ? List.of()
                        : List.copyOf(
                        contextSignals
                );

        natalCrossPatterns =
                natalCrossPatterns == null
                        ? List.of()
                        : List.copyOf(
                        natalCrossPatterns
                );

        /*
         * =================================================
         * 직접 대운 십성 Evidence 검증
         * =================================================
         */

        for (
                DecadeLuckTenGodEvidence evidence
                : directTenGodEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "DecadeFunctionEffect의 직접 십성 Evidence는 null일 수 없습니다."
            );

            if (
                    evidence.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "다른 대운 Period의 십성 Evidence가 "
                                + "DecadeFunctionEffect에 연결되었습니다."
                );
            }

            if (
                    !functionType.contains(
                            evidence.tenGodType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "대운 십성 Evidence가 현재 기능축에 속하지 않습니다. "
                                + "function="
                                + functionType
                                + ", tenGod="
                                + evidence.tenGodType()
                );
            }
        }

        /*
         * =================================================
         * 기능별 Context Signal 검증
         * =================================================
         *
         * tenGodTypes가 비어 있는 Signal은
         * Period 전체 Context이므로
         * 여기 들어오면 안 된다.
         *
         * 그것은 DecadePeriodFunctionalAnalysis의
         * globalContextSignals로 보존한다.
         */

        for (
                DecadeLuckContextSignal signal
                : contextSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "DecadeFunctionEffect의 Context Signal은 null일 수 없습니다."
            );

            if (
                    signal.tenGodTypes()
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "십성 대상이 없는 대운 Context Signal은 "
                                + "기능별 Effect가 아니라 Period 전역 Context로 보존해야 합니다. "
                                + "code="
                                + signal.code()
                );
            }

            boolean functionIncluded =
                    signal
                            .tenGodTypes()
                            .stream()
                            .anyMatch(
                                    functionType::contains
                            );

            if (
                    !functionIncluded
            ) {

                throw new IllegalArgumentException(
                        "대운 Context Signal이 현재 기능축을 포함하지 않습니다. "
                                + "function="
                                + functionType
                                + ", code="
                                + signal.code()
                );
            }
        }

        /*
         * =================================================
         * 원국 Cross Pattern
         * =================================================
         */

        for (
                NatalCrossFunctionalPattern pattern
                : natalCrossPatterns
        ) {

            Objects.requireNonNull(
                    pattern,
                    "DecadeFunctionEffect의 Natal Cross Pattern은 null일 수 없습니다."
            );
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    /*
     * =================================================
     * 대운에서 이 기능이 직접 들어왔는가
     * =================================================
     */

    public boolean directlyEntered() {

        return !directTenGodEvidences
                .isEmpty();
    }

    public int directInputCount() {

        return directTenGodEvidences
                .size();
    }

    /**
     * 대운 천간에서
     * 해당 기능이 직접 드러나는가.
     */
    public boolean visibleInput() {

        return directTenGodEvidences
                .stream()
                .anyMatch(
                        DecadeLuckTenGodEvidence
                                ::visible
                );
    }

    /**
     * 대운 지지 본기에서
     * 해당 기능이 들어오는가.
     */
    public boolean mainHiddenInput() {

        return directTenGodEvidences
                .stream()
                .anyMatch(
                        DecadeLuckTenGodEvidence
                                ::mainHiddenStem
                );
    }

    public boolean hiddenInput() {

        return directTenGodEvidences
                .stream()
                .anyMatch(
                        DecadeLuckTenGodEvidence
                                ::hidden
                );
    }

    /**
     * 실제로 들어온 세부 십성 종류.
     *
     * 개수 점수가 아니라
     * subtype 보존용이다.
     */
    public List<StructureTenGodType>
    directTenGodTypes() {

        Set<StructureTenGodType> result =
                new LinkedHashSet<>();

        for (
                DecadeLuckTenGodEvidence evidence
                : directTenGodEvidences
        ) {

            result.add(
                    evidence.tenGodType()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Context
     * =================================================
     */

    public boolean hasContext() {

        return !contextSignals
                .isEmpty();
    }

    public List<DecadeLuckContextSignal>
    contextSignalsOf(

            DecadeLuckContextAxis axis

    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return contextSignals
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public List<DecadeLuckContextSignal>
    contextSignalsOf(

            DecadeLuckContextStatus status

    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return contextSignals
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    public boolean hasSupportedContext() {

        return !contextSignalsOf(
                DecadeLuckContextStatus.SUPPORTED
        )
                .isEmpty();
    }

    public boolean hasQualifiedContext() {

        return !contextSignalsOf(
                DecadeLuckContextStatus.QUALIFIED
        )
                .isEmpty();
    }

    public boolean hasWeakenedContext() {

        return !contextSignalsOf(
                DecadeLuckContextStatus.WEAKENED
        )
                .isEmpty();
    }

    public boolean hasConflictedContext() {

        return !contextSignalsOf(
                DecadeLuckContextStatus.CONFLICTED
        )
                .isEmpty();
    }

    /**
     * 한 기능에 대해
     * 지지/조건부와 제한/충돌 Context가
     * 동시에 존재하는지 확인.
     *
     * 하나로 평균내지 않는다.
     */
    public boolean mixedContext() {

        boolean supportiveSide =
                hasSupportedContext()
                        || hasQualifiedContext();

        boolean restrictiveSide =
                hasWeakenedContext()
                        || hasConflictedContext();

        return supportiveSide
                && restrictiveSide;
    }

    /*
     * =================================================
     * Natal Cross Pattern Context
     * =================================================
     *
     * 주의:
     *
     * 대운에 해당 기능이 들어왔다고 해서
     * Natal Cross Pattern이 자동으로
     * "활성화되었다"고 단정하지 않는다.
     *
     * 현재 단계에서는
     * 관련 원국 Pattern을
     * Context로만 보존한다.
     */

    public boolean hasNatalCrossPatternContext() {

        return !natalCrossPatterns
                .isEmpty();
    }

    public List<NatalCrossFunctionalPattern>
    effectiveNatalCrossPatterns() {

        return natalCrossPatterns
                .stream()
                .filter(
                        NatalCrossFunctionalPattern
                                ::structurallyEffective
                )
                .toList();
    }

    public List<NatalCrossFunctionalPattern>
    referenceNatalCrossPatterns() {

        return natalCrossPatterns
                .stream()
                .filter(
                        pattern ->
                                !pattern
                                        .structurallyEffective()
                )
                .toList();
    }

    /**
     * 대운에서 기능이 직접 들어오고
     * 원국에 해당 기능이 참여하는
     * 유효 Cross Pattern이 존재하는가.
     *
     * 이것 역시 최종 "활성화 확정"이 아니라
     * 다음 계층에서 검토할 후보조건이다.
     */
    public boolean hasEffectiveNatalPatternContact() {

        return directlyEntered()
                && !effectiveNatalCrossPatterns()
                .isEmpty();
    }

    /**
     * 다음 단계에서 사용할 수 있도록
     * 기능 관련 Context를 상태별로
     * 삭제 없이 연결한다.
     */
    public List<DecadeLuckContextSignal>
    restrictiveContextSignals() {

        List<DecadeLuckContextSignal> result =
                new ArrayList<>();

        result.addAll(
                contextSignalsOf(
                        DecadeLuckContextStatus.WEAKENED
                )
        );

        result.addAll(
                contextSignalsOf(
                        DecadeLuckContextStatus.CONFLICTED
                )
        );

        return List.copyOf(
                result
        );
    }
}