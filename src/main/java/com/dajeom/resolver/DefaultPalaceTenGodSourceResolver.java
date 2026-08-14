package com.dajeom.resolver;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceResolvedTenGodSource;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceTenGodSourceType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 12차-3.
 *
 * PalaceTenGodSourceResolver 실제 운영 구현체.
 *
 * 기존:
 *
 * - HiddenStemCalculator
 * - StructureTenGodCalculator
 *
 * 를 12차 궁위 십성 분석에 연결한다.
 *
 * =================================================
 *
 * 처리:
 *
 * YEAR
 * - 년간 visible 십성
 * - 년지 본기/중기/여기 십성
 *
 * MONTH
 * - 월간 visible 십성
 * - 월지 본기/중기/여기 십성
 *
 * DAY
 * - 일간 자신은 외부 십성 Source에서 제외
 * - 일지 본기/중기/여기 십성
 *
 * HOUR
 * - 출생시간이 있는 경우만
 * - 시간 visible 십성
 * - 시지 본기/중기/여기 십성
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
 * - 출생시간 미상인데 HOUR를 생성하지 않는다.
 *
 * 기존 canonical Calculator 결과만 연결한다.
 */
@Component
public class DefaultPalaceTenGodSourceResolver
        implements PalaceTenGodSourceResolver {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    public DefaultPalaceTenGodSourceResolver(

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
    public List<PalaceResolvedTenGodSource> resolve(
            PalacePositionAnalysis palacePositionAnalysis
    ) {

        Objects.requireNonNull(
                palacePositionAnalysis,
                "PalacePositionAnalysis가 필요합니다."
        );

        HeavenlyStem dayMaster =
                Objects.requireNonNull(
                        palacePositionAnalysis.dayMaster(),
                        "궁위 십성 분석에는 일간이 필요합니다."
                );

        List<PalaceResolvedTenGodSource> result =
                new ArrayList<>();

        for (
                PalacePosition position
                : palacePositionAnalysis.knownPositions()
        ) {

            /*
             * =================================================
             * 천간 투출
             * =================================================
             *
             * DAY 천간은 일간 자신이므로
             * 외부 십성 Source로 추가하지 않는다.
             */

            if (
                    position != PalacePosition.DAY
            ) {

                HeavenlyStem visibleStem =
                        palacePositionAnalysis
                                .stemEvidenceOf(
                                        position
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "Palace 천간 Evidence가 없습니다: "
                                                                + position
                                                )
                                )
                                .stem();

                StructureTenGodType visibleTenGod =
                        structureTenGodCalculator
                                .calculate(
                                        dayMaster,
                                        visibleStem
                                );

                result.add(
                        new PalaceResolvedTenGodSource(
                                position,
                                PalaceTenGodSourceType.VISIBLE_STEM,
                                visibleStem,
                                visibleTenGod
                        )
                );
            }

            /*
             * =================================================
             * 지지 지장간
             * =================================================
             */

            EarthlyBranch branch =
                    palacePositionAnalysis
                            .branchEvidenceOf(
                                    position
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Palace 지지 Evidence가 없습니다: "
                                                            + position
                                            )
                            )
                            .branch();

            List<HiddenStem> hiddenStems =
                    hiddenStemCalculator
                            .calculate(
                                    branch
                            );

            for (
                    HiddenStem hiddenStem
                    : hiddenStems
            ) {

                PalaceTenGodSourceType sourceType =
                        resolveSourceType(
                                hiddenStem
                        );

                HeavenlyStem sourceStem =
                        hiddenStem.stem();

                StructureTenGodType tenGodType =
                        structureTenGodCalculator
                                .calculate(
                                        dayMaster,
                                        sourceStem
                                );

                result.add(
                        new PalaceResolvedTenGodSource(
                                position,
                                sourceType,
                                sourceStem,
                                tenGodType
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 기존 HiddenStem 역할을
     * 12차 Palace Source Type으로 변환한다.
     *
     * 지장간 역할 자체를 새로 판정하지 않는다.
     */
    private PalaceTenGodSourceType resolveSourceType(
            HiddenStem hiddenStem
    ) {

        Objects.requireNonNull(
                hiddenStem,
                "지장간이 필요합니다."
        );

        if (
                hiddenStem.main()
        ) {

            return PalaceTenGodSourceType.BRANCH_MAIN;
        }

        if (
                hiddenStem.middle()
        ) {

            return PalaceTenGodSourceType.BRANCH_MIDDLE;
        }

        if (
                hiddenStem.residual()
        ) {

            return PalaceTenGodSourceType.BRANCH_RESIDUAL;
        }

        throw new IllegalStateException(
                "지원하지 않는 지장간 역할입니다: "
                        + hiddenStem.role()
        );
    }
}