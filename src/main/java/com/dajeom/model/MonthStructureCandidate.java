package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-1.
 *
 * 월지 지장간 하나에서 생성된
 * 격국 후보.
 *
 * 예:
 *
 * 丁 일간
 * 申 월지
 *
 * 庚 정기
 * -> 丁이 庚을 극함
 * -> 음양 반대
 * -> 정재
 *
 * 따라서 DIRECT_WEALTH 후보가 된다.
 *
 * 주의:
 *
 * 이 객체가 존재한다고 해서
 * 해당 격국이 성립한 것은 아니다.
 *
 * 이후 5-2 ~ 5-10에서:
 *
 * - 투간
 * - 위치
 * - 세력
 * - 일간 강약
 * - 성격
 * - 파격
 * - 혼잡
 *
 * 등을 추가 검토한다.
 */
public record MonthStructureCandidate(

        HeavenlyStem dayMaster,

        HeavenlyStem hiddenStem,

        HiddenStemRole hiddenStemRole,

        int hiddenStemWeight,

        StructureTenGodType tenGod,

        boolean exposed,

        boolean exposedAtYearStem,

        boolean exposedAtMonthStem,

        boolean exposedAtHourStem,

        boolean repeatedExposure,

        int visibleStemCount,

        int exposureSourceWeight

) {

    public MonthStructureCandidate {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "월지 지장간이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStemRole,
                "지장간 역할이 필요합니다."
        );

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        if (
                hiddenStemWeight <= 0
                        || hiddenStemWeight > 100
        ) {

            throw new IllegalArgumentException(
                    "지장간 세력은 1~100 사이여야 합니다."
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

        boolean actualExposed =
                exposedAtYearStem
                        || exposedAtMonthStem
                        || exposedAtHourStem;

        if (
                exposed != actualExposed
        ) {

            throw new IllegalArgumentException(
                    "투간 여부와 실제 투출 위치가 일치하지 않습니다."
            );
        }

        if (
                exposed
                        && visibleStemCount <= 0
        ) {

            throw new IllegalArgumentException(
                    "투간된 후보는 visibleStemCount가 1 이상이어야 합니다."
            );
        }

        if (
                !exposed
                        && visibleStemCount != 0
        ) {

            throw new IllegalArgumentException(
                    "투간되지 않은 후보의 visibleStemCount는 0이어야 합니다."
            );
        }

        if (
                repeatedExposure
                        != (
                        visibleStemCount >= 2
                )
        ) {

            throw new IllegalArgumentException(
                    "반복 투출 여부와 천간 수가 일치하지 않습니다."
            );
        }
    }

    public boolean mainQi() {

        return hiddenStemRole.isMain();
    }

    public boolean middleQi() {

        return hiddenStemRole.isMiddle();
    }

    public boolean residualQi() {

        return hiddenStemRole.isResidual();
    }

    /**
     * 월령의 정기가 그대로
     * 월간에 투출한 가장 직접적인 형태.
     */
    public boolean monthMainQiExposed() {

        return mainQi()
                && exposedAtMonthStem;
    }

    /**
     * 정기가 어떤 천간 위치로든
     * 투출했는가.
     */
    public boolean mainQiExposed() {

        return mainQi()
                && exposed;
    }

    /**
     * 중기/여기가 실제 천간으로
     * 투출했는가.
     */
    public boolean secondaryQiExposed() {

        return !mainQi()
                && exposed;
    }

    /**
     * 월간에 직접 올라온 후보.
     *
     * 5-2 우선순위 분석에서
     * 중요한 Evidence가 된다.
     */
    public boolean monthStemExposed() {

        return exposedAtMonthStem;
    }

    public boolean hiddenOnly() {

        return !exposed;
    }

    public boolean wealthCandidate() {

        return tenGod.wealthGroup();
    }

    public boolean officerCandidate() {

        return tenGod.officerGroup();
    }

    public boolean outputCandidate() {

        return tenGod.outputGroup();
    }

    public boolean resourceCandidate() {

        return tenGod.resourceGroup();
    }

    public boolean companionCandidate() {

        return tenGod.companionGroup();
    }

    /**
     * 아직 격국 확정 점수가 아니다.
     *
     * 후보의 지장간 원래 비중을 그대로 반환한다.
     */
    public int sourceWeight() {

        return hiddenStemWeight;
    }

    public List<String> exposurePositions() {

        java.util.ArrayList<String> result =
                new java.util.ArrayList<>();

        if (
                exposedAtYearStem
        ) {

            result.add(
                    "년간"
            );
        }

        if (
                exposedAtMonthStem
        ) {

            result.add(
                    "월간"
            );
        }

        if (
                exposedAtHourStem
        ) {

            result.add(
                    "시간"
            );
        }

        return List.copyOf(
                result
        );
    }
}