package com.dajeom.engine;

import com.dajeom.analyzer.AnnualDecadeLuckInteractionAnalyzer;
import com.dajeom.analyzer.AnnualLuckContextAnalyzer;
import com.dajeom.analyzer.AnnualLuckNatalRelationAnalyzer;
import com.dajeom.analyzer.AnnualLuckTenGodAnalyzer;
import com.dajeom.calculator.AnnualLuckPeriodCalculator;
import com.dajeom.model.AnnualDecadeLuckInteractionAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.AnnualDecadeLuckCrossResolver;
import com.dajeom.resolver.AnnualLuckContextResolver;
import com.dajeom.resolver.AnnualLuckNatalCrossResolver;
import com.dajeom.resolver.AnnualLuckTenGodSourceResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 14차-7.
 *
 * 세운 분석 최종 Engine.
 *
 * =================================================
 *
 * 처리 순서:
 *
 * 14-1 / 14-2
 * AnnualLuckPeriodCalculator
 *
 * ↓
 *
 * 14-3
 * AnnualLuckTenGodAnalyzer
 *
 * ↓
 *
 * 14-4
 * AnnualLuckNatalRelationAnalyzer
 *
 * ↓
 *
 * 14-5
 * AnnualDecadeLuckInteractionAnalyzer
 *
 * ↓
 *
 * 14-6
 * AnnualLuckContextAnalyzer
 *
 * ↓
 *
 * 14-7
 * AnnualLuckAnalysis
 *
 * =================================================
 *
 * 이 Engine은 새로운 명리 규칙을
 * 계산하지 않는다.
 *
 * 각 단계의 deterministic 결과를
 * 연결하는 orchestration 역할만 한다.
 *
 * =================================================
 *
 * 정밀화 16차 연결:
 *
 * 16차에서 이미 선택된 AnnualLuckPeriod가 있다면
 * 그 Period를 다시 계산하지 않고
 * 동일 객체 그대로 분석할 수 있어야 한다.
 *
 * 따라서 AnnualLuckPeriod를 직접 받는
 * analyze(...) overload를 제공한다.
 */
@Component
public class AnnualLuckAnalysisEngine {

    private final AnnualLuckPeriodCalculator
            annualLuckPeriodCalculator;

    private final AnnualLuckTenGodAnalyzer
            annualLuckTenGodAnalyzer;

    private final AnnualLuckNatalRelationAnalyzer
            annualLuckNatalRelationAnalyzer;

    private final AnnualDecadeLuckInteractionAnalyzer
            annualDecadeLuckInteractionAnalyzer;

    private final AnnualLuckContextAnalyzer
            annualLuckContextAnalyzer;

    public AnnualLuckAnalysisEngine(

            AnnualLuckPeriodCalculator
                    annualLuckPeriodCalculator,

            AnnualLuckTenGodAnalyzer
                    annualLuckTenGodAnalyzer,

            AnnualLuckNatalRelationAnalyzer
                    annualLuckNatalRelationAnalyzer,

            AnnualDecadeLuckInteractionAnalyzer
                    annualDecadeLuckInteractionAnalyzer,

            AnnualLuckContextAnalyzer
                    annualLuckContextAnalyzer

    ) {

        this.annualLuckPeriodCalculator =
                Objects.requireNonNull(
                        annualLuckPeriodCalculator,
                        "AnnualLuckPeriodCalculator가 필요합니다."
                );

        this.annualLuckTenGodAnalyzer =
                Objects.requireNonNull(
                        annualLuckTenGodAnalyzer,
                        "AnnualLuckTenGodAnalyzer가 필요합니다."
                );

        this.annualLuckNatalRelationAnalyzer =
                Objects.requireNonNull(
                        annualLuckNatalRelationAnalyzer,
                        "AnnualLuckNatalRelationAnalyzer가 필요합니다."
                );

        this.annualDecadeLuckInteractionAnalyzer =
                Objects.requireNonNull(
                        annualDecadeLuckInteractionAnalyzer,
                        "AnnualDecadeLuckInteractionAnalyzer가 필요합니다."
                );

        this.annualLuckContextAnalyzer =
                Objects.requireNonNull(
                        annualLuckContextAnalyzer,
                        "AnnualLuckContextAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 특정 명리 연도 분석
     * =================================================
     *
     * targetYear를 기준으로
     * AnnualLuckPeriod를 새로 계산한다.
     *
     * 일반적인 14차 직접 호출용 API다.
     */
    public AnnualLuckAnalysis analyze(

            int targetYear,

            ZoneId zoneId,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        validateCommonInputs(
                zoneId,
                natalAnalysis,
                decadeLuckAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                contextResolver
        );

        AnnualLuckPeriod annualPeriod =
                annualLuckPeriodCalculator
                        .calculate(
                                targetYear,
                                zoneId
                        );

        return buildAnalysis(
                annualPeriod,
                natalAnalysis,
                decadeLuckAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                contextResolver
        );
    }

    /**
     * =================================================
     * 정밀화 16차 연결용
     * 정확한 AnnualLuckPeriod 분석
     * =================================================
     *
     * 16-3 AnnualLuckCandidate가 이미 선택한
     * AnnualLuckPeriod 객체를 그대로 사용한다.
     *
     * 여기서:
     *
     * - targetYear로 Period를 재계산하지 않는다.
     * - referenceDateTime으로 Period를 재계산하지 않는다.
     * - 값이 같은 새 AnnualLuckPeriod를 만들지 않는다.
     *
     * 이를 통해:
     *
     * annualLuckAnalysis.period()
     * ==
     * annualCandidate.period()
     *
     * Object Identity를 보존한다.
     */
    public AnnualLuckAnalysis analyze(

            AnnualLuckPeriod annualPeriod,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "16차 연결 세운 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        validateCommonInputs(
                annualPeriod.zoneId(),
                natalAnalysis,
                decadeLuckAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                contextResolver
        );

        AnnualLuckAnalysis analysis =
                buildAnalysis(
                        annualPeriod,
                        natalAnalysis,
                        decadeLuckAnalysis,
                        tenGodSourceResolver,
                        natalCrossResolver,
                        decadeCrossResolver,
                        contextResolver
                );

        /*
         * =================================================
         * Period Identity 최종 방어
         * =================================================
         */

        if (
                analysis.period()
                        != annualPeriod
        ) {

            throw new IllegalStateException(
                    "AnnualLuckAnalysisEngine이 전달받은 AnnualLuckPeriod 객체를 그대로 보존하지 않았습니다."
            );
        }

        return analysis;
    }

    /**
     * =================================================
     * 특정 실제 시각 기준 세운 분석
     * =================================================
     */
    public AnnualLuckAnalysis analyzeAt(

            ZonedDateTime referenceDateTime,

            ZoneId zoneId,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 분석 기준시각이 필요합니다."
        );

        validateCommonInputs(
                zoneId,
                natalAnalysis,
                decadeLuckAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                contextResolver
        );

        AnnualLuckPeriod annualPeriod =
                annualLuckPeriodCalculator
                        .calculateAt(
                                referenceDateTime,
                                zoneId
                        );

        AnnualLuckAnalysis analysis =
                buildAnalysis(
                        annualPeriod,
                        natalAnalysis,
                        decadeLuckAnalysis,
                        tenGodSourceResolver,
                        natalCrossResolver,
                        decadeCrossResolver,
                        contextResolver
                );

        if (
                !analysis.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalStateException(
                    "기준시각과 최종 세운 분석 기간이 일치하지 않습니다."
            );
        }

        return analysis;
    }

