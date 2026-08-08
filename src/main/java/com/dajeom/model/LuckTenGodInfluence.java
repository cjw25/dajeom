package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGod;

import java.util.Objects;

/**
 * 대운 또는 세운의 십성 영향 데이터.
 *
 * 천간의 십성
 * +
 * 지지 정기의 십성
 *
 * 을 함께 보관한다.
 */
public record LuckTenGodInfluence(

        LuckPillarSource source,

        TenGod stemTenGod,

        HeavenlyStem branchMainStem,

        TenGod branchMainTenGod

) {

    public LuckTenGodInfluence {

        Objects.requireNonNull(
                source,
                "운 출처는 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                branchMainStem,
                "지지 정기는 필수입니다."
        );

        Objects.requireNonNull(
                branchMainTenGod,
                "지지 정기 십성은 필수입니다."
        );

        if (!source.isExternalLuck()) {

            throw new IllegalArgumentException(
                    "운 십성 영향은 대운 또는 세운만 사용할 수 있습니다."
            );
        }
    }
}