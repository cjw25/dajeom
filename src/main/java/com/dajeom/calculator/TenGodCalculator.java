package com.dajeom.calculator;

import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.TenGodAssignment;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 일간을 기준으로 원국 천간과 지장간의 십성을 계산한다.
 */
@Component
public class TenGodCalculator {

    public List<TenGodAssignment> calculate(
            FourPillars fourPillars,
            List<HiddenStem> hiddenStems
    ) {
        HeavenlyStem dayMaster =
                fourPillars.dayPillar().stem();

        List<TenGodAssignment> result =
                new ArrayList<>();

        addVisibleStem(
                result,
                dayMaster,
                PillarPosition.YEAR_STEM,
                fourPillars.yearPillar().stem()
        );

        addVisibleStem(
                result,
                dayMaster,
                PillarPosition.MONTH_STEM,
                fourPillars.monthPillar().stem()
        );

        addVisibleStem(
                result,
                dayMaster,
                PillarPosition.DAY_STEM,
                fourPillars.dayPillar().stem()
        );

        addVisibleStem(
                result,
                dayMaster,
                PillarPosition.HOUR_STEM,
                fourPillars.hourPillar().stem()
        );

        for (HiddenStem hiddenStem : hiddenStems) {
            TenGod tenGod =
                    calculateTenGod(
                            dayMaster,
                            hiddenStem.stem()
                    );

            result.add(
                    TenGodAssignment.hidden(
                            hiddenStem.branchPosition(),
                            hiddenStem.stem(),
                            tenGod,
                            hiddenStem.role()
                    )
            );
        }

        return List.copyOf(result);
    }

    public TenGod calculateTenGod(
            HeavenlyStem dayMaster,
            HeavenlyStem targetStem
    ) {
        FiveElement dayMasterElement =
                dayMaster.getElement();

        FiveElement targetElement =
                targetStem.getElement();

        boolean samePolarity =
                dayMaster.getYinYang()
                        == targetStem.getYinYang();

        /*
         * 같은 오행:
         * 같은 음양은 비견
         * 다른 음양은 겁재
         */
        if (dayMasterElement == targetElement) {
            return samePolarity
                    ? TenGod.FRIEND
                    : TenGod.ROB_WEALTH;
        }

        /*
         * 일간이 대상 오행을 생함:
         * 같은 음양은 식신
         * 다른 음양은 상관
         */
        if (dayMasterElement.generates(targetElement)) {
            return samePolarity
                    ? TenGod.EATING_GOD
                    : TenGod.HURTING_OFFICER;
        }

        /*
         * 일간이 대상 오행을 극함:
         * 같은 음양은 편재
         * 다른 음양은 정재
         */
        if (dayMasterElement.controls(targetElement)) {
            return samePolarity
                    ? TenGod.INDIRECT_WEALTH
                    : TenGod.DIRECT_WEALTH;
        }

        /*
         * 대상 오행이 일간을 극함:
         * 같은 음양은 편관
         * 다른 음양은 정관
         */
        if (targetElement.controls(dayMasterElement)) {
            return samePolarity
                    ? TenGod.SEVEN_KILLINGS
                    : TenGod.DIRECT_OFFICER;
        }

        /*
         * 대상 오행이 일간을 생함:
         * 같은 음양은 편인
         * 다른 음양은 정인
         */
        if (targetElement.generates(dayMasterElement)) {
            return samePolarity
                    ? TenGod.INDIRECT_RESOURCE
                    : TenGod.DIRECT_RESOURCE;
        }

        throw new IllegalStateException(
                "십성을 계산할 수 없는 오행 관계입니다."
        );
    }

    private void addVisibleStem(
            List<TenGodAssignment> destination,
            HeavenlyStem dayMaster,
            PillarPosition position,
            HeavenlyStem targetStem
    ) {
        TenGod tenGod =
                calculateTenGod(
                        dayMaster,
                        targetStem
                );

        destination.add(
                TenGodAssignment.visible(
                        position,
                        targetStem,
                        tenGod
                )
        );
    }
}