package com.dajeom.engine;

import com.dajeom.model.AnnualDecadeFunctionSegmentEffect;
import com.dajeom.model.AnnualDecadeFunctionalAnalysis;
import com.dajeom.model.AnnualDecadeFunctionalSegment;
import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualDecadeLuckInteractionAnalysis;
import com.dajeom.model.AnnualFunctionCapacityContext;
import com.dajeom.model.AnnualFunctionalCapacityAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.DecadeFunctionalSynthesisAnalysis;
import com.dajeom.model.DecadePeriodFunctionalSynthesisAnalysis;
import com.dajeom.model.DecadeResolvedFunctionEffect;
import com.dajeom.type.AnnualDecadeFunctionContactMode;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 대운 Functional 기준선과
 * 세운 Functional Capacity를
 * 실제 14-5 시간 Segment별로 연결한다.
 *
 * =================================================
 *
 * 입력:
 *
 * DecadeFunctionalSynthesisAnalysis
 *
 * +
 *
 * AnnualFunctionalCapacityAnalysis
 *
 * =================================================
 *
 * 시간 Segment:
 *
 * 기존 14-5
 * AnnualDecadeLuckInteraction
 *
 * 을 그대로 사용한다.
 *
 * =================================================
 *
 * Context:
 *
 * 기존 14-6
 * AnnualLuckDecadeContextAnalysis
 *
 * 를 그대로 사용한다.
 *
 * =================================================
 *
 * 따라서:
 *
 * - 교운시점 재계산 없음
 * - Segment 재분할 없음
 * - 대운 하나 임의선택 없음
 *
 * =================================================
 *
 * 이 단계가 판정하는 것:
 *
 * - 대운 Function 존재
 * - 세운 Function 존재
 * - 동일 Function continuation
 * - 세운 신규 Function
 * - 같은 Function 누적 검토 Candidate
 * - Capacity-side 교차
 * - 14-5 typed relation Context
 * - 14-5 typed transformation Target Function
 *
 * =================================================
 *
 * 아직 하지 않는 것:
 *
 * - 14-5 Transformation의 Source Function별
 *   최종 원기능 감소 재배치
 *
 * - 대운 CrossFunctional Pattern을
 *   세운이 실제 완성하는지 최종 판정
 *
 * - 길흉
 */
@Component
public class AnnualDecadeFunctionalAnalysisEngine {

