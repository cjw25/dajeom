package com.dajeom.domain;

import java.util.Objects;

/**
 * 지장간 하나.
 *
 * 지장간은 지지 내부에 들어 있는 천간이다.
 *
 * 위치:
 * EARLY  = 여기
 * MIDDLE = 중기
 * MAIN   = 정기
 *
 * 일부 지지는 중기가 없을 수 있다.
 */
public record HiddenStem(
        Position position,
        HeavenlyStem stem
) {

    public HiddenStem {

        Objects.requireNonNull(
                position,
                "지장간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "지장간 천간이 필요합니다."
        );
    }

    public String hanja() {
        return stem.hanja();
    }

    public String korean() {
        return stem.korean();
    }

    public enum Position {

        EARLY(
                "여기"
        ),

        MIDDLE(
                "중기"
        ),

        MAIN(
                "정기"
        );

        private final String korean;

        Position(
                String korean
        ) {
            this.korean = korean;
        }

        public String korean() {
            return korean;
        }
    }
}