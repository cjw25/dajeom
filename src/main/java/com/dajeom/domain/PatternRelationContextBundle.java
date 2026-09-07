package com.dajeom.domain;

import java.util.Objects;

/**
 * 일반 8격에서 사용하는 RelationContext 묶음.
 *
 * =================================================
 *
 * 목적:
 *
 * 격별로 흩어진 RelationContext를
 * 하나의 통합 Calculator에서 다룰 수 있게 한다.
 *
 * =================================================
 *
 * 포함:
 *
 * - 正官格
 * - 財格
 * - 印格
 * - 食神格
 * - 傷官格
 * - 七殺格
 *
 * =================================================
 *
 * 이 객체 자체는 명리 판정을 하지 않는다.
 * 관계 Context의 전달 객체다.
 */
public record PatternRelationContextBundle(

        DirectOfficerPatternRelationContext directOfficer,

        WealthPatternRelationContext wealth,

        ResourcePatternRelationContext resource,

        EatingGodPatternRelationContext eatingGod,

        HurtingOfficerPatternRelationContext hurtingOfficer,

        SevenKillingsPatternRelationContext sevenKillings

) {

    public PatternRelationContextBundle {

        Objects.requireNonNull(
                directOfficer,
                "DirectOfficerPatternRelationContext는 필수입니다."
        );

        Objects.requireNonNull(
                wealth,
                "WealthPatternRelationContext는 필수입니다."
        );

        Objects.requireNonNull(
                resource,
                "ResourcePatternRelationContext는 필수입니다."
        );

        Objects.requireNonNull(
                eatingGod,
                "EatingGodPatternRelationContext는 필수입니다."
        );

        Objects.requireNonNull(
                hurtingOfficer,
                "HurtingOfficerPatternRelationContext는 필수입니다."
        );

        Objects.requireNonNull(
                sevenKillings,
                "SevenKillingsPatternRelationContext는 필수입니다."
        );
    }

    /**
     * 아직 실제 관계 Adapter가
     * 아무것도 연결되지 않은 초기 상태.
     */
    public static PatternRelationContextBundle unresolved() {

        return new PatternRelationContextBundle(

                DirectOfficerPatternRelationContext
                        .unresolved(),

                WealthPatternRelationContext
                        .unresolved(),

                ResourcePatternRelationContext
                        .unresolved(),

                EatingGodPatternRelationContext
                        .unresolved(),

                HurtingOfficerPatternRelationContext
                        .unresolved(),

                SevenKillingsPatternRelationContext
                        .unresolved()
        );
    }
}