package com.dajeom.resolver;

import com.dajeom.analyzer.DecadeLuckContextSignalAnalyzer;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 13차-6.
 *
 * 요청별 immutable DecadeLuckContextResolver를 생성한다.
 *
 * =================================================
 *
 * Resolver가 참조하는 원국:
 *
 * 5차 StructureAnalysis
 * 7차 UsefulGodAnalysis
 * 10차 TenGodCompositeAnalysis
 * 12차 PalaceAnalysis
 *
 * =================================================
 *
 * Spring singleton Resolver에
 * 사용자별 원국 상태를 저장하지 않는다.
 */
@Component
public class DecadeLuckContextResolverFactory {

    private final DecadeLuckContextSignalAnalyzer
            decadeLuckContextSignalAnalyzer;

    public DecadeLuckContextResolverFactory(
            DecadeLuckContextSignalAnalyzer
                    decadeLuckContextSignalAnalyzer
    ) {

        this.decadeLuckContextSignalAnalyzer =
                Objects.requireNonNull(
                        decadeLuckContextSignalAnalyzer,
                        "DecadeLuckContextSignalAnalyzer가 필요합니다."
                );
    }

    public DecadeLuckContextResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "원국 고급 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "10차 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "12차 PalaceAnalysis가 필요합니다."
        );

        validateIdentity(
                advancedAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis
        );

        return (
                luckTenGodAnalysis,
                natalRelationAnalysis
        ) ->
                decadeLuckContextSignalAnalyzer
                        .analyze(
                                advancedAnalysis
                                        .structureAnalysis(),

                                advancedAnalysis
                                        .usefulGodAnalysis(),

                                tenGodCompositeAnalysis,

                                palaceAnalysis,

                                luckTenGodAnalysis,

                                natalRelationAnalysis
                        );
    }

    private void validateIdentity(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        if (
                tenGodCompositeAnalysis
                        .dayMasterStrengthAnalysis()

                        != advancedAnalysis
                        .dayMasterStrengthAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 4차 신강신약 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .structureAnalysis()

                        != advancedAnalysis
                        .structureAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 5차 격국 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .usefulGodAnalysis()

                        != advancedAnalysis
                        .usefulGodAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 7차 용신 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .combinationTransformationAnalysis()

                        != advancedAnalysis
                        .combinationTransformationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 9차 합화 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 분석과 12차 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.completeChart()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 분석과 12차 PalaceAnalysis의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}