package com.dajeom.resolver;

import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckResolvedTenGodSource;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckTenGodSourceType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-3.
 *
 * MonthlyLuckTenGodSourceResolver의
 * 실제 운영 구현체.
 *
 * =================================================
 *
 * 역할:
 *
 * 기존 canonical source인:
 *
 * - HiddenStemTable
 * - StructureTenGodCalculator
 *
 * 를 사용해서
 *
 * MonthlyLuckPeriod
 *
 * 의:
 *
 * 1. 월운 천간
 * 2. 월운 지지 본기
 * 3. 월운 지지 중기
 * 4. 월운 지지 여기
 *
 * 를 정확한 십성 Source로 변환한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 클래스에서는:
 *
 * - 지장간표를 새로 정의하지 않는다.
 * - 십성 공식을 새로 구현하지 않는다.
 * - 지장간 가중치를 새로 계산하지 않는다.
 * - 월운 Period를 다시 생성하지 않는다.
 * - 길흉 점수를 만들지 않는다.
 *
 * =================================================
 *
 * Source 순서:
 *
 * VISIBLE_STEM
 * BRANCH_MAIN
 * BRANCH_MIDDLE
 * BRANCH_RESIDUAL
 *
 * =================================================
 *
 * 중기/여기가 실제 지지에 존재하지 않으면
 * 해당 Source 자체를 생성하지 않는다.
 */
@Component
public class DefaultMonthlyLuckTenGodSourceResolver
        implements MonthlyLuckTenGodSourceResolver {

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    public DefaultMonthlyLuckTenGodSourceResolver(
            StructureTenGodCalculator
                    structureTenGodCalculator
    ) {

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-3 Boundary 구현
     * =================================================
     */
    @Override
    public List<MonthlyLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            MonthlyLuckPeriod monthlyLuckPeriod

    ) {

        Objects.requireNonNull(
                dayMaster,
                "월운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckPeriod,
                "월운 십성 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        HeavenlyStem monthlyStem =
                Objects.requireNonNull(
                        monthlyLuckPeriod
                                .pillar()
                                .stem(),
                        "MonthlyLuckPeriod의 월운 천간이 필요합니다."
                );

        EarthlyBranch monthlyBranch =
                Objects.requireNonNull(
                        monthlyLuckPeriod
                                .pillar()
                                .branch(),
                        "MonthlyLuckPeriod의 월운 지지가 필요합니다."
                );

        List<MonthlyLuckResolvedTenGodSource> result =
                new ArrayList<>(
                        4
                );

        /*
         * =================================================
         * 1.
         * 월운 천간
         * =================================================
         *
         * MonthlyLuckPeriod의 실제 천간을
         * 그대로 사용한다.
         */

        result.add(
                createSource(
                        MonthlyLuckTenGodSourceType
                                .VISIBLE_STEM,

                        dayMaster,

                        monthlyStem
                )
        );

        /*
         * =================================================
         * 2.
         * 월운 지지 본기
         * =================================================
         *
         * 지장간을 직접 정의하지 않고
         * 기존 HiddenStemTable.main()을 사용한다.
         */

        HiddenStem mainHiddenStem =
                HiddenStemTable
                        .main(
                                monthlyBranch
                        );

        result.add(
                createSource(
                        MonthlyLuckTenGodSourceType
                                .BRANCH_MAIN,

                        dayMaster,

                        mainHiddenStem
                                .stem()
                )
        );

        /*
         * =================================================
         * 3.
         * 월운 지지 중기
         * =================================================
         *
         * 실제 중기가 존재하는 지지만 추가한다.
         */

        HiddenStemTable
                .middle(
                        monthlyBranch
                )
                .ifPresent(
                        hiddenStem ->
                                result.add(
                                        createSource(
                                                MonthlyLuckTenGodSourceType
                                                        .BRANCH_MIDDLE,

                                                dayMaster,

                                                hiddenStem
                                                        .stem()
                                        )
                                )
                );

        /*
         * =================================================
         * 4.
         * 월운 지지 여기
         * =================================================
         *
         * 실제 여기가 존재하는 지지만 추가한다.
         */

        HiddenStemTable
                .residual(
                        monthlyBranch
                )
                .ifPresent(
                        hiddenStem ->
                                result.add(
                                        createSource(
                                                MonthlyLuckTenGodSourceType
                                                        .BRANCH_RESIDUAL,

                                                dayMaster,

                                                hiddenStem
                                                        .stem()
                                        )
                                )
                );

        return List.copyOf(
                result
        );
    }

    /**
     * =================================================
     * Source 생성
     * =================================================
     *
     * 십성 공식은 이 클래스에서 구현하지 않는다.
     *
     * 기존 StructureTenGodCalculator의
     * 공식 계산 결과만 사용한다.
     */
    private MonthlyLuckResolvedTenGodSource createSource(

            MonthlyLuckTenGodSourceType sourceType,

            HeavenlyStem dayMaster,

            HeavenlyStem sourceStem

    ) {

        Objects.requireNonNull(
                sourceType,
                "월운 십성 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "월운 십성 계산에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "월운 십성 계산에는 Source 천간이 필요합니다."
        );

        StructureTenGodType tenGodType =
                structureTenGodCalculator
                        .calculate(
                                dayMaster,
                                sourceStem
                        );

        return new MonthlyLuckResolvedTenGodSource(
                sourceType,
                sourceStem,
                tenGodType
        );
    }
}