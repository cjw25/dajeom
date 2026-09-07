package com.dajeom.rule;

import com.dajeom.domain.PatternStemCombinationActivationAnalysis;
import com.dajeom.domain.PatternStemCombinationActivationOccurrence;
import com.dajeom.domain.PatternStemCombinationActivationReason;
import com.dajeom.domain.PatternStemCombinationActivationState;
import com.dajeom.domain.PatternStemCombinationEffectAnalysis;
import com.dajeom.domain.PatternStemCombinationEffectOccurrence;
import com.dajeom.domain.PatternStemCombinationEffectState;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 天干五合 Effect를
 * 실제 격국에서 사용할 수 있는
 * Activation 상태로 한 단계 더 판정한다.
 *
 * =================================================
 *
 * 판정 순서:
 *
 * 1. 本身之合
 *
 * 2. 爭合 / 妬合
 *
 * 3. 隔位 중간 제약
 *
 * 4. 단순 遠合
 *
 * 5. 인접 비일간 active 합
 *
 * =================================================
 *
 * 중간 제약:
 *
 * 甲 ... 庚 ... 己
 *
 * 에서 庚이 甲을 剋하면
 *
 * 甲己 五合 pair 자체는 존재하지만
 * 실제 합 작용에 제약이 있다고 본다.
 *
 * =================================================
 *
 * 이 Rule은 合化 여부를 판정하지 않는다.
 */
@Component
public class PatternStemCombinationActivationRule {

    public PatternStemCombinationActivationAnalysis assess(

            PatternStemCombinationEffectAnalysis effectAnalysis,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        Objects.requireNonNull(
                effectAnalysis,
                "PatternStemCombinationEffectAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        if (
                effectAnalysis.dayMaster()
                        != tenGodInteractions.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "천간합 Effect와 십성 Interaction의 일간이 일치하지 않습니다."
            );
        }

        List<PatternStemCombinationActivationOccurrence> results =
                new ArrayList<>();

        for (
                PatternStemCombinationEffectOccurrence effect
                : effectAnalysis.effects()
        ) {

            results.add(
                    assessOccurrence(
                            effect,
                            tenGodInteractions
                    )
            );
        }

