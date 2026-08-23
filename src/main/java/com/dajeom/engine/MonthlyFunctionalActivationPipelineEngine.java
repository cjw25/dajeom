package com.dajeom.engine;

import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Monthly Functional Activation 공식 Pipeline.
 *
 * =================================================
 *
 * 입력:
 *
 * LuckIntegratedAnalysis
 *
 * +
 *
 * 동일 LuckIntegratedAnalysis에서 생성된
 * AnnualFunctionalBaselineAnalysis Map
 *
 * =================================================
 *
 * 처리:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * ->
 *
 * MonthlyFunctionalActivationAnalysis
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 1.
 * MonthlyLuckAnalysis를 새로 계산하지 않는다.
 *
 * 16차 LuckIntegratedAnalysis가 실제 보유한
 * MonthlyLuckAnalysis 객체를 그대로 사용한다.
 *
 * 2.
 * Annual Functional Baseline을 새로 계산하지 않는다.
 *
 * 상위 공식 AnnualFunctionalBaselinePipelineEngine이
 * 생성한 Baseline만 입력받는다.
 *
 * 3.
 * 연도 숫자 / monthIndex 숫자로
 * Source를 다시 찾지 않는다.
 *
 * AnnualLuckAnalysis
 * MonthlyLuckAnalysis
 *
 * 객체 Identity를 기준으로 연결한다.
 *
 * 4.
 * 동일 MonthlyLuckAnalysis가 여러 Timeline Segment에서
 * 재사용되더라도 Monthly Functional Activation은
 * 한 번만 계산한다.
 *
 * 5.
 * 신강·신약을 재계산하지 않는다.
 *
 * 6.
 * 용신을 재선정하지 않는다.
 *
 * 7.
 * Annual Functional Baseline을 변경하지 않는다.
 *
 * 8.
 * 월운의 Function 접촉을
 * Annual Function 활성으로 승격하지 않는다.
 *
 * 9.
 * 길흉 / 사건 / LifeDomain 결론을 만들지 않는다.
 *
 * 10.
 * 이후 LifeDomain에서는
 * MONTHLY FUNCTIONAL CONTEXT라는
 * derived temporal source로만 사용한다.
 */
@Component
public class MonthlyFunctionalActivationPipelineEngine {

    private final MonthlyFunctionalActivationAnalysisEngine
            monthlyFunctionalActivationAnalysisEngine;


    public MonthlyFunctionalActivationPipelineEngine(

            MonthlyFunctionalActivationAnalysisEngine
                    monthlyFunctionalActivationAnalysisEngine

    ) {

        this.monthlyFunctionalActivationAnalysisEngine =
                Objects.requireNonNull(
                        monthlyFunctionalActivationAnalysisEngine,
                        "MonthlyFunctionalActivationAnalysisEngine이 필요합니다."
                );
    }


    public Map<
            MonthlyLuckAnalysis,
            MonthlyFunctionalActivationAnalysis
            > analyze(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            Map<
                    AnnualLuckAnalysis,
                    AnnualFunctionalBaselineAnalysis
                    > annualFunctionalBaselines

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "Monthly Functional Pipeline에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionalBaselines,
                "Monthly Functional Pipeline에는 Annual Functional Baseline Map이 필요합니다."
        );


        /*
         * =================================================
         * Annual Baseline Coverage 선검증
         * =================================================
         *
         * 현재 16차가 실제 보유한 모든 AnnualLuckAnalysis에
         * 공식 Baseline이 있어야 한다.
         *
         * targetYear 숫자로 비교하지 않는다.
         */

        for (
                AnnualLuckAnalysis annualLuckAnalysis
                : luckIntegratedAnalysis.annualLuckAnalyses()
        ) {

            AnnualFunctionalBaselineAnalysis baseline =
                    annualFunctionalBaselines
                            .get(
                                    annualLuckAnalysis
                            );


            if (
                    baseline == null
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Pipeline에 필요한 Annual Functional Baseline이 누락되었습니다. "
                                + "targetYear="
                                + annualLuckAnalysis.targetYear()
                );
            }


