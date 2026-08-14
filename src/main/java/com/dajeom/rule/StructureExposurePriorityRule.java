package com.dajeom.rule;

import com.dajeom.model.MonthStructureCandidate;
import com.dajeom.type.StructureExposurePriority;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차-2.
 *
 * 월령 지장간 후보의
 * 투출 형태를 분류한다.
 *
 * 여기서는 임의의 종합점수를 만들지 않는다.
 *
 * 다음 두 축을 구분한다.
 *
 * 1.
 * 월령 내부 source 권위
 *
 * 정기 > 중기 > 여기
 *
 * 2.
 * 투출의 직접성
 *
 * 월간 투출
 * > 년간/시간 투출
 * > 미투출
 *
 * 최종 격국 결정은
 * 이 Rule의 책임이 아니다.
 */
@Component
public class StructureExposurePriorityRule {

    public StructureExposurePriority determinePriority(
            MonthStructureCandidate candidate
    ) {

        Objects.requireNonNull(
                candidate,
                "월령 격 후보가 필요합니다."
        );

        /*
         * ================================================
         * 정기
         * ================================================
         */
        if (
                candidate.mainQi()
        ) {

            if (
                    candidate.exposedAtMonthStem()
            ) {

                return StructureExposurePriority
                        .MAIN_QI_MONTH_STEM;
            }

            if (
                    candidate.exposedAtYearStem()
                            || candidate.exposedAtHourStem()
            ) {

                return StructureExposurePriority
                        .MAIN_QI_OTHER_STEM;
            }

            return StructureExposurePriority
                    .MAIN_QI_HIDDEN;
        }

        /*
         * ================================================
         * 중기
         * ================================================
         */
        if (
                candidate.middleQi()
        ) {

            if (
                    candidate.exposedAtMonthStem()
            ) {

                return StructureExposurePriority
                        .MIDDLE_QI_MONTH_STEM;
            }

            if (
                    candidate.exposedAtYearStem()
                            || candidate.exposedAtHourStem()
            ) {

                return StructureExposurePriority
                        .MIDDLE_QI_OTHER_STEM;
            }

            return StructureExposurePriority
                    .MIDDLE_QI_HIDDEN;
        }

        /*
         * ================================================
         * 여기
         * ================================================
         */
        if (
                candidate.residualQi()
        ) {

            if (
                    candidate.exposedAtMonthStem()
            ) {

                return StructureExposurePriority
                        .RESIDUAL_QI_MONTH_STEM;
            }

            if (
                    candidate.exposedAtYearStem()
                            || candidate.exposedAtHourStem()
            ) {

                return StructureExposurePriority
                        .RESIDUAL_QI_OTHER_STEM;
            }

            return StructureExposurePriority
                    .RESIDUAL_QI_HIDDEN;
        }

        throw new IllegalStateException(
                "알 수 없는 월령 지장간 역할입니다: "
                        + candidate.hiddenStemRole()
        );
    }

    /**
     * 월령 source 자체의 권위.
     *
     * 정기 = 3
     * 중기 = 2
     * 여기 = 1
     *
     * 투출 우선순위와 별도로 유지한다.
     */
    public int determineSourceAuthorityRank(
            MonthStructureCandidate candidate
    ) {

        Objects.requireNonNull(
                candidate,
                "월령 격 후보가 필요합니다."
        );

        if (
                candidate.mainQi()
        ) {

            return 3;
        }

        if (
                candidate.middleQi()
        ) {

            return 2;
        }

        if (
                candidate.residualQi()
        ) {

            return 1;
        }

        throw new IllegalStateException(
                "알 수 없는 월령 지장간 역할입니다."
        );
    }
}