package com.dajeom.analyzer;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.TenGodCalculator;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.LuckTenGodInfluence;
import com.dajeom.model.Pillar;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 대운과 세운이
 * 원국 일간 기준으로 가지는
 * 십성을 분석한다.
 *
 * =================================================
 *
 * 현재 구조:
 *
 * DecadeLuckPeriod.pillar()
 * AnnualLuckAnalysis.annualPillar()
 *
 * 는 SexagenaryPillar다.
 *
 * 반면 LuckPillarSource의 관계 분석용 간지는
 * Pillar를 사용한다.
 *
 * 따라서 LuckPillarSource 생성 시점에만
 * SexagenaryPillar -> Pillar 변환을 수행한다.
 *
 * =================================================
 *
 * 세운 십성은 현재 14차에서
 * StructureTenGodType 기반 Evidence로도 보존되지만,
 *
 * 이 기존 LuckTenGodInfluence는
 * TenGod 타입을 사용하므로
 * 동일한 일간 / 천간을 기준으로
 * TenGodCalculator를 이용해 결정론적으로 계산한다.
 */
@Component
public class LuckTenGodAnalyzer {

    private final TenGodCalculator
            tenGodCalculator;

    private final HiddenStemCalculator
            hiddenStemCalculator;

    public LuckTenGodAnalyzer(
            TenGodCalculator tenGodCalculator,
            HiddenStemCalculator hiddenStemCalculator
    ) {

        this.tenGodCalculator =
                Objects.requireNonNull(
                        tenGodCalculator,
                        "TenGodCalculator가 필요합니다."
                );

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );
    }

    public List<LuckTenGodInfluence> analyze(
            HeavenlyStem dayMaster,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        if (
                annualLuckAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간과 세운 분석의 일간이 일치하지 않습니다."
            );
        }

        List<LuckTenGodInfluence> result =
                new ArrayList<>();

        /*
         * =================================================
         * 대운 십성
         * =================================================
         */
        if (
                decadeLuckPeriod != null
        ) {

            SexagenaryPillar decadePillar =
                    decadeLuckPeriod
                            .pillar();

            LuckPillarSource source =
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod
                                    .sequenceNumber(),
                            toPillar(
                                    decadePillar
                            )
                    );

            HiddenStem mainHiddenStem =
                    hiddenStemCalculator
                            .getMainHiddenStem(
                                    decadePillar
                                            .branch()
                            );

            if (
                    mainHiddenStem == null
            ) {

                throw new IllegalStateException(
                        "대운 지지의 정기 지장간을 찾을 수 없습니다: "
                                + decadePillar.branch()
                );
            }

            HeavenlyStem branchMainStem =
                    mainHiddenStem
                            .stem();

            TenGod stemTenGod =
                    tenGodCalculator
                            .calculate(
                                    dayMaster,
                                    decadePillar
                                            .stem()
                            );

            TenGod branchMainTenGod =
                    tenGodCalculator
                            .calculate(
                                    dayMaster,
                                    branchMainStem
                            );

            result.add(
                    new LuckTenGodInfluence(
                            source,
                            stemTenGod,
                            branchMainStem,
                            branchMainTenGod
                    )
            );
        }

        /*
         * =================================================
         * 세운 십성
         * =================================================
         */
        SexagenaryPillar annualPillar =
                annualLuckAnalysis
                        .annualPillar();

        LuckPillarSource annualSource =
                new LuckPillarSource(
                        LuckSourceType.ANNUAL_LUCK,
                        null,
                        annualLuckAnalysis
                                .targetYear(),
                        toPillar(
                                annualPillar
                        )
                );

        HiddenStem annualMainHiddenStem =
                hiddenStemCalculator
                        .getMainHiddenStem(
                                annualPillar
                                        .branch()
                        );

        if (
                annualMainHiddenStem == null
        ) {

            throw new IllegalStateException(
                    "세운 지지의 정기 지장간을 찾을 수 없습니다: "
                            + annualPillar.branch()
            );
        }

        HeavenlyStem annualBranchMainStem =
                annualMainHiddenStem
                        .stem();

        TenGod annualStemTenGod =
                tenGodCalculator
                        .calculate(
                                dayMaster,
                                annualPillar
                                        .stem()
                        );

        TenGod annualBranchMainTenGod =
                tenGodCalculator
                        .calculate(
                                dayMaster,
                                annualBranchMainStem
                        );

        result.add(
                new LuckTenGodInfluence(
                        annualSource,
                        annualStemTenGod,
                        annualBranchMainStem,
                        annualBranchMainTenGod
                )
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 운 전용 SexagenaryPillar를
     * LuckPillarSource에서 사용하는 Pillar로 변환한다.
     *
     * 간지의 천간과 지지만 복사하며,
     * 대운 순번 / 세운 연도는
     * LuckPillarSource의 별도 메타데이터로 유지한다.
     */
    private Pillar toPillar(
            SexagenaryPillar sexagenaryPillar
    ) {

        Objects.requireNonNull(
                sexagenaryPillar,
                "운 간지가 필요합니다."
        );

        return new Pillar(
                sexagenaryPillar.stem(),
                sexagenaryPillar.branch()
        );
    }
}