    public AnnualDecadeFunctionalAnalysis analyze(

            DecadeFunctionalSynthesisAnalysis
                    decadeFunctionalSynthesisAnalysis,

            AnnualFunctionalCapacityAnalysis
                    annualFunctionalCapacityAnalysis

    ) {

        Objects.requireNonNull(
                decadeFunctionalSynthesisAnalysis,
                "Annual-Decade Functional Analysis에는 DecadeFunctionalSynthesisAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionalCapacityAnalysis,
                "Annual-Decade Functional Analysis에는 AnnualFunctionalCapacityAnalysis가 필요합니다."
        );

        boolean completeChart =
                annualFunctionalCapacityAnalysis
                        .completeChart();

        if (
                decadeFunctionalSynthesisAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Decade Functional Synthesis와 Annual Functional Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        AnnualLuckAnalysis annualLuckAnalysis =
                annualFunctionalCapacityAnalysis
                        .projectionAnalysis()
                        .annualLuckAnalysis();

        AnnualDecadeLuckInteractionAnalysis interactionAnalysis =
                annualLuckAnalysis
                        .decadeInteractionAnalysis();

        /*
         * =================================================
         * 14-5 unavailable
         * =================================================
         *
         * Annual Functional 자체는 버리지 않는다.
         */

        if (
                !interactionAnalysis
                        .analysisAvailable()
        ) {

            String reason =
                    interactionAnalysis
                            .unavailableReason();

            if (
                    reason == null
                            || reason.isBlank()
            ) {

                reason =
                        "대운 Sequence가 확정되지 않아 Annual-Decade Functional Segment를 생성할 수 없습니다.";
            }

            return new AnnualDecadeFunctionalAnalysis(
                    decadeFunctionalSynthesisAnalysis,
                    annualFunctionalCapacityAnalysis,
                    List.of(),
                    false,
                    reason,
                    completeChart
            );
        }

        /*
         * =================================================
         * Decade Functional unavailable
         * =================================================
         */

        if (
                !decadeFunctionalSynthesisAnalysis
                        .analysisAvailable()
        ) {

            String reason =
                    decadeFunctionalSynthesisAnalysis
                            .unavailableReason()
                            .orElse(
                                    "대운 Functional Synthesis를 사용할 수 없습니다."
                            );

            return new AnnualDecadeFunctionalAnalysis(
                    decadeFunctionalSynthesisAnalysis,
                    annualFunctionalCapacityAnalysis,
                    List.of(),
                    false,
                    reason,
                    completeChart
            );
        }

        /*
         * =================================================
         * 14-6 Segment Context
         * =================================================
         */

        if (
                !annualLuckAnalysis
                        .decadeContextAvailable()
        ) {

            String reason =
                    annualLuckAnalysis
                            .decadeContextUnavailableReason()
                            .orElse(
                                    "14-6 대운 Segment Context를 사용할 수 없습니다."
                            );

            return new AnnualDecadeFunctionalAnalysis(
                    decadeFunctionalSynthesisAnalysis,
                    annualFunctionalCapacityAnalysis,
                    List.of(),
                    false,
                    reason,
                    completeChart
            );
        }

        List<AnnualDecadeFunctionalSegment> segments =
                new ArrayList<>();

        /*
         * =================================================
         * 14-5 Interaction 하나 = Functional Segment 하나
         * =================================================
         */

        for (
                AnnualDecadeLuckInteraction interaction
                : interactionAnalysis.interactions()
        ) {

            int sequenceNumber =
                    interaction
                            .decadeSequenceNumber();

            /*
             * =================================================
             * 해당 대운 Functional 기준선
             * =================================================
             */

            DecadePeriodFunctionalSynthesisAnalysis decadeBaseline =
                    decadeFunctionalSynthesisAnalysis
                            .requirePeriodAnalysis(
                                    sequenceNumber
                            );

            if (
                    decadeBaseline.period()
                            != interaction.decadePeriod()
            ) {

                throw new IllegalStateException(
                        "Decade Functional Synthesis와 14-5 Interaction이 "
                                + "서로 다른 DecadeLuckPeriod를 참조합니다. sequence="
                                + sequenceNumber
                );
            }

            /*
             * =================================================
             * 기존 14-6 동일 Segment Context
             * =================================================
             */

            AnnualLuckDecadeContextAnalysis annualDecadeContext =
                    annualLuckAnalysis
                            .contextOfDecade(
                                    sequenceNumber
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "14-6에서 대응하는 Annual-Decade Context를 찾을 수 없습니다: "
                                                            + sequenceNumber
                                            )
                            );

            if (
                    annualDecadeContext.interaction()
                            != interaction
            ) {

                throw new IllegalStateException(
                        "14-6 Annual-Decade Context가 14-5 Interaction Object Identity를 보존하지 않았습니다. sequence="
                                + sequenceNumber
                );
            }

            /*
             * =================================================
             * 5 Function
             * =================================================
             */

            List<AnnualDecadeFunctionSegmentEffect>
                    functionEffects =
                    new ArrayList<>();

            for (
                    NatalTenGodFunctionType functionType
                    : NatalTenGodFunctionType.values()
            ) {

                DecadeResolvedFunctionEffect decadeEffect =
                        decadeBaseline
                                .requireFunctionEffect(
                                        functionType
                                );

                AnnualFunctionCapacityContext annualContext =
                        annualFunctionalCapacityAnalysis
                                .requireContext(
                                        functionType
                                );

                AnnualDecadeFunctionContactMode contactMode =
                        AnnualDecadeFunctionContactMode.of(
                                decadeEffect.active(),
                                annualContext.active()
                        );

                functionEffects.add(
                        new AnnualDecadeFunctionSegmentEffect(
                                interaction,
                                decadeEffect,
                                annualContext,
                                contactMode,
                                completeChart
                        )
                );
            }

            segments.add(
                    new AnnualDecadeFunctionalSegment(
                            interaction,
                            annualDecadeContext,
                            decadeBaseline,
                            annualFunctionalCapacityAnalysis,
                            List.copyOf(
                                    functionEffects
                            ),
                            completeChart
                    )
            );
        }

        /*
         * =================================================
         * 기존 14-5와 동일한 Segment 순서
         * =================================================
         */

        segments.sort(
                Comparator
                        .comparing(
                                (
                                        AnnualDecadeFunctionalSegment segment
                                ) ->
                                        segment
                                                .overlap()
                                                .possibleStartInclusive()
                        )
                        .thenComparingInt(
                                AnnualDecadeFunctionalSegment
                                        ::decadeSequenceNumber
                        )
        );

        return new AnnualDecadeFunctionalAnalysis(
                decadeFunctionalSynthesisAnalysis,
                annualFunctionalCapacityAnalysis,
                List.copyOf(
                        segments
                ),
                true,
                "",
                completeChart
        );
    }
}