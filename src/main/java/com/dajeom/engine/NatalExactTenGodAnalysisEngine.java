package com.dajeom.engine;

import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalExactTenGodAnalysis;
import com.dajeom.model.NatalExactTenGodProfile;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 기존 10차에서 이미 계산된 정확한 십성 분포를
 * 5개 Family로 합치지 않고
 * 10십성 그대로 보존하는 Engine.
 *
 * =================================================
 *
 * 입력
 *
 * NatalChartAdvancedAnalysis
 *
 * +
 *
 * TenGodCompositeAnalysis
 *
 * =================================================
 *
 * 사용하는 기존 계산
 *
 * 5차:
 * StructureAnalysis.primaryStandardTenGod()
 *
 * 10-1:
 * TenGodDistributionAnalysis
 *
 * =================================================
 *
 * 하지 않는 것
 *
 * - 새로운 십성 세력 계산
 * - 새로운 가중치
 * - 새로운 성격 점수
 * - 5개 Family 합성
 * - 정재/편재 통합 Meaning
 * - 정관/편관 통합 Meaning
 * - 식신/상관 통합 Meaning
 *
 * 정확한 10십성을 그대로 보존한다.
 */
@Component
public class NatalExactTenGodAnalysisEngine {

    public NatalExactTenGodAnalysis analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "Exact TenGod 분석에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Exact TenGod 분석에는 TenGodCompositeAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                advancedAnalysis
                        .coreAnalysis()
                        .dayMaster()
                        != tenGodCompositeAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "Advanced Analysis와 TenGod Composite의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성 동일성
         * =================================================
         */

        if (
                advancedAnalysis
                        .hasHourPillar()
                        != tenGodCompositeAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Advanced Analysis와 TenGod Composite의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        TenGodDistributionAnalysis
                distributionAnalysis =
                tenGodCompositeAnalysis
                        .distributionAnalysis();

        Optional<StructureTenGodType>
                primaryStructureTenGod =
                advancedAnalysis
                        .structureAnalysis()
                        .primaryStandardTenGod();

        List<NatalExactTenGodProfile> profiles =
                new ArrayList<>();

        /*
         * =================================================
         * 정확한 10십성 모두 생성
         * =================================================
         */

        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            TenGodPowerSummary summary =
                    distributionAnalysis
                            .summaryOf(
                                    tenGodType
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "10-1 십성 Summary가 없습니다: "
                                                            + tenGodType
                                            )
                            );

            profiles.add(
                    new NatalExactTenGodProfile(

                            tenGodType,

                            summary,

                            primaryStructureTenGod
                                    .map(
                                            primary ->
                                                    primary
                                                            == tenGodType
                                    )
                                    .orElse(
                                            false
                                    ),

                            tenGodCompositeAnalysis
                                    .completeChart()
                    )
            );
        }

        return new NatalExactTenGodAnalysis(

                tenGodCompositeAnalysis
                        .dayMaster(),

                distributionAnalysis,

                primaryStructureTenGod,

                List.copyOf(
                        profiles
                ),

                tenGodCompositeAnalysis
                        .completeChart()
        );
    }
}