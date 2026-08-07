package com.dajeom.model;

import com.dajeom.type.TenGod;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 10년 단위의 대운 하나를 나타낸다.
 */
public record DecadeLuck(
        int sequence,
        Pillar pillar,
        LocalDate startDate,
        LocalDate endDate,
        int startAge,
        int endAge,
        TenGod stemTenGod,
        TenGod branchMainTenGod,
        boolean current
) {

    public DecadeLuck {
        if (sequence < 1) {
            throw new IllegalArgumentException(
                    "대운 순번은 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                pillar,
                "대운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDate,
                "대운 시작일은 필수입니다."
        );

        Objects.requireNonNull(
                endDate,
                "대운 종료일은 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "대운 천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                branchMainTenGod,
                "대운 지지 본기 십성은 필수입니다."
        );
    }
}