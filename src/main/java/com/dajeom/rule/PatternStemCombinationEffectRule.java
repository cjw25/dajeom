package com.dajeom.rule;

import com.dajeom.domain.PatternStemCombinationContext;
import com.dajeom.domain.PatternStemCombinationEffectAnalysis;
import com.dajeom.domain.PatternStemCombinationEffectOccurrence;
import com.dajeom.domain.PatternStemCombinationEffectReason;
import com.dajeom.domain.PatternStemCombinationEffectState;
import com.dajeom.domain.PatternStemCombinationOccurrence;
import com.dajeom.domain.PatternStemCombinationParticipation;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 天干五合 occurrence를
 *
 * - 本身之合
 * - 合去 후보
 * - 爭合 검토
 * - 隔位 검토
 *
 * 로 분류한다.
 *
 * =================================================
 *
 * 기본 원칙:
 *
 * 1. 日主가 직접 합하는 경우
 *
 *    → 本身之合
 *    → 合去로 보지 않음
 *
 * 2. 日主 외 두 천간이 인접하여 합
 *
 *    → 合去 후보
 *
 * 3. 동일 천간을 향해
 *    가까운 두 합이 동시에 몰림
 *
 *    → 爭合 / 妬合 검토
 *
 * 4. 합 pair가 서로 멀리 떨어짐
 *
 *    → 隔位 검토
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Rule은 아직 최종 合去를 확정하지 않는다.
 *
 * 이후:
 *
 * - 다른 천간의 剋으로 合이 막히는가
 * - 合化 여부
 *
 * 등을 추가 판정한다.
 */
@Component
public class PatternStemCombinationEffectRule {

    public PatternStemCombinationEffectAnalysis assess(
            PatternStemCombinationContext context
    ) {

        Objects.requireNonNull(
                context,
                "PatternStemCombinationContext는 필수입니다."
        );

        List<PatternStemCombinationEffectOccurrence> effects =
                new ArrayList<>();

        for (
                PatternStemCombinationOccurrence combination
                : context.combinations()
        ) {

            effects.add(
                    assessOccurrence(
                            combination,
                            context
                    )
            );
        }

        return new PatternStemCombinationEffectAnalysis(
                context.dayMaster(),
                effects
        );
    }

    private PatternStemCombinationEffectOccurrence assessOccurrence(

            PatternStemCombinationOccurrence combination,

            PatternStemCombinationContext context

    ) {

        EnumSet<PatternStemCombinationEffectReason> reasons =
                EnumSet.noneOf(
                        PatternStemCombinationEffectReason.class
                );

        /*
         * =================================================
         * 1. 즉시 爭合/妬合 검토
         * =================================================
         *
         * 동일 천간을 중심으로
         * 현재 합 상대와 다른 합 상대가
         * 둘 다 바로 인접해 있으면
         * immediate competing 구조로 본다.
         *
         * 예:
         *
         * 月壬 - 日丁 - 時壬
         *
         * 日丁 기준으로
         * 月壬 / 時壬 모두 인접.
         */
        boolean immediateCompetition =
                hasImmediateCompetingCombination(
                        combination,
                        context
                );

        if (immediateCompetition) {

            reasons.add(
                    PatternStemCombinationEffectReason
                            .IMMEDIATE_COMPETING_COMBINATION
            );

            reasons.add(
                    PatternStemCombinationEffectReason
                            .COMPETING_REVIEW_REQUIRED
            );

            if (combination.involvesDayMaster()) {

                reasons.add(
                        PatternStemCombinationEffectReason
                                .DAY_MASTER_INVOLVED
                );
            } else {

                reasons.add(
                        PatternStemCombinationEffectReason
                                .NON_DAY_MASTER_COMBINATION
                );
            }

            return new PatternStemCombinationEffectOccurrence(

                    combination,

                    PatternStemCombinationEffectState
                            .COMPETING_COMBINATION_REVIEW_REQUIRED,

                    reasons
            );
        }

        /*
         * =================================================
         * 2. 本身之合
         * =================================================
         *
         * 日主가 직접 합하는 경우
         * 合去로 처리하지 않는다.
         */
        if (combination.involvesDayMaster()) {

            reasons.add(
                    PatternStemCombinationEffectReason
                            .DAY_MASTER_INVOLVED
            );

            reasons.add(
                    PatternStemCombinationEffectReason
                            .SELF_COMBINATION_NOT_COMBINED_AWAY
            );

            return new PatternStemCombinationEffectOccurrence(

                    combination,

                    PatternStemCombinationEffectState
                            .SELF_COMBINATION_NOT_REMOVAL,

                    reasons
            );
        }

        /*
         * =================================================
         * 3. 日主 외 두 천간
         * =================================================
         */

        reasons.add(
                PatternStemCombinationEffectReason
                        .NON_DAY_MASTER_COMBINATION
        );

        int distance =
                positionDistance(
                        combination.left().position(),
                        combination.right().position()
                );

        /*
         * =================================================
         * 4. 隔位
         * =================================================
         *
         * 年 - 時
         * 月 - 時
         *
         * 처럼 다른 위치가 사이에 있으면
         * 곧바로 合去로 올리지 않는다.
         */
        if (distance > 1) {

            reasons.add(
                    PatternStemCombinationEffectReason
                            .DISTANT_STEMS
            );

            reasons.add(
                    PatternStemCombinationEffectReason
                            .DISTANT_REVIEW_REQUIRED
            );

            return new PatternStemCombinationEffectOccurrence(

                    combination,

                    PatternStemCombinationEffectState
                            .DISTANT_COMBINATION_REVIEW_REQUIRED,

                    reasons
            );
        }

        /*
         * =================================================
         * 5. 인접한 비일간 천간합
         * =================================================
         *
         * 年干 ↔ 月干 같은 구조.
         *
         * 현재 단계에서는 合去 후보.
         */
        reasons.add(
                PatternStemCombinationEffectReason
                        .ADJACENT_STEMS
        );

        reasons.add(
                PatternStemCombinationEffectReason
                        .REMOVAL_CANDIDATE
        );

        return new PatternStemCombinationEffectOccurrence(

                combination,

                PatternStemCombinationEffectState
                        .NON_DAY_MASTER_REMOVAL_CANDIDATE,

                reasons
        );
    }

