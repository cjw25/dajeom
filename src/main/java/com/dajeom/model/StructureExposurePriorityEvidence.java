package com.dajeom.model;

import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StructureExposurePriority;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 5차-2.
 *
 * 월령 격 후보 하나의
 * 투출 우선순위 Evidence.
 *
 * 중요한 구분:
 *
 * sourceAuthorityRank
 * -> 정기/중기/여기 자체의 월령 내부 권위
 *
 * exposurePriority
 * -> 천간에 얼마나 직접적으로 드러났는가
 *
 * 둘을 분리한다.
 *
 * 따라서:
 *
 * 중기가 월간에 투출했다고 해서
 * 월령 정기의 존재 자체가 사라지지 않는다.
 */
public record StructureExposurePriorityEvidence(

        MonthStructureCandidate candidate,

        StructureExposurePriority exposurePriority,

        int sourceAuthorityRank,

        int exposureRank,

        boolean monthStemExposed,

        boolean otherStemExposed,

        boolean repeatedExposure,

        int visibleStemCount,

        int exposureSourceWeight

) {

    public StructureExposurePriorityEvidence {

        Objects.requireNonNull(
                candidate,
                "월령 격 후보가 필요합니다."
        );

        Objects.requireNonNull(
                exposurePriority,
                "투출 우선순위가 필요합니다."
        );

        if (
                sourceAuthorityRank < 1
                        || sourceAuthorityRank > 3
        ) {

            throw new IllegalArgumentException(
                    "월령 source 권위는 1~3 사이여야 합니다."
            );
        }

        if (
                exposureRank
                        != exposurePriority.rank()
        ) {

            throw new IllegalArgumentException(
                    "투출 우선순위 rank가 enum과 일치하지 않습니다."
            );
        }

        if (
                visibleStemCount < 0
        ) {

            throw new IllegalArgumentException(
                    "투출 천간 수는 음수가 될 수 없습니다."
            );
        }

        if (
                exposureSourceWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "투간 활성도는 음수가 될 수 없습니다."
            );
        }

        if (
                monthStemExposed
                        != candidate.exposedAtMonthStem()
        ) {

            throw new IllegalArgumentException(
                    "월간 투출 여부가 후보 정보와 일치하지 않습니다."
            );
        }

        boolean expectedOtherStemExposure =
                candidate.exposedAtYearStem()
                        || candidate.exposedAtHourStem();

        if (
                otherStemExposed
                        != expectedOtherStemExposure
        ) {

            throw new IllegalArgumentException(
                    "타간 투출 여부가 후보 정보와 일치하지 않습니다."
            );
        }

        if (
                repeatedExposure
                        != candidate.repeatedExposure()
        ) {

            throw new IllegalArgumentException(
                    "반복 투출 여부가 후보 정보와 일치하지 않습니다."
            );
        }

        if (
                visibleStemCount
                        != candidate.visibleStemCount()
        ) {

            throw new IllegalArgumentException(
                    "투출 천간 수가 후보 정보와 일치하지 않습니다."
            );
        }

        if (
                exposureSourceWeight
                        != candidate.exposureSourceWeight()
        ) {

            throw new IllegalArgumentException(
                    "투간 활성도가 후보 정보와 일치하지 않습니다."
            );
        }

        if (
                exposurePriority.mainQi()
                        != candidate.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "투출 우선순위의 정기 여부와 후보 역할이 일치하지 않습니다."
            );
        }

        if (
                exposurePriority.exposed()
                        != candidate.exposed()
        ) {

            throw new IllegalArgumentException(
                    "투출 우선순위와 후보의 투간 여부가 일치하지 않습니다."
            );
        }
    }

    public HiddenStemRole hiddenStemRole() {

        return candidate.hiddenStemRole();
    }

    public StructureTenGodType tenGod() {

        return candidate.tenGod();
    }

    public boolean mainQi() {

        return candidate.mainQi();
    }

    public boolean middleQi() {

        return candidate.middleQi();
    }

    public boolean residualQi() {

        return candidate.residualQi();
    }

    public boolean exposed() {

        return candidate.exposed();
    }

    public boolean hiddenOnly() {

        return !exposed();
    }

    /**
     * 월령 본기 자체의 권위를 가진 후보인가.
     */
    public boolean monthCommandAuthority() {

        return mainQi();
    }

    /**
     * 투출 근거가 월간에 직접 나타났는가.
     */
    public boolean directMonthStemExpression() {

        return monthStemExposed;
    }

    /**
     * 정기가 월간에 그대로 투출한
     * 가장 직접적인 월령 표현.
     */
    public boolean mainQiMonthStemExpression() {

        return mainQi()
                && monthStemExposed;
    }

    /**
     * 하나의 월령 지장간이
     * 둘 이상의 외부 천간 위치에
     * 반복해서 나타난 구조.
     */
    public boolean reinforcedByRepeatedExposure() {

        return repeatedExposure
                && visibleStemCount >= 2;
    }

    /**
     * 투출은 되었지만
     * 월간이 아닌 년간/시간에만 나타난 경우.
     */
    public boolean exposedOnlyOutsideMonthStem() {

        return exposed()
                && !monthStemExposed
                && otherStemExposed;
    }
}