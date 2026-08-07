package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.PillarPosition;

import java.util.Objects;

/**
 * 특정 지지 안에 포함된 지장간 하나를 나타낸다.
 */
public record HiddenStem(
        PillarPosition branchPosition,
        HeavenlyStem stem,
        HiddenStemRole role
) {

    public HiddenStem {
        Objects.requireNonNull(
                branchPosition,
                "지지 위치는 필수입니다."
        );

        Objects.requireNonNull(
                stem,
                "지장간 천간은 필수입니다."
        );

        Objects.requireNonNull(
                role,
                "지장간 역할은 필수입니다."
        );
    }
}