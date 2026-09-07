package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

public record FourPillars(
        Pillar year,
        Pillar month,
        Pillar day,
        Pillar hour
) {

    public FourPillars {

        Objects.requireNonNull(
                year,
                "년주가 필요합니다."
        );

        Objects.requireNonNull(
                month,
                "월주가 필요합니다."
        );

        Objects.requireNonNull(
                day,
                "일주가 필요합니다."
        );

        /*
         * 출생시간을 모르는 사용자가 존재하므로
         * hour는 null을 허용합니다.
         *
         * 임의의 시주를 생성하지 않습니다.
         */
    }

    public boolean hasKnownHour() {
        return hour != null;
    }

    public boolean hasUnknownHour() {
        return hour == null;
    }

    public Optional<Pillar> optionalHour() {
        return Optional.ofNullable(
                hour
        );
    }

    public HeavenlyStem dayStem() {
        return day.stem();
    }

    public EarthlyBranch dayBranch() {
        return day.branch();
    }

    public String dayMasterHanja() {
        return day.stem().hanja();
    }

    public String dayMasterKorean() {
        return day.stem().korean();
    }
}