    /**
     * LocalDateTime 버전.
     */
    public AnnualLuckAnalysis analyzeAt(

            LocalDateTime referenceDateTime,

            ZoneId zoneId,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 분석 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 분석에는 ZoneId가 필요합니다."
        );

        return analyzeAt(
                referenceDateTime
                        .atZone(
                                zoneId
                        ),
                zoneId,
                natalAnalysis,
                decadeLuckAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                contextResolver
        );
    }

    /**
     * =================================================
     * 14-3 ~ 14-7 공통 조립
     * =================================================
     */
    private AnnualLuckAnalysis buildAnalysis(

            AnnualLuckPeriod annualPeriod,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "14차 공통 조립에는 AnnualLuckPeriod가 필요합니다."
        );

        /*
         * =================================================
         * 14-3
         * 세운 천간·지지 십성
         * =================================================
         */

        AnnualLuckTenGodAnalysis tenGodAnalysis =
                annualLuckTenGodAnalyzer
                        .analyze(
                                natalAnalysis.dayMaster(),
                                annualPeriod,
                                tenGodSourceResolver
                        );

        /*
         * =================================================
         * 14-4
         * 원국 × 세운
         * =================================================
         */

        AnnualLuckNatalRelationAnalysis natalRelationAnalysis =
                annualLuckNatalRelationAnalyzer
                        .analyze(
                                natalAnalysis,
                                annualPeriod,
                                natalCrossResolver
                        );

        /*
         * =================================================
         * 14-5
         * 대운 × 세운
         * =================================================
         */

        AnnualDecadeLuckInteractionAnalysis
                decadeInteractionAnalysis =
                annualDecadeLuckInteractionAnalyzer
                        .analyze(
                                annualPeriod,
                                decadeLuckAnalysis,
                                decadeCrossResolver
                        );

        /*
         * =================================================
         * 14-6
         * Context
         * =================================================
         */

        AnnualLuckContextAnalysis contextAnalysis =
                annualLuckContextAnalyzer
                        .analyze(
                                tenGodAnalysis,
                                natalRelationAnalysis,
                                decadeInteractionAnalysis,
                                contextResolver
                        );

        /*
         * =================================================
         * 14-7
         * 최종 Aggregate
         * =================================================
         */

        AnnualLuckAnalysis analysis =
                new AnnualLuckAnalysis(
                        natalAnalysis.dayMaster(),
                        natalAnalysis,
                        decadeLuckAnalysis,
                        annualPeriod,
                        tenGodAnalysis,
                        natalRelationAnalysis,
                        decadeInteractionAnalysis,
                        contextAnalysis
                );

        /*
         * 내부 단계가 동일 Period를 유지했는지
         * Aggregate 생성 뒤 다시 확인한다.
         */
        if (
                analysis.period()
                        != annualPeriod
        ) {

            throw new IllegalStateException(
                    "14차 Aggregate가 입력 AnnualLuckPeriod Identity를 보존하지 않았습니다."
            );
        }

        return analysis;
    }

    /*
     * =================================================
     * 공통 입력 검증
     * =================================================
     */

    private void validateCommonInputs(

            ZoneId zoneId,

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckTenGodSourceResolver
                    tenGodSourceResolver,

            AnnualLuckNatalCrossResolver
                    natalCrossResolver,

            AnnualDecadeLuckCrossResolver
                    decadeCrossResolver,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                zoneId,
                "세운 분석에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "세운 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "세운 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodSourceResolver,
                "AnnualLuckTenGodSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                natalCrossResolver,
                "AnnualLuckNatalCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                decadeCrossResolver,
                "AnnualDecadeLuckCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "AnnualLuckContextResolver가 필요합니다."
        );

        /*
         * 같은 원국.
         */
        if (
                decadeLuckAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 14차 세운 분석이 서로 다른 PalaceAnalysis를 참조합니다."
            );
        }

        if (
                decadeLuckAnalysis.dayMaster()
                        != natalAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 원국 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }
    }
}