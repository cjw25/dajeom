package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 월령 안의 한 지장간과 일간의 관계.
 *
 * 예:
 *
 * 丁 일간 / 申월
 *
 * EARLY  戊 → 丁이 생함
 * MIDDLE 壬 → 壬이 丁을 극함
 * MAIN   庚 → 丁이 庚을 극함
 *
 * 또한 해당 월령 지장간이
 * 천간 어디에 투출되어 있는지도 함께 보존한다.
 *
 * 이 객체 자체는
 * 득령/실령, 신강/신약을 판단하지 않는다.
 */
public record MonthCommandStemRelation(

        HeavenlyStem dayMaster,

        HeavenlyStem hiddenStem,

        HiddenStemPhase phase,

        DayMasterRelationType relationType,

        List<HiddenStemExposureHit> exposureHits

) {

    public MonthCommandStemRelation {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "월령 지장간은 필수입니다."
        );

        Objects.requireNonNull(
                phase,
                "월령 지장간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "일간과 월령 지장간의 관계는 필수입니다."
        );

        exposureHits =
                exposureHits == null
                        ? List.of()
                        : List.copyOf(
                        exposureHits
                );

        for (
                HiddenStemExposureHit hit
                : exposureHits
        ) {

            Objects.requireNonNull(
                    hit,
                    "투출 목록에 null이 포함될 수 없습니다."
            );

            if (
                    hit.hiddenStem()
                            != hiddenStem
            ) {

                throw new IllegalArgumentException(
                        "투출된 천간과 월령 지장간이 일치하지 않습니다."
                );
            }

            if (
                    hit.hiddenStemPhase()
                            != phase
            ) {

                throw new IllegalArgumentException(
                        "투출 정보의 지장간 위치가 일치하지 않습니다."
                );
            }

            if (!hit.fromMonthCommand()) {

                throw new IllegalArgumentException(
                        "월령 관계에는 월지에서 발생한 투출 정보만 포함할 수 있습니다."
                );
            }
        }
    }

    public boolean exposed() {

        return !exposureHits.isEmpty();
    }

    public boolean main() {

        return phase
                == HiddenStemPhase.MAIN;
    }

    public boolean middle() {

        return phase
                == HiddenStemPhase.MIDDLE;
    }

    public boolean early() {

        return phase
                == HiddenStemPhase.EARLY;
    }
}