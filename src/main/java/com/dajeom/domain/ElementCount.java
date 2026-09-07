package com.dajeom.domain;

/**
 * 원국에 드러난 천간 + 지지의 오행 개수.
 *
 * 지장간은 포함하지 않는다.
 *
 * 출생시간을 아는 경우:
 * 4주 × 2글자 = 총 8
 *
 * 출생시간을 모르는 경우:
 * 3주 × 2글자 = 총 6
 */
public record ElementCount(
        int wood,
        int fire,
        int earth,
        int metal,
        int water
) {

    public ElementCount {

        if (wood < 0
                || fire < 0
                || earth < 0
                || metal < 0
                || water < 0) {

            throw new IllegalArgumentException(
                    "오행 개수는 음수가 될 수 없습니다."
            );
        }
    }

    public int get(
            FiveElement element
    ) {

        if (element == null) {
            throw new IllegalArgumentException(
                    "오행이 필요합니다."
            );
        }

        return switch (element) {

            case WOOD -> wood;
            case FIRE -> fire;
            case EARTH -> earth;
            case METAL -> metal;
            case WATER -> water;
        };
    }

    public int total() {

        return wood
                + fire
                + earth
                + metal
                + water;
    }

    public boolean has(
            FiveElement element
    ) {

        return get(element) > 0;
    }

    public boolean isMissing(
            FiveElement element
    ) {

        return get(element) == 0;
    }
}