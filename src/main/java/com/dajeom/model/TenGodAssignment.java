package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TenGod;

import java.util.Objects;

/**
 * 원국의 특정 천간 또는 지장간에 배정된 십성을 나타낸다.
 *
 * visibleStem이 true이면 사주 표면에 드러난 천간이고,
 * false이면 지지 안에 들어 있는 지장간이다.
 */
public record TenGodAssignment(
        PillarPosition sourcePosition,
        HeavenlyStem targetStem,
        TenGod tenGod,
        boolean visibleStem,
        HiddenStemRole hiddenStemRole
) {

    public TenGodAssignment {
        Objects.requireNonNull(
                sourcePosition,
                "십성의 출처 위치는 필수입니다."
        );

        Objects.requireNonNull(
                targetStem,
                "십성 대상 천간은 필수입니다."
        );

        Objects.requireNonNull(
                tenGod,
                "십성은 필수입니다."
        );

        if (!visibleStem && hiddenStemRole == null) {
            throw new IllegalArgumentException(
                    "지장간 십성에는 지장간 역할이 필요합니다."
            );
        }

        if (visibleStem && hiddenStemRole != null) {
            throw new IllegalArgumentException(
                    "표면 천간에는 지장간 역할을 지정할 수 없습니다."
            );
        }
    }

    public static TenGodAssignment visible(
            PillarPosition position,
            HeavenlyStem targetStem,
            TenGod tenGod
    ) {
        return new TenGodAssignment(
                position,
                targetStem,
                tenGod,
                true,
                null
        );
    }

    public static TenGodAssignment hidden(
            PillarPosition position,
            HeavenlyStem targetStem,
            TenGod tenGod,
            HiddenStemRole role
    ) {
        return new TenGodAssignment(
                position,
                targetStem,
                tenGod,
                false,
                role
        );
    }
}