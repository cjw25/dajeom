package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import com.dajeom.type.PalaceTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 12차-3.
 *
 * 궁위에 배치된 하나의 십성 Evidence.
 *
 * =================================================
 *
 * source:
 *
 * 천간
 * 또는
 * 지장간 본기/중기/여기
 *
 * =================================================
 *
 * 십성의 실제 세력 점수는 여기서 새로 만들지 않는다.
 *
 * 10차 TenGodCompositeAnalysis가
 * 전체 십성 세력의 canonical source다.
 *
 * 12차의 책임은:
 *
 * "어느 궁위에서
 * 어떤 Source로
 * 어떤 십성이 나타나는가"
 *
 * 를 보존하는 것이다.
 */
public record PalaceTenGodEvidence(

        HeavenlyStem dayMaster,

        PalacePosition position,

        PalacePositionContextType positionContext,

        PalaceTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public PalaceTenGodEvidence {

        Objects.requireNonNull(
                dayMaster,
                "궁위 십성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "궁위 십성 Evidence에는 궁위 위치가 필요합니다."
        );

        Objects.requireNonNull(
                positionContext,
                "궁위 십성 Evidence에는 위치 Context가 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "궁위 십성 Evidence에는 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "궁위 십성 Evidence에는 Source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "궁위 십성 Evidence에는 십성 종류가 필요합니다."
        );
    }

    public boolean yearPosition() {

        return position == PalacePosition.YEAR;
    }

    public boolean monthPosition() {

        return position == PalacePosition.MONTH;
    }

    public boolean dayPosition() {

        return position == PalacePosition.DAY;
    }

    public boolean hourPosition() {

        return position == PalacePosition.HOUR;
    }

    public boolean visible() {

        return sourceType
                .visibleStem();
    }

    public boolean hidden() {

        return sourceType
                .hiddenStem();
    }

    public boolean mainHiddenStem() {

        return sourceType
                .mainHiddenStem();
    }

    public boolean middleHiddenStem() {

        return sourceType
                .middleHiddenStem();
    }

    public boolean residualHiddenStem() {

        return sourceType
                .residualHiddenStem();
    }

    public boolean is(
            StructureTenGodType expected
    ) {

        return expected != null
                && tenGodType == expected;
    }
}