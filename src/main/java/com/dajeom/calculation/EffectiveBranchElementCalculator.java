package com.dajeom.calculation;

import com.dajeom.domain.BranchCombinationOccurrence;
import com.dajeom.domain.BranchCombinationTransformationAssessment;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.EffectiveBranchElementAnalysis;
import com.dajeom.domain.EffectiveBranchElementState;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 지지의 본래 오행과
 * ACCEPTED 합화 작용 오행을 함께 계산한다.
 *
 * =================================================
 *
 * 절대로 하지 않는 것:
 *
 * - EarthlyBranch 변경
 * - 지장간 삭제
 * - 본래 오행 덮어쓰기
 *
 * =================================================
 *
 * 예:
 *
 * 辰酉合化金 ACCEPTED
 *
 * 辰:
 * originalElement = EARTH
 * transformationElement = METAL
 *
 * 酉:
 * originalElement = METAL
 * transformationElement = METAL
 *
 * 辰은 여전히 辰이고,
 * 辰의 지장간도 그대로 남는다.
 */
@Component
public class EffectiveBranchElementCalculator {

    public EffectiveBranchElementAnalysis analyze(

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch,

            List<BranchCombinationTransformationAssessment>
                    transformationAssessments

    ) {

        Objects.requireNonNull(
                yearBranch,
                "년지는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지는 필수입니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지 Optional은 필수입니다."
        );

        List<BranchCombinationTransformationAssessment>
                safeAssessments =
                transformationAssessments == null
                        ? List.of()
                        : List.copyOf(
                        transformationAssessments
                );

        EffectiveBranchElementState year =
                createState(
                        PillarPosition.YEAR,
                        yearBranch,
                        safeAssessments
                );

        EffectiveBranchElementState month =
                createState(
                        PillarPosition.MONTH,
                        monthBranch,
                        safeAssessments
                );

        EffectiveBranchElementState day =
                createState(
                        PillarPosition.DAY,
                        dayBranch,
                        safeAssessments
                );

        Optional<EffectiveBranchElementState> hour =
                hourBranch.map(
                        branch ->
                                createState(
                                        PillarPosition.HOUR,
                                        branch,
                                        safeAssessments
                                )
                );

        EffectiveBranchElementAnalysis result =
                new EffectiveBranchElementAnalysis(
                        year,
                        month,
                        day,
                        hour
                );

        /*
         * 서로 다른 합화 오행이 동시에
         * ACCEPTED됐다면 이전 단계의
         * 경쟁 관계 계산이 빠졌을 가능성이 크다.
         *
         * 여기서는 임의로 하나를 선택하지 않는다.
         */
        if (
                result
                        .hasTransformationConflict()
        ) {

            throw new IllegalStateException(
                    "동일 지지에 서로 다른 합화 오행이 동시에 ACCEPTED되었습니다. "
                            +
                            "합 경쟁 Context를 확인해야 합니다."
            );
        }

        return result;
    }

    private EffectiveBranchElementState createState(

            PillarPosition position,

            EarthlyBranch branch,

            List<BranchCombinationTransformationAssessment>
                    assessments

    ) {

        List<BranchCombinationTransformationAssessment>
                accepted =
                assessments
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .filter(
                                BranchCombinationTransformationAssessment
                                        ::transformationAccepted
                        )
                        .filter(
                                assessment ->
                                        matchesParticipant(
                                                assessment,
                                                position,
                                                branch
                                        )
                        )
                        .toList();

        return new EffectiveBranchElementState(
                position,
                branch,
                originalElementOf(
                        branch
                ),
                accepted
        );
    }

    /**
     * occurrence의 positions와 branches는
     * 같은 인덱스끼리 하나의 실제 기둥을 뜻한다.
     *
     * 단순히:
     *
     * positions.contains(position)
     * &&
     * branches.contains(branch)
     *
     * 로 검사하지 않는다.
     *
     * 같은 지지가 원국에 반복될 수 있기 때문이다.
     */
    private boolean matchesParticipant(

            BranchCombinationTransformationAssessment assessment,

            PillarPosition position,

            EarthlyBranch branch

    ) {

        BranchCombinationOccurrence occurrence =
                assessment.occurrence();

        List<PillarPosition> positions =
                occurrence.positions();

        List<EarthlyBranch> branches =
                occurrence.branches();

        for (
                int i = 0;
                i < positions.size();
                i++
        ) {

            if (
                    positions.get(i)
                            == position
                            &&
                            branches.get(i)
                                    == branch
            ) {

                return true;
            }
        }

        return false;
    }

    /**
     * 지지의 본래 오행.
     *
     * 子亥 = 水
     * 寅卯 = 木
     * 巳午 = 火
     * 申酉 = 金
     * 辰戌丑未 = 土
     *
     * 합화 여부와 관계없이
     * 이 값은 원국 기본 정보로 유지된다.
     */
    public FiveElement originalElementOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return switch (branch) {

            case ZI, HAI ->
                    FiveElement.WATER;

            case YIN, MAO ->
                    FiveElement.WOOD;

            case SI, WU ->
                    FiveElement.FIRE;

            case SHEN, YOU ->
                    FiveElement.METAL;

            case CHEN, XU, CHOU, WEI ->
                    FiveElement.EARTH;
        };
    }
}