package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckNatalCrossResolution;
import com.dajeom.model.DecadeLuckNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckPeriodNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckSequenceAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.DecadeLuckNatalCrossResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-5.
 *
 * 원국 × 대운 관계 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 12차 PalaceAnalysis
 * +
 * 13-3 DecadeLuckSequenceAnalysis
 * +
 * 8/9차 연결 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * DecadeLuckNatalRelationAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 합충형파해 공식을 다시 계산하지 않는다.
 * - 합화 조건을 다시 판단하지 않는다.
 * - 합 = 길
 * - 충 = 흉
 *
 * 같은 평가를 하지 않는다.
 *
 * 관계 Evidence만 구조화한다.
 */
@Component
public class DecadeLuckNatalRelationAnalyzer {

    /**
     * =================================================
     * 공식 13-5 API
     * =================================================
     */
    public DecadeLuckNatalRelationAnalysis analyze(

            PalaceAnalysis natalAnalysis,

            DecadeLuckSequenceAnalysis sequenceAnalysis,

            DecadeLuckNatalCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "13-5 분석에는 원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                sequenceAnalysis,
                "13-3 DecadeLuckSequenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "DecadeLuckNatalCrossResolver가 필요합니다."
        );

        validateReferenceMonth(
                natalAnalysis,
                sequenceAnalysis
        );

        /*
         * =================================================
         * Sequence 자체가 확정되지 않은 경우
         * =================================================
         */

        if (
                !sequenceAnalysis
                        .sequenceAvailable()
        ) {

            return new DecadeLuckNatalRelationAnalysis(
                    natalAnalysis.dayMaster(),
                    natalAnalysis,
                    sequenceAnalysis,
                    List.of(),
                    false,
                    "대운 간지 Sequence가 확정되지 않아 "
                            + "원국과의 관계도 하나로 확정할 수 없습니다. "
                            + sequenceAnalysis.unavailableReason()
            );
        }

        List<DecadeLuckPeriodNatalRelationAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadeLuckPeriod period
                : sequenceAnalysis.periods()
        ) {

            DecadeLuckNatalCrossResolution resolution =
                    Objects.requireNonNull(
                            crossResolver.resolve(
                                    natalAnalysis,
                                    period
                            ),
                            "DecadeLuckNatalCrossResolver가 null을 반환했습니다."
                    );

            /*
             * =================================================
             * Resolver가 다른 대운을 반환하는 오류 방지
             * =================================================
             */

            if (
                    resolution.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "Resolver 결과가 현재 대운 Period를 참조하지 않습니다: "
                                + period.sequenceNumber()
                );
            }

            periodAnalyses.add(
                    new DecadeLuckPeriodNatalRelationAnalysis(
                            natalAnalysis.dayMaster(),
                            natalAnalysis,
                            period,
                            resolution.relations(),
                            resolution.transformations()
                    )
            );
        }

        periodAnalyses.sort(
                Comparator.comparingInt(
                        DecadeLuckPeriodNatalRelationAnalysis
                                ::sequenceNumber
                )
        );

        return new DecadeLuckNatalRelationAnalysis(
                natalAnalysis.dayMaster(),
                natalAnalysis,
                sequenceAnalysis,
                periodAnalyses,
                true,
                ""
        );
    }

    /*
     * =================================================
     * 12차 원국 ↔ 13-3 월주 검증
     * =================================================
     */

    private void validateReferenceMonth(

            PalaceAnalysis natalAnalysis,

            DecadeLuckSequenceAnalysis sequenceAnalysis

    ) {

        if (
                !sequenceAnalysis
                        .referenceMonthPillar()
                        .samePillar(
                                natalAnalysis
                                        .monthStemEvidence()
                                        .stem(),
                                natalAnalysis
                                        .monthBranchEvidence()
                                        .branch()
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 Sequence의 기준 월주와 PalaceAnalysis의 원국 월주가 다릅니다."
            );
        }
    }
}