        return new PatternStemCombinationActivationAnalysis(
                effectAnalysis.dayMaster(),
                results
        );
    }

    private PatternStemCombinationActivationOccurrence assessOccurrence(

            PatternStemCombinationEffectOccurrence effect,

            PatternTenGodInteractionAnalysis interactions

    ) {

        EnumSet<PatternStemCombinationActivationReason> reasons =
                EnumSet.noneOf(
                        PatternStemCombinationActivationReason.class
                );

        /*
         * =================================================
         * 1. 本身之合
         * =================================================
         */

        if (
                effect.state()
                        == PatternStemCombinationEffectState
                        .SELF_COMBINATION_NOT_REMOVAL
        ) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .DAY_MASTER_SELF_COMBINATION
            );

            reasons.add(
                    PatternStemCombinationActivationReason
                            .SELF_COMBINATION_NOT_REMOVAL
            );

            return new PatternStemCombinationActivationOccurrence(

                    effect,

                    PatternStemCombinationActivationState
                            .SELF_COMBINATION_NOT_REMOVAL,

                    List.of(),

                    reasons
            );
        }

        /*
         * =================================================
         * 2. 爭合 / 妬合
         * =================================================
         */

        if (
                effect.state()
                        == PatternStemCombinationEffectState
                        .COMPETING_COMBINATION_REVIEW_REQUIRED
        ) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .IMMEDIATE_COMPETING_COMBINATION
            );

            reasons.add(
                    PatternStemCombinationActivationReason
                            .COMPETING_REVIEW_REQUIRED
            );

            return new PatternStemCombinationActivationOccurrence(

                    effect,

                    PatternStemCombinationActivationState
                            .COMPETING_REVIEW_REQUIRED,

                    interveningStems(
                            effect,
                            interactions
                    ),

                    reasons
            );
        }

        /*
         * =================================================
         * 3. 두 합 당사자 사이의 천간들
         * =================================================
         */

        List<PatternTenGodNode> intervening =
                interveningStems(
                        effect,
                        interactions
                );

        if (!intervening.isEmpty()) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .INTERVENING_STEM_EXISTS
            );
        }

        /*
         * 중간 천간이 합 당사자를 실제 剋하는가.
         */
        boolean controlsLeft =
                intervening
                        .stream()
                        .anyMatch(
                                node ->
                                        controls(
                                                node,
                                                effect
                                                        .combination()
                                                        .left(),
                                                interactions
                                        )
                        );

        boolean controlsRight =
                intervening
                        .stream()
                        .anyMatch(
                                node ->
                                        controls(
                                                node,
                                                effect
                                                        .combination()
                                                        .right(),
                                                interactions
                                        )
                        );

        if (controlsLeft) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .INTERVENING_STEM_CONTROLS_LEFT_PARTICIPANT
            );
        }

        if (controlsRight) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .INTERVENING_STEM_CONTROLS_RIGHT_PARTICIPANT
            );
        }

        /*
         * =================================================
         * 4. 有所制而不敢合
         * =================================================
         */

        if (
                controlsLeft
                        ||
                        controlsRight
        ) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .COMBINATION_BLOCKED_BY_CONTROL
            );

            return new PatternStemCombinationActivationOccurrence(

                    effect,

                    PatternStemCombinationActivationState
                            .BLOCKED_BY_INTERVENING_CONTROL,

                    intervening,

                    reasons
            );
        }

        /*
         * =================================================
         * 5. 隔位太遠
         * =================================================
         *
         * Effect 단계에서 이미 distant로 분류됐고
         * 중간 剋까지는 확인되지 않은 경우.
         *
         * 관계 자체를 삭제하지는 않지만
         * 현재 엔진에서 合去로 사용하지 않는다.
         */

        if (
                effect.state()
                        == PatternStemCombinationEffectState
                        .DISTANT_COMBINATION_REVIEW_REQUIRED
        ) {

            reasons.add(
                    PatternStemCombinationActivationReason
                            .DISTANT_COMBINATION
            );

            reasons.add(
                    PatternStemCombinationActivationReason
                            .DISTANT_WITHOUT_DIRECT_CONTROL_BLOCK
            );

            reasons.add(
                    PatternStemCombinationActivationReason
                            .DISTANT_COMBINATION_NOT_USABLE_AS_REMOVAL
            );

            return new PatternStemCombinationActivationOccurrence(

                    effect,

                    PatternStemCombinationActivationState
                            .DISTANT_WEAK_COMBINATION,

                    intervening,

                    reasons
            );
        }

        /*
         * =================================================
         * 6. 인접 비일간 합
         * =================================================
         */

        if (
                effect.state()
                        != PatternStemCombinationEffectState
                        .NON_DAY_MASTER_REMOVAL_CANDIDATE
        ) {

            throw new IllegalStateException(
                    "처리되지 않은 PatternStemCombinationEffectState입니다: "
                            +
                            effect.state()
            );
        }

        if (!intervening.isEmpty()) {

            /*
             * NON_DAY_MASTER_REMOVAL_CANDIDATE는
             * EffectRule상 인접이어야 한다.
             *
             * 여기서 중간 천간이 나온다면
             * 두 분석 결과가 모순이다.
             */
            throw new IllegalStateException(
                    "인접 천간합으로 판정되었지만 합 당사자 사이에 다른 천간이 존재합니다."
            );
        }

        reasons.add(
                PatternStemCombinationActivationReason
                        .NON_DAY_MASTER_COMBINATION
        );

        reasons.add(
                PatternStemCombinationActivationReason
                        .ADJACENT_COMBINATION
        );

        reasons.add(
                PatternStemCombinationActivationReason
                        .ACTIVE_COMBINATION
        );

        return new PatternStemCombinationActivationOccurrence(

                effect,

                PatternStemCombinationActivationState
                        .ACTIVE_NON_DAY_MASTER_COMBINATION,

                List.of(),

                reasons
        );
    }

    /**
     * 두 합 당사자의 물리적 위치 사이에 있는
     * 천간 노드들을 반환한다.
     *
     * 예:
     *
     * YEAR 甲
     * MONTH 庚
     * DAY 己
     *
     * 甲己 사이의 intervening stem
     * = 月庚
     */
    private List<PatternTenGodNode> interveningStems(

            PatternStemCombinationEffectOccurrence effect,

            PatternTenGodInteractionAnalysis interactions

    ) {

        int leftIndex =
                positionIndex(
                        effect
                                .combination()
                                .left()
                                .position()
                );

        int rightIndex =
                positionIndex(
                        effect
                                .combination()
                                .right()
                                .position()
                );

        int start =
                Math.min(
                        leftIndex,
                        rightIndex
                );

        int end =
                Math.max(
                        leftIndex,
                        rightIndex
                );

        if (
                end - start <= 1
        ) {

            return List.of();
        }

        List<PatternTenGodNode> result =
                new ArrayList<>();

        for (
                int index = start + 1;
                index < end;
                index++
        ) {

            PillarPosition position =
                    positionAt(
                            index
                    );

            PatternTenGodNode node =
                    stemNodeAt(
                            position,
                            interactions
                    );

            result.add(
                    node
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * source 천간이 target 천간을
     * raw 오행 graph에서 직접 剋하는가.
     */
    private boolean controls(

            PatternTenGodNode source,

            PatternTenGodNode target,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return interactions
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .equals(
                                                        source
                                                )

                                        &&

                                        link.target()
                                                .equals(
                                                        target
                                                )
                );
    }

    /**
     * YEAR/MONTH/DAY/HOUR 위치에 대응하는
     * 천간 노드를 가져온다.
     *
     * DAY는 DayMasterNode,
     * 나머지는 VisibleStemNode.
     */
    private PatternTenGodNode stemNodeAt(

            PillarPosition position,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return interactions
                .nodes()
                .stream()
                .filter(
                        node ->
                                !node.hidden()
                )
                .filter(
                        node ->
                                node.position()
                                        == position
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        position
                                                +
                                                " 위치의 천간 노드를 찾을 수 없습니다."
                                )
                );
    }

    private int positionIndex(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return switch (position) {

            case YEAR -> 0;

            case MONTH -> 1;

            case DAY -> 2;

            case HOUR -> 3;
        };
    }

    private PillarPosition positionAt(
            int index
    ) {

        return switch (index) {

            case 0 -> PillarPosition.YEAR;

            case 1 -> PillarPosition.MONTH;

            case 2 -> PillarPosition.DAY;

            case 3 -> PillarPosition.HOUR;

            default ->
                    throw new IllegalArgumentException(
                            "유효하지 않은 기둥 위치 index입니다: "
                                    +
                                    index
                    );
        };
    }
}