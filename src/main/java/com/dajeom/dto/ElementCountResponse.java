package com.dajeom.dto;

import com.dajeom.domain.ElementCount;

import java.util.Objects;

/**
 * 사용자 공개용 오행 분포.
 *
 * 내부 ElementCount의 total은 계산 검증용으로 유지하되
 * 공개 API에는 목/화/토/금/수만 노출한다.
 */
public record ElementCountResponse(
        int wood,
        int fire,
        int earth,
        int metal,
        int water
) {

    public static ElementCountResponse from(
            ElementCount count
    ) {

        Objects.requireNonNull(
                count,
                "오행 개수 결과가 필요합니다."
        );

        return new ElementCountResponse(
                count.wood(),
                count.fire(),
                count.earth(),
                count.metal(),
                count.water()
        );
    }
}
