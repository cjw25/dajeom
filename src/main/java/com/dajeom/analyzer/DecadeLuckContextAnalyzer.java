package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckContextAnalysis;
import com.dajeom.model.DecadeLuckContextResolution;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckPeriodTenGodAnalysis;
import com.dajeom.model.DecadeLuckNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckTenGodAnalysis;
import com.dajeom.resolver.DecadeLuckContextResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-6.
 *
 * 대운 교차 Context Analyzer.
 *
 * =================================================
 *
 * 13-4 대운 십성
 * +
 * 13-5 원국 관계
 * +
 * 5/7/10/12차 Context Resolver
 *
 * ↓
 *
 * DecadeLuckContextAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 용신을 다시 계산하지 않는다.
 * - 격국을 다시 판정하지 않는다.
 * - 원국 십성 세력을 변경하지 않는다.
 * - 궁위를 가족관계로 확정하지 않는다.
 * - 합충을 길흉 점수로 바꾸지 않는다.
 *
 * 기존 분석 결과의 교차 근거만 묶는다.
 */
@Component
public class DecadeLuckContextAnalyzer {

    /**
     * =================================================
     * 공식 13-6 API
     * =================================================
     */
    public DecadeLuckContextAnalysis analyze(

            DecadeLuckTenGodAnalysis tenGodAnalysis,

            DecadeLuckNatalRelationAnalysis natalRelationAnalysis,

            DecadeLuckContextResolver contextResolver

    ) {

        Objects.requireNonNull(
                tenGodAnalysis,
                "13-4 DecadeLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "13-5 DecadeLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "DecadeLuckContextResolver가 필요합니다."
        );

        validateInputs(
                tenGodAnalysis,
                natalRelationAnalysis
        );

        /*
         * =================================================
         * 선행 분석 불가
         * =================================================
         */

        if (
                !tenGodAnalysis.analysisAvailable()
                        || !natalRelationAnalysis.analysisAvailable()
        ) {

            String reason =
                    buildUnavailableReason(
                            tenGodAnalysis,
                            natalRelationAnalysis
                    );

            return new DecadeLuckContextAnalysis(
                    tenGodAnalysis.dayMaster(),
                    tenGodAnalysis,
                    natalRelationAnalysis,
                    List.of(),
                    false,
                    reason
            );
        }

        List<DecadeLuckPeriodContextAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadeLuckPeriod period
                : tenGodAnalysis
                .sequenceAnalysis()
                .periods()
        ) {

            int sequenceNumber =
                    period.sequenceNumber();

            DecadeLuckPeriodTenGodAnalysis
                    periodTenGodAnalysis =
                    tenGodAnalysis
                            .periodAnalysis(
                                    sequenceNumber
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "대운 십성 분석을 찾을 수 없습니다: "
                                                            + sequenceNumber
                                            )
                            );

            DecadeLuckPeriodNatalRelationAnalysis
                    periodRelationAnalysis =
                    natalRelationAnalysis
                            .periodAnalysis(
                                    sequenceNumber
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "대운 원국 관계 분석을 찾을 수 없습니다: "
                                                            + sequenceNumber
                                            )
                            );

            /*
             * 같은 Period 객체인지 확인.
             */

            if (
                    periodTenGodAnalysis.period()
                            != period

                            || periodRelationAnalysis.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "13-4 / 13-5 분석이 현재 13-3 Period를 참조하지 않습니다: "
                                + sequenceNumber
                );
            }

            DecadeLuckContextResolution resolution =
                    Objects.requireNonNull(
                            contextResolver.resolve(
                                    periodTenGodAnalysis,
                                    periodRelationAnalysis
                            ),
                            "DecadeLuckContextResolver가 null을 반환했습니다."
                    );

            if (
                    resolution.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "13-6 Resolver가 다른 대운 Period의 결과를 반환했습니다: "
                                + sequenceNumber
                );
            }

            periodAnalyses.add(
                    new DecadeLuckPeriodContextAnalysis(
                            tenGodAnalysis.dayMaster(),
                            period,
                            periodTenGodAnalysis,
                            periodRelationAnalysis,
                            resolution.signals()
                    )
            );
        }

        periodAnalyses.sort(
                Comparator.comparingInt(
                        DecadeLuckPeriodContextAnalysis
                                ::sequenceNumber
                )
        );

        return new DecadeLuckContextAnalysis(
                tenGodAnalysis.dayMaster(),
                tenGodAnalysis,
                natalRelationAnalysis,
                periodAnalyses,
                true,
                ""
        );
    }

    /*
     * =================================================
     * 입력 정합성
     * =================================================
     */

    private void validateInputs(

            DecadeLuckTenGodAnalysis tenGodAnalysis,

            DecadeLuckNatalRelationAnalysis natalRelationAnalysis

    ) {

        if (
                tenGodAnalysis.dayMaster()
                        != natalRelationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodAnalysis.sequenceAnalysis()
                        != natalRelationAnalysis.sequenceAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5가 서로 다른 13-3 대운 Sequence를 참조합니다."
            );
        }

        if (
                tenGodAnalysis.analysisAvailable()
                        != natalRelationAnalysis.analysisAvailable()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5의 분석 가능 상태가 서로 다릅니다."
            );
        }
    }

    /*
     * =================================================
     * 불가 사유
     * =================================================
     */

    private String buildUnavailableReason(

            DecadeLuckTenGodAnalysis tenGodAnalysis,

            DecadeLuckNatalRelationAnalysis natalRelationAnalysis

    ) {

        List<String> reasons =
                new ArrayList<>();

        if (
                !tenGodAnalysis.analysisAvailable()
        ) {

            reasons.add(
                    tenGodAnalysis
                            .unavailableReason()
            );
        }

        if (
                !natalRelationAnalysis.analysisAvailable()
        ) {

            reasons.add(
                    natalRelationAnalysis
                            .unavailableReason()
            );
        }

        return reasons
                .stream()
                .filter(
                        Objects::nonNull
                )
                .map(
                        String::trim
                )
                .filter(
                        value ->
                                !value.isBlank()
                )
                .distinct()
                .reduce(
                        (left, right) ->
                                left + " / " + right
                )
                .orElse(
                        "선행 대운 분석이 확정되지 않아 교차 Context를 생성할 수 없습니다."
                );
    }
}