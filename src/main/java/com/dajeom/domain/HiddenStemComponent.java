package com.dajeom.domain;

import java.util.Objects;

public record HiddenStemComponent(

        HeavenlyStem stem,

        HiddenStemPhase phase

) {

    public HiddenStemComponent {

        Objects.requireNonNull(
                stem,
                "지장간 천간은 필수입니다."
        );

        Objects.requireNonNull(
                phase,
                "지장간 위치는 필수입니다."
        );
    }
}