            if (
                    baseline
                            .annualLuckAnalysis()
                            != annualLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Annual Functional Baseline이 현재 LuckIntegratedAnalysis의 "
                                + "AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                + "targetYear="
                                + annualLuckAnalysis.targetYear()
                );
            }


            validateAnnualAnchor(
                    luckIntegratedAnalysis,
                    annualLuckAnalysis
            );
        }


        /*
         * =================================================
         * Monthly Identity Cache
         * =================================================
         *
         * 같은 MonthlyLuckAnalysis 객체가
         * 여러 Timeline Segment에서 재사용되어도
         * 계산은 한 번만 수행한다.
         */

        IdentityHashMap<
                MonthlyLuckAnalysis,
                MonthlyFunctionalActivationAnalysis
                > analyses =
                new IdentityHashMap<>();


        for (
                MonthlyLuckAnalysis monthlyLuckAnalysis
                : luckIntegratedAnalysis.monthlyLuckAnalyses()
        ) {

            Objects.requireNonNull(
                    monthlyLuckAnalysis,
                    "16차 Timeline의 MonthlyLuckAnalysis는 null일 수 없습니다."
            );


            if (
                    analyses.containsKey(
                            monthlyLuckAnalysis
                    )
            ) {

                continue;
            }


            validateMonthlyAnchor(
                    luckIntegratedAnalysis,
                    monthlyLuckAnalysis
            );


            AnnualLuckAnalysis annualLuckAnalysis =
                    monthlyLuckAnalysis
                            .annualLuckAnalysis();


            AnnualFunctionalBaselineAnalysis annualBaseline =
                    annualFunctionalBaselines
                            .get(
                                    annualLuckAnalysis
                            );


            if (
                    annualBaseline == null
            ) {

                throw new IllegalStateException(
                        "MonthlyLuckAnalysis의 AnnualLuckAnalysis에 대응하는 "
                                + "Annual Functional Baseline이 없습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }


            if (
                    annualBaseline
                            .annualLuckAnalysis()
                            != annualLuckAnalysis
            ) {

                throw new IllegalStateException(
                        "Monthly Functional Pipeline이 조회한 Annual Baseline이 "
                                + "MonthlyLuckAnalysis의 AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }


            MonthlyFunctionalActivationAnalysis activationAnalysis =
                    Objects.requireNonNull(
                            monthlyFunctionalActivationAnalysisEngine
                                    .analyze(
                                            annualBaseline,
                                            monthlyLuckAnalysis
                                    ),
                            "MonthlyFunctionalActivationAnalysisEngine이 null을 반환했습니다."
                    );


            /*
             * =================================================
             * 결과 Identity
             * =================================================
             */

            if (
                    activationAnalysis
                            .annualFunctionalBaseline()
                            != annualBaseline
            ) {

                throw new IllegalStateException(
                        "Monthly Functional Activation이 입력 Annual Functional Baseline Identity를 보존하지 않았습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }


            if (
                    activationAnalysis
                            .monthlyLuckAnalysis()
                            != monthlyLuckAnalysis
            ) {

                throw new IllegalStateException(
                        "Monthly Functional Activation이 입력 MonthlyLuckAnalysis Identity를 보존하지 않았습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }


            if (
                    activationAnalysis
                            .completeChart()
                            != luckIntegratedAnalysis
                            .completeChart()
            ) {

                throw new IllegalStateException(
                        "Monthly Functional Activation과 LuckIntegratedAnalysis의 completeChart가 일치하지 않습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }


            analyses.put(
                    monthlyLuckAnalysis,
                    activationAnalysis
            );
        }


        /*
         * =================================================
         * Monthly Coverage 후검증
         * =================================================
         */

        for (
                MonthlyLuckAnalysis monthlyLuckAnalysis
                : luckIntegratedAnalysis.monthlyLuckAnalyses()
        ) {

            if (
                    !analyses.containsKey(
                            monthlyLuckAnalysis
                    )
            ) {

                throw new IllegalStateException(
                        "16차 Timeline의 Monthly Functional Activation이 누락되었습니다. "
                                + "sajuYear="
                                + monthlyLuckAnalysis.sajuYear()
                                + ", monthIndex="
                                + monthlyLuckAnalysis.monthIndex()
                );
            }
        }


        return Collections.unmodifiableMap(
                analyses
        );
    }


    /*
     * =================================================
     * Annual Anchor
     * =================================================
     */

    private void validateAnnualAnchor(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "Annual Anchor 검증에는 AnnualLuckAnalysis가 필요합니다."
        );


        if (
                annualLuckAnalysis
                        .natalAnalysis()
                        != luckIntegratedAnalysis
                        .natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Pipeline의 AnnualLuckAnalysis가 "
                            + "현재 LuckIntegratedAnalysis와 다른 PalaceAnalysis를 참조합니다. "
                            + "targetYear="
                            + annualLuckAnalysis.targetYear()
            );
        }


        if (
                annualLuckAnalysis
                        .decadeLuckAnalysis()
                        != luckIntegratedAnalysis
                        .decadeLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Pipeline의 AnnualLuckAnalysis가 "
                            + "현재 LuckIntegratedAnalysis와 다른 DecadeLuckAnalysis를 참조합니다. "
                            + "targetYear="
                            + annualLuckAnalysis.targetYear()
            );
        }
    }


    /*
     * =================================================
     * Monthly Anchor
     * =================================================
     */

    private void validateMonthlyAnchor(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            MonthlyLuckAnalysis monthlyLuckAnalysis

    ) {

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "Monthly Anchor 검증에는 MonthlyLuckAnalysis가 필요합니다."
        );


        AnnualLuckAnalysis annualLuckAnalysis =
                monthlyLuckAnalysis
                        .annualLuckAnalysis();


        validateAnnualAnchor(
                luckIntegratedAnalysis,
                annualLuckAnalysis
        );


        /*
         * =================================================
         * Monthly Natal Anchor
         * =================================================
         *
         * MonthlyLuckAnalysis 자체에서는
         * natalRelationAnalysis를 통해
         * AnnualLuckAnalysis의 natalAnalysis Identity를
         * 이미 검증한다.
         *
         * 여기서는 Pipeline 수준에서 다시
         * 공식 LuckIntegratedAnalysis Anchor와 연결한다.
         */

        if (
                monthlyLuckAnalysis
                        .natalRelationAnalysis()
                        .natalAnalysis()
                        != luckIntegratedAnalysis
                        .natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckAnalysis가 현재 LuckIntegratedAnalysis와 "
                            + "다른 PalaceAnalysis를 참조합니다. "
                            + "sajuYear="
                            + monthlyLuckAnalysis.sajuYear()
                            + ", monthIndex="
                            + monthlyLuckAnalysis.monthIndex()
            );
        }


        /*
         * =================================================
         * Monthly Decade Anchor
         * =================================================
         */

        if (
                monthlyLuckAnalysis
                        .decadeInteractionAnalysis()
                        .decadeLuckAnalysis()
                        != luckIntegratedAnalysis
                        .decadeLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckAnalysis가 현재 LuckIntegratedAnalysis와 "
                            + "다른 DecadeLuckAnalysis를 참조합니다. "
                            + "sajuYear="
                            + monthlyLuckAnalysis.sajuYear()
                            + ", monthIndex="
                            + monthlyLuckAnalysis.monthIndex()
            );
        }


        /*
         * =================================================
         * Annual Period Identity
         * =================================================
         */

        if (
                monthlyLuckAnalysis
                        .period()
                        .annualPeriod()
                        != annualLuckAnalysis
                        .period()
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckAnalysis의 MonthlyLuckPeriod가 "
                            + "현재 AnnualLuckAnalysis의 AnnualLuckPeriod Identity를 보존하지 않았습니다. "
                            + "sajuYear="
                            + monthlyLuckAnalysis.sajuYear()
                            + ", monthIndex="
                            + monthlyLuckAnalysis.monthIndex()
            );
        }
    }
}