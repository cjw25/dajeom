package com.dajeom.engine;

import com.dajeom.analyzer.DecadeLuckContextAnalyzer;
import com.dajeom.analyzer.DecadeLuckNatalRelationAnalyzer;
import com.dajeom.analyzer.DecadeLuckTenGodAnalyzer;
import com.dajeom.calculator.DecadeLuckSequenceCalculator;
import com.dajeom.calculator.LuckDirectionCalculator;
import com.dajeom.calculator.LuckStartCalculator;
import com.dajeom.entity.Gender;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckContextAnalysis;
import com.dajeom.model.DecadeLuckDirectionAnalysis;
import com.dajeom.model.DecadeLuckNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckSequenceAnalysis;
import com.dajeom.model.DecadeLuckStartAnalysis;
import com.dajeom.model.DecadeLuckTenGodAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.DecadeLuckContextResolver;
import com.dajeom.resolver.DecadeLuckNatalCrossResolver;
import com.dajeom.resolver.DecadeLuckTenGodSourceResolver;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * 정밀화 13차-7.
 *
 * 대운 분석 최종 Engine.
 *
 * =================================================
 *
 * 실행 흐름:
 *
 * 13-1
 * LuckDirectionCalculator
 *
 * ↓
 *
 * 13-2
 * LuckStartCalculator
 *
 * ↓
 *
 * 13-3
 * DecadeLuckSequenceCalculator
 *
 * ↓
 *
 * 13-4
 * DecadeLuckTenGodAnalyzer
 *
 * ↓
 *
 * 13-5
 * DecadeLuckNatalRelationAnalyzer
 *
 * ↓
 *
 * 13-6
 * DecadeLuckContextAnalyzer
 *
 * ↓
 *
 * 13-7
 * DecadeLuckAnalysis
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Engine은:
 *
 * - 원국을 재계산하지 않는다.
 * - 용신을 재계산하지 않는다.
 * - 격국을 재판정하지 않는다.
 * - 관계 공식을 다시 구현하지 않는다.
 *
 * 기존 단계 결과와 Resolver를 조립한다.
 */
@Component
public class DecadeLuckAnalysisEngine {

    private static final int
            DEFAULT_PERIOD_COUNT = 10;

    private final LuckDirectionCalculator
            luckDirectionCalculator;

    private final LuckStartCalculator
            luckStartCalculator;

    private final DecadeLuckSequenceCalculator
            decadeLuckSequenceCalculator;

    private final DecadeLuckTenGodAnalyzer
            decadeLuckTenGodAnalyzer;

    private final DecadeLuckNatalRelationAnalyzer
            decadeLuckNatalRelationAnalyzer;

    private final DecadeLuckContextAnalyzer
            decadeLuckContextAnalyzer;