    /**
     * 현재 occurrence의 양쪽 노드 중 하나가
     *
     * 다른 occurrence에도 참여하고,
     *
     * 현재 상대와 다른 상대가 모두
     * 해당 노드 바로 옆에 붙어 있는 경우
     *
     * 즉시 爭合 검토 대상으로 본다.
     *
     * =================================================
     *
     * 예:
     *
     * 月壬
     * 日丁
     * 時壬
     *
     * 丁 중심:
     *
     * 月壬 거리 1
     * 時壬 거리 1
     *
     * → true
     *
     * =================================================
     *
     * 반면:
     *
     * 年庚
     * 月乙
     * 日甲
     * 時乙
     *
     * 年庚 기준:
     *
     * 月乙 거리 1
     * 時乙 거리 3
     *
     * → immediate competition 아님
     *
     * 원문의 隔位例와 구분한다.
     */
    private boolean hasImmediateCompetingCombination(

            PatternStemCombinationOccurrence current,

            PatternStemCombinationContext context

    ) {

        return nodeHasImmediateCompetition(
                current.left(),
                current,
                context
        )
                ||
                nodeHasImmediateCompetition(
                        current.right(),
                        current,
                        context
                );
    }

    private boolean nodeHasImmediateCompetition(

            PatternTenGodNode sharedNode,

            PatternStemCombinationOccurrence current,

            PatternStemCombinationContext context

    ) {

        PatternStemCombinationParticipation participation =
                context
                        .participations()
                        .stream()
                        .filter(
                                value ->
                                        value.node()
                                                .equals(
                                                        sharedNode
                                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (
                participation == null
                        ||
                        !participation
                                .competingCombinationCandidate()
        ) {

            return false;
        }

        PatternTenGodNode currentCounterpart =
                counterpart(
                        current,
                        sharedNode
                );

        /*
         * 현재 합 상대부터
         * sharedNode와 인접해야 한다.
         */
        if (
                positionDistance(
                        sharedNode.position(),
                        currentCounterpart.position()
                )
                        != 1
        ) {

            return false;
        }

        for (
                PatternStemCombinationOccurrence other
                : participation.combinations()
        ) {

            if (other.equals(current)) {

                continue;
            }

            PatternTenGodNode otherCounterpart =
                    counterpart(
                            other,
                            sharedNode
                    );

            if (
                    positionDistance(
                            sharedNode.position(),
                            otherCounterpart.position()
                    )
                            == 1
            ) {

                return true;
            }
        }

        return false;
    }

    private PatternTenGodNode counterpart(

            PatternStemCombinationOccurrence occurrence,

            PatternTenGodNode node

    ) {

        if (
                occurrence.left()
                        .equals(node)
        ) {

            return occurrence.right();
        }

        if (
                occurrence.right()
                        .equals(node)
        ) {

            return occurrence.left();
        }

        throw new IllegalArgumentException(
                "요청한 천간 노드가 해당 五合 occurrence에 참여하지 않습니다."
        );
    }

    /**
     * 四柱 위치의 물리적 거리.
     *
     * YEAR  = 0
     * MONTH = 1
     * DAY   = 2
     * HOUR  = 3
     *
     * PillarPosition enum 선언 순서에 의존하지 않는다.
     */
    private int positionDistance(

            PillarPosition first,

            PillarPosition second

    ) {

        return Math.abs(
                positionIndex(first)
                        -
                        positionIndex(second)
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
}