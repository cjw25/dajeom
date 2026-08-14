package com.dajeom.resolver;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.DecadeLuckResolvedTenGodSource;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.DecadeLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-4.
 *
 * DecadeLuckTenGodSourceResolver 실제 운영 구현체.
 *
 * 기존:
 *
 * - HiddenStemCalculator
 * - StructureTenGodCalculator
 *
 * 를 대운 십성 분석에 연결한다.
 *
 * =================================================
 *
 * 처리:
 *
 * 대운 천간
 * - VISIBLE_STEM
 * - 일간 기준 십성
 *
 * 대운 지지
 * - BRANCH_MAIN
 * - BRANCH_MIDDLE
 * - BRANCH_RESIDUAL
 * - 각 지장간의 일간 기준 십성
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Resolver에서는:
 *
 * - 새로운 지장간표를 만들지 않는다.
 * - 새로운 십성 공식을 만들지 않는다.
 * - 지장간 weight를 다시 계산하지 않는다.
 * - Source Type에 임의 가중치를 부여하지 않는다.
 *
 * 기존 canonical Calculator 결과만
 * 13차 대운 분석 구조로 변환한다.
 */
@Component
public class DefaultDecadeLuckTenGodSourceResolver
        implements DecadeLuckTenGodSourceResolver {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    public DefaultDecadeLuckTenGodSourceResolver(

            HiddenStemCalculator
                    hiddenStemCalculator,

            StructureTenGodCalculator
                    structureTenGodCalculator

    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );
    }

    @Override
    public List<DecadeLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            SexagenaryPillar luckPillar

    ) {

        Objects.requireNonNull(
                dayMaster,
                "대운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                luckPillar,
                "대운 간지가 필요합니다."
        );

        List<DecadeLuckResolvedTenGodSource> result =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 대운 천간
         * =================================================
         */

        HeavenlyStem visibleStem =
                Objects.requireNonNull(
                        luckPillar.stem(),
                        "대운 천간이 필요합니다."
                );

        StructureTenGodType visibleTenGod =
                structureTenGodCalculator
                        .calculate(
                                dayMaster,
                                visibleStem
                        );

        result.add(
                new DecadeLuckResolvedTenGodSource(
                        DecadeLuckTenGodSourceType.VISIBLE_STEM,
                        visibleStem,
                        visibleTenGod
                )
        );

        /*
         * =================================================
         * 2.
         * 대운 지지 지장간
         * =================================================
         */

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator
                        .calculate(
                                Objects.requireNonNull(
                                        luckPillar.branch(),
                                        "대운 지지가 필요합니다."
                                )
                        );

        for (
                HiddenStem hiddenStem
                : hiddenStems
        ) {

            Objects.requireNonNull(
                    hiddenStem,
                    "대운 지장간은 null일 수 없습니다."
            );

            DecadeLuckTenGodSourceType sourceType =
                    resolveSourceType(
                            hiddenStem
                    );

            HeavenlyStem sourceStem =
                    Objects.requireNonNull(
                            hiddenStem.stem(),
                            "대운 지장간 천간이 필요합니다."
                    );

            StructureTenGodType tenGodType =
                    structureTenGodCalculator
                            .calculate(
                                    dayMaster,
                                    sourceStem
                            );

            result.add(
                    new DecadeLuckResolvedTenGodSource(
                            sourceType,
                            sourceStem,
                            tenGodType
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 기존 HiddenStem 역할을
     * 13차 대운 십성 Source Type으로 변환한다.
     *
     * 지장간 역할을 새로 판정하지 않는다.
     */
    private DecadeLuckTenGodSourceType resolveSourceType(
            HiddenStem hiddenStem
    ) {

        Objects.requireNonNull(
                hiddenStem,
                "지장간이 필요합니다."
        );

        if (
                hiddenStem.main()
        ) {

            return DecadeLuckTenGodSourceType.BRANCH_MAIN;
        }

        if (
                hiddenStem.middle()
        ) {

            return DecadeLuckTenGodSourceType.BRANCH_MIDDLE;
        }

        if (
                hiddenStem.residual()
        ) {

            return DecadeLuckTenGodSourceType.BRANCH_RESIDUAL;
        }

        throw new IllegalStateException(
                "지원하지 않는 지장간 역할입니다: "
                        + hiddenStem.role()
        );
    }
}