package com.dajeom.domain;

import java.util.Objects;

/**
 * 官殺 정리 규칙에서 사용하는 개별 官/殺 노드 식별자.
 *
 * =================================================
 *
 * 왜 String 하나를 직접 사용하지 않고
 * 별도 record로 감싸는가?
 *
 * 官殺混雜과 羊刃合殺은 이제
 *
 * "七殺이 있다"
 *
 * 수준이 아니라
 *
 * "어느 七殺 노드가 실제로 처리되었는가"
 *
 * 를 추적해야 하기 때문이다.
 *
 * =================================================
 *
 * nodeKey 예:
 *
 * YEAR_STEM-SEVEN_KILLINGS
 * MONTH_STEM-SEVEN_KILLINGS
 * HOUR_STEM-SEVEN_KILLINGS
 *
 * 실제 Adapter에서는 기존 PatternTenGodNode의
 * 안정적인 식별값을 넣으면 된다.
 *
 * =================================================
 *
 * 이 객체는 사용자에게 노출하지 않는다.
 */
public record OfficerKillingsNodeRef(
        String nodeKey
) {

    public OfficerKillingsNodeRef {

        Objects.requireNonNull(
                nodeKey,
                "官殺 nodeKey는 필수입니다."
        );

        nodeKey =
                nodeKey.trim();

        if (nodeKey.isEmpty()) {

            throw new IllegalArgumentException(
                    "官殺 nodeKey는 비어 있을 수 없습니다."
            );
        }
    }
}