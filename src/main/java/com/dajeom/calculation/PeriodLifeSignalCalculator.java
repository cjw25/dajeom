package com.dajeom.calculation;

import com.dajeom.domain.LifePeriodKey;
import com.dajeom.domain.PeriodLifeSignal;
import com.dajeom.domain.PeriodLifeSignalCode;
import com.dajeom.domain.PeriodMyeongriInfluence;
import com.dajeom.domain.PeriodMyeongriInfluenceCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정규화된 운 명리 영향:
 *
 * PeriodMyeongriInfluence
 *
 * 를
 *
 * 지나온 흐름 해석 직전의 구조 Signal:
 *
 * PeriodLifeSignal
 *
 * 로 변환한다.
 *
 * =================================================
 *
 * 구조
 * =================================================
 *
 * Daewoon / Sewoon / Wolwoon 계산
 *
 *      ↓ Adapter
 *
 * PeriodMyeongriInfluence
 *
 *      ↓ 여기
 *
 * PeriodLifeSignal
 *
 *      ↓ PastFlowLifeClaimCalculator
 *
 * PeriodLifeClaim
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Calculator는 실제 사건을 생성하지 않는다.
 *
 * 예:
 *
 * WEALTH_ACTIVATED
 *
 * →
 *
 * WEALTH_PRESSURE_INCREASED
 *
 * 까지만 판단한다.
 *
 * "돈을 벌었다"
 * "취업했다"
 * "결혼했다"
 *
 * 같은 실제 사건으로 변환하지 않는다.
 */
@Component
public class PeriodLifeSignalCalculator {

    public List<PeriodLifeSignal> calculate(
            List<PeriodMyeongriInfluence> influences
    ) {

        Objects.requireNonNull(
                influences,
                "PeriodMyeongriInfluence 목록은 필수입니다."
        );

        if (influences.isEmpty()) {

            return List.of();
        }

        /*
         * =================================================
         * 입력 검증 + 정렬
         * =================================================
         *
         * Stream Comparator 타입 추론에 의존하지 않고
         * PeriodMyeongriInfluence 타입을 명시적으로 사용한다.
         */

        List<PeriodMyeongriInfluence> ordered =
                new ArrayList<>();

        for (
                PeriodMyeongriInfluence influence
                : influences
        ) {

            ordered.add(
                    Objects.requireNonNull(
                            influence,
                            "PeriodMyeongriInfluence 목록에 null이 포함될 수 없습니다."
                    )
            );
        }

        ordered.sort(

                Comparator
                        .comparing(
                                (PeriodMyeongriInfluence influence) ->
                                        influence
                                                .period()
                                                .startInclusive()
                        )
                        .thenComparing(
                                (PeriodMyeongriInfluence influence) ->
                                        influence
                                                .period()
                                                .key()
                        )
                        .thenComparing(
                                influence ->
                                        influence
                                                .code()
                                                .ordinal()
                        )
        );

        /*
         * =================================================
         * 기간별 Signal 병합
         * =================================================
         *
         * 같은 기간에 동일 Signal이 여러 번 발생해도
         * EnumSet을 사용하여 하나만 유지한다.
         */

        Map<
                LifePeriodKey,
                EnumSet<PeriodLifeSignalCode>
                > signalsByPeriod =
                new LinkedHashMap<>();

        for (
                PeriodMyeongriInfluence influence
                : ordered
        ) {

            EnumSet<PeriodLifeSignalCode> periodSignals =
                    signalsByPeriod
                            .computeIfAbsent(

                                    influence.period(),

                                    ignored ->
                                            EnumSet.noneOf(
                                                    PeriodLifeSignalCode.class
                                            )
                            );

            mapInfluence(
                    influence.code(),
                    periodSignals
            );
        }

        /*
         * =================================================
         * 최종 PeriodLifeSignal 생성
         * =================================================
         *
         * Signal enum 선언 순서를 사용하여
         * 결과 순서를 항상 동일하게 유지한다.
         */

        List<PeriodLifeSignal> result =
                new ArrayList<>();

        for (
                Map.Entry<
                        LifePeriodKey,
                        EnumSet<PeriodLifeSignalCode>
                        > entry
                : signalsByPeriod.entrySet()
        ) {

            LifePeriodKey period =
                    entry.getKey();

            EnumSet<PeriodLifeSignalCode> periodSignals =
                    entry.getValue();

            for (
                    PeriodLifeSignalCode signalCode
                    : PeriodLifeSignalCode.values()
            ) {

                if (
                        !periodSignals.contains(
                                signalCode
                        )
                ) {

                    continue;
                }

                result.add(
                        new PeriodLifeSignal(
                                period,
                                signalCode
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Influence → Signal
     * =================================================
     */

    private void mapInfluence(

            PeriodMyeongriInfluenceCode influence,

            EnumSet<PeriodLifeSignalCode> signals

    ) {

        Objects.requireNonNull(
                influence,
                "PeriodMyeongriInfluenceCode는 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "PeriodLifeSignal Set은 필수입니다."
        );

        switch (influence) {

            /*
             * =================================================
             * 印
             * =================================================
             */

            case RESOURCE_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .SUPPORT_INCREASED
                    );

            case RESOURCE_WEAKENED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .SUPPORT_DECREASED
                    );

            /*
             * =================================================
             * 食傷
             * =================================================
             */

            case OUTPUT_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .OUTPUT_INCREASED
                    );

            /*
             * =================================================
             * 財
             * =================================================
             */

            case WEALTH_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .WEALTH_PRESSURE_INCREASED
                    );

            /*
             * =================================================
             * 官殺
             * =================================================
             */

            case AUTHORITY_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .AUTHORITY_PRESSURE_INCREASED
                    );

            /*
             * =================================================
             * 比劫
             * =================================================
             */

            case COMPANION_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .PEER_COMPETITION_INCREASED
                    );

            /*
             * =================================================
             * 원국 구조 교란
             * =================================================
             */

            case NATAL_STRUCTURE_DISRUPTION_ACTIVATED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .STRUCTURAL_FRICTION_ACTIVATED
                    );

            case NATAL_STRUCTURE_DISRUPTION_RESOLVED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .STRUCTURAL_FRICTION_RESOLVED
                    );

            /*
             * =================================================
             * 天干合 등 결속
             * =================================================
             */

            case ACTIVE_BINDING_ESTABLISHED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .BINDING_ACTIVATED
                    );

            /*
             * =================================================
             * 원국 중심 방향
             * =================================================
             */

            case NATAL_DIRECTION_REINFORCED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .NATAL_DIRECTION_REINFORCED
                    );

            case NATAL_DIRECTION_CHALLENGED ->

                    signals.add(
                            PeriodLifeSignalCode
                                    .NATAL_DIRECTION_CHALLENGED
                    );
        }
    }
}