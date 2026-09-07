package com.dajeom.calculation;

import com.dajeom.domain.DayMasterForceFormationAnalysis;
import com.dajeom.domain.DayMasterGroundAnalysis;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterThreeFactorsAnalysis;
import com.dajeom.domain.DayMasterVisibleStemRelation;
import com.dajeom.domain.DayMasterVisibleSupportPattern;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 得令 / 得地 / 得勢 영역의
 * 원형 구조를 하나로 정리한다.
 *
 * 중요한 원칙:
 *
 * - 점수화하지 않는다.
 * - 다수결하지 않는다.
 * - true 개수를 세지 않는다.
 * - 여기서 신강/신약을 결정하지 않는다.
 */
@Component
public class DayMasterThreeFactorsCalculator {

    public DayMasterThreeFactorsAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            DayMasterGroundAnalysis ground,

            DayMasterForceFormationAnalysis forceFormation

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 계절 상태는 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "지지 뿌리 구조는 필수입니다."
        );

        Objects.requireNonNull(
                forceFormation,
                "세력 형성 구조는 필수입니다."
        );

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 계절 상태의 일간이 일치하지 않습니다."
            );
        }

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "지지 뿌리 구조의 일간이 일치하지 않습니다."
            );
        }

        if (
                forceFormation.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "세력 형성 구조의 일간이 일치하지 않습니다."
            );
        }

        List<DayMasterVisibleStemRelation>
                generating =
                forceFormation
                        .visibleGeneratingDayMaster();

        List<DayMasterVisibleStemRelation>
                sameElement =
                forceFormation
                        .visibleSameElement();

        DayMasterVisibleSupportPattern pattern =
                resolveVisibleSupportPattern(
                        generating,
                        sameElement
                );

        return new DayMasterThreeFactorsAnalysis(
                dayMaster,
                season,
                ground,
                pattern,
                generating,
                sameElement
        );
    }

    private DayMasterVisibleSupportPattern
    resolveVisibleSupportPattern(

            List<DayMasterVisibleStemRelation> generating,

            List<DayMasterVisibleStemRelation> sameElement

    ) {

        boolean hasGenerating =
                generating != null
                        &&
                        !generating.isEmpty();

        boolean hasSameElement =
                sameElement != null
                        &&
                        !sameElement.isEmpty();

        if (
                hasGenerating
                        &&
                        hasSameElement
        ) {

            return DayMasterVisibleSupportPattern
                    .GENERATING_AND_SAME_ELEMENT;
        }

        if (hasGenerating) {

            return DayMasterVisibleSupportPattern
                    .GENERATING_ONLY;
        }

        if (hasSameElement) {

            return DayMasterVisibleSupportPattern
                    .SAME_ELEMENT_ONLY;
        }

        return DayMasterVisibleSupportPattern
                .NO_VISIBLE_SUPPORT;
    }
}