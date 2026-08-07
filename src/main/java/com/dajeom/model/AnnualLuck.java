package com.dajeom.model;

import com.dajeom.type.TenGod;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 특정 연도의 세운을 나타낸다.
 *
 * 세운은 해당 연도 입춘부터 다음 연도 입춘 전까지 작용한다.
 */
public record AnnualLuck(
        int targetYear,
        Pillar pillar,
        ZonedDateTime startDateTime,
        ZonedDateTime endDateTime,
        TenGod stemTenGod,
        TenGod branchMainTenGod
) {

    public AnnualLuck {
        Objects.requireNonNull(
                pillar,
                "세운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTime,
                "세운 시작 시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTime,
                "세운 종료 시각은 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "세운 천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                branchMainTenGod,
                "세운 지지 본기 십성은 필수입니다."
        );
    }
}