    public DecadeLuckAnalysisEngine(

            LuckDirectionCalculator
                    luckDirectionCalculator,

            LuckStartCalculator
                    luckStartCalculator,

            DecadeLuckSequenceCalculator
                    decadeLuckSequenceCalculator,

            DecadeLuckTenGodAnalyzer
                    decadeLuckTenGodAnalyzer,

            DecadeLuckNatalRelationAnalyzer
                    decadeLuckNatalRelationAnalyzer,

            DecadeLuckContextAnalyzer
                    decadeLuckContextAnalyzer

    ) {

        this.luckDirectionCalculator =
                Objects.requireNonNull(
                        luckDirectionCalculator,
                        "LuckDirectionCalculator가 필요합니다."
                );

        this.luckStartCalculator =
                Objects.requireNonNull(
                        luckStartCalculator,
                        "LuckStartCalculator가 필요합니다."
                );

        this.decadeLuckSequenceCalculator =
                Objects.requireNonNull(
                        decadeLuckSequenceCalculator,
                        "DecadeLuckSequenceCalculator가 필요합니다."
                );

        this.decadeLuckTenGodAnalyzer =
                Objects.requireNonNull(
                        decadeLuckTenGodAnalyzer,
                        "DecadeLuckTenGodAnalyzer가 필요합니다."
                );

        this.decadeLuckNatalRelationAnalyzer =
                Objects.requireNonNull(
                        decadeLuckNatalRelationAnalyzer,
                        "DecadeLuckNatalRelationAnalyzer가 필요합니다."
                );

        this.decadeLuckContextAnalyzer =
                Objects.requireNonNull(
                        decadeLuckContextAnalyzer,
                        "DecadeLuckContextAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 정확한 출생시간
     * =================================================
     *
     * 기본 10개 대운.
     */
    public DecadeLuckAnalysis analyze(

            Gender gender,

            FourPillars fourPillars,

            LocalDateTime birthDateTime,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver

    ) {

        return analyze(
                gender,
                fourPillars,
                birthDateTime,
                zoneId,
                palaceAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver,
                DEFAULT_PERIOD_COUNT
        );
    }

    /**
     * =================================================
     * 정확한 출생시간
     * +
     * 대운 생성 개수 지정
     * =================================================
     */
    public DecadeLuckAnalysis analyze(

            Gender gender,

            FourPillars fourPillars,

            LocalDateTime birthDateTime,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver,

            int periodCount

    ) {

        validateCommonInputs(
                gender,
                fourPillars,
                zoneId,
                palaceAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver
        );

        Objects.requireNonNull(
                birthDateTime,
                "정확한 대운 분석에는 출생일시가 필요합니다."
        );

        validateNatalIdentity(
                fourPillars,
                palaceAnalysis
        );

        validateKnownTimeState(
                fourPillars,
                palaceAnalysis
        );

        /*
         * =================================================
         * 13-1
         * =================================================
         */

        DecadeLuckDirectionAnalysis directionAnalysis =
                luckDirectionCalculator
                        .analyze(
                                gender,
                                fourPillars
                        );

        /*
         * =================================================
         * 13-2
         * =================================================
         */

        DecadeLuckStartAnalysis startAnalysis =
                luckStartCalculator
                        .analyze(
                                birthDateTime,
                                zoneId,
                                directionAnalysis
                        );

        /*
         * =================================================
         * 13-3 ~ 13-7
         * =================================================
         */

        return buildAnalysis(
                fourPillars,
                palaceAnalysis,
                directionAnalysis,
                startAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver,
                periodCount
        );
    }

    /**
     * =================================================
     * 출생시간 미상
     * =================================================
     *
     * 기본 10개 대운.
     *
     * 임의로 00:00 / 12:00를
     * 실제 출생시간으로 확정하지 않는다.
     */
    public DecadeLuckAnalysis analyzeUnknownTime(

            Gender gender,

            FourPillars fourPillars,

            LocalDate birthDate,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver

    ) {

        return analyzeUnknownTime(
                gender,
                fourPillars,
                birthDate,
                zoneId,
                palaceAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver,
                DEFAULT_PERIOD_COUNT
        );
    }

    /**
     * =================================================
     * 출생시간 미상
     * +
     * 대운 생성 개수 지정
     * =================================================
     */
    public DecadeLuckAnalysis analyzeUnknownTime(

            Gender gender,

            FourPillars fourPillars,

            LocalDate birthDate,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver,

            int periodCount

    ) {

        validateCommonInputs(
                gender,
                fourPillars,
                zoneId,
                palaceAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver
        );

        Objects.requireNonNull(
                birthDate,
                "출생시간 미상 대운 분석에는 출생일이 필요합니다."
        );

        validateNatalIdentity(
                fourPillars,
                palaceAnalysis
        );

        validateUnknownTimeState(
                fourPillars,
                palaceAnalysis
        );

        /*
         * =================================================
         * 13-1
         * =================================================
         */

        DecadeLuckDirectionAnalysis directionAnalysis =
                luckDirectionCalculator
                        .analyze(
                                gender,
                                fourPillars
                        );

        /*
         * =================================================
         * 13-2
         *
         * 시간 미상 범위 분석.
         * =================================================
         */

        DecadeLuckStartAnalysis startAnalysis =
                luckStartCalculator
                        .analyzeUnknownTime(
                                birthDate,
                                zoneId,
                                directionAnalysis
                        );

        /*
         * =================================================
         * 13-3 ~ 13-7
         * =================================================
         */

        return buildAnalysis(
                fourPillars,
                palaceAnalysis,
                directionAnalysis,
                startAnalysis,
                tenGodSourceResolver,
                natalCrossResolver,
                contextResolver,
                periodCount
        );
    }

    /**
     * =================================================
     * 13-3 ~ 13-7 공통 조립
     * =================================================
     */
    private DecadeLuckAnalysis buildAnalysis(

            FourPillars fourPillars,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckDirectionAnalysis directionAnalysis,

            DecadeLuckStartAnalysis startAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver,

            int periodCount

    ) {

        /*
         * =================================================
         * 13-3
         *
         * 대운 간지 Sequence
         * =================================================
         */

        DecadeLuckSequenceAnalysis sequenceAnalysis =
                decadeLuckSequenceCalculator
                        .analyze(
                                fourPillars,
                                startAnalysis,
                                periodCount
                        );

        /*
         * =================================================
         * 13-4
         *
         * 각 대운 십성
         * =================================================
         */

        DecadeLuckTenGodAnalysis tenGodAnalysis =
                decadeLuckTenGodAnalyzer
                        .analyze(
                                palaceAnalysis.dayMaster(),
                                sequenceAnalysis,
                                tenGodSourceResolver
                        );

        /*
         * =================================================
         * 13-5
         *
         * 원국 × 대운 관계
         * =================================================
         */

        DecadeLuckNatalRelationAnalysis natalRelationAnalysis =
                decadeLuckNatalRelationAnalyzer
                        .analyze(
                                palaceAnalysis,
                                sequenceAnalysis,
                                natalCrossResolver
                        );

        /*
         * =================================================
         * 13-6
         *
         * 용신·격국·십성·궁위 Context
         * =================================================
         */

        DecadeLuckContextAnalysis contextAnalysis =
                decadeLuckContextAnalyzer
                        .analyze(
                                tenGodAnalysis,
                                natalRelationAnalysis,
                                contextResolver
                        );

        /*
         * =================================================
         * 13-7
         *
         * 최종 Aggregate
         * =================================================
         */

        return new DecadeLuckAnalysis(
                palaceAnalysis.dayMaster(),
                palaceAnalysis,
                directionAnalysis,
                startAnalysis,
                sequenceAnalysis,
                tenGodAnalysis,
                natalRelationAnalysis,
                contextAnalysis
        );
    }

    /*
     * =================================================
     * 공통 입력 검증
     * =================================================
     */

    private void validateCommonInputs(

            Gender gender,

            FourPillars fourPillars,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckTenGodSourceResolver
                    tenGodSourceResolver,

            DecadeLuckNatalCrossResolver
                    natalCrossResolver,

            DecadeLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                gender,
                "대운 분석에는 성별이 필요합니다."
        );

        Objects.requireNonNull(
                fourPillars,
                "대운 분석에는 FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "대운 분석에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "대운 분석에는 12차 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodSourceResolver,
                "DecadeLuckTenGodSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                natalCrossResolver,
                "DecadeLuckNatalCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "DecadeLuckContextResolver가 필요합니다."
        );
    }

    /*
     * =================================================
     * FourPillars ↔ PalaceAnalysis 동일 원국 검증
     * =================================================
     *
     * 다른 사용자의 PalaceAnalysis가
     * 실수로 섞이는 것을 방지한다.
     */
    private void validateNatalIdentity(

            FourPillars fourPillars,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                fourPillars.yearPillar(),
                "년주가 필요합니다."
        );

        Objects.requireNonNull(
                fourPillars.monthPillar(),
                "월주가 필요합니다."
        );

        Objects.requireNonNull(
                fourPillars.dayPillar(),
                "일주가 필요합니다."
        );

        /*
         * =================================================
         * YEAR
         * =================================================
         */

        HeavenlyStem yearStem =
                Objects.requireNonNull(
                        fourPillars
                                .yearPillar()
                                .stem(),
                        "년간이 필요합니다."
                );

        EarthlyBranch yearBranch =
                Objects.requireNonNull(
                        fourPillars
                                .yearPillar()
                                .branch(),
                        "년지가 필요합니다."
                );

        if (
                palaceAnalysis
                        .yearStemEvidence()
                        .stem()
                        != yearStem

                        || palaceAnalysis
                        .yearBranchEvidence()
                        .branch()
                        != yearBranch
        ) {

            throw new IllegalArgumentException(
                    "FourPillars와 PalaceAnalysis의 년주가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * MONTH
         * =================================================
         */

        HeavenlyStem monthStem =
                Objects.requireNonNull(
                        fourPillars
                                .monthPillar()
                                .stem(),
                        "월간이 필요합니다."
                );

        EarthlyBranch monthBranch =
                Objects.requireNonNull(
                        fourPillars
                                .monthPillar()
                                .branch(),
                        "월지가 필요합니다."
                );

        if (
                palaceAnalysis
                        .monthStemEvidence()
                        .stem()
                        != monthStem

                        || palaceAnalysis
                        .monthBranchEvidence()
                        .branch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "FourPillars와 PalaceAnalysis의 월주가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * DAY
         * =================================================
         */

        HeavenlyStem dayStem =
                Objects.requireNonNull(
                        fourPillars
                                .dayPillar()
                                .stem(),
                        "일간이 필요합니다."
                );

        EarthlyBranch dayBranch =
                Objects.requireNonNull(
                        fourPillars
                                .dayPillar()
                                .branch(),
                        "일지가 필요합니다."
                );

        if (
                palaceAnalysis
                        .dayMasterAnchor()
                        .stem()
                        != dayStem

                        || palaceAnalysis
                        .dayBranchEvidence()
                        .branch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "FourPillars와 PalaceAnalysis의 일주가 일치하지 않습니다."
            );
        }

        if (
                palaceAnalysis.dayMaster()
                        != dayStem
        ) {

            throw new IllegalArgumentException(
                    "FourPillars의 일간과 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * HOUR
         * =================================================
         */

        if (
                fourPillars.hourPillar()
                        != null
        ) {

            HeavenlyStem hourStem =
                    Objects.requireNonNull(
                            fourPillars
                                    .hourPillar()
                                    .stem(),
                            "시간이 필요합니다."
                    );

            EarthlyBranch hourBranch =
                    Objects.requireNonNull(
                            fourPillars
                                    .hourPillar()
                                    .branch(),
                            "시지가 필요합니다."
                    );

            if (
                    palaceAnalysis
                            .hourStemEvidence()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "FourPillars에는 시주가 있으나 PalaceAnalysis에는 시궁이 없습니다."
                                            )
                            )
                            .stem()
                            != hourStem
            ) {

                throw new IllegalArgumentException(
                        "FourPillars와 PalaceAnalysis의 시간이 일치하지 않습니다."
                );
            }

            if (
                    palaceAnalysis
                            .hourBranchEvidence()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "FourPillars에는 시주가 있으나 PalaceAnalysis에는 시지가 없습니다."
                                            )
                            )
                            .branch()
                            != hourBranch
            ) {

                throw new IllegalArgumentException(
                        "FourPillars와 PalaceAnalysis의 시지가 일치하지 않습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 출생시간 확인 상태
     * =================================================
     */

    private void validateKnownTimeState(

            FourPillars fourPillars,

            PalaceAnalysis palaceAnalysis

    ) {

        if (
                fourPillars.hourPillar()
                        == null
        ) {

            throw new IllegalArgumentException(
                    "정확한 출생시간 분석 API에서는 FourPillars의 시주가 필요합니다."
            );
        }

        if (
                !palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "정확한 출생시간 분석 API에서는 complete PalaceAnalysis가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 출생시간 미상 상태
     * =================================================
     */

    private void validateUnknownTimeState(

            FourPillars fourPillars,

            PalaceAnalysis palaceAnalysis

    ) {

        if (
                fourPillars.hourPillar()
                        != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상 분석에서는 FourPillars에 시주를 생성하면 안 됩니다."
            );
        }

        if (
                palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상 분석에서는 complete PalaceAnalysis를 사용할 수 없습니다."
            );
        }

        if (
                palaceAnalysis
                        .hourContext()
                        .isPresent()
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상 원국에 HOUR Palace Context가 존재합니다."
            );
        }
    }
}