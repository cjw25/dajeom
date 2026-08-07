package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 합충형파해가 일간 강약에 미친 보수적 조정 결과다.
 *
 * 4차에서는 충·형·파·해가 실제 통근 위치를 건드리는 경우만
 * 제한적으로 감점한다.
 */
public record RelationImpactResult(
        double scoreChange,
        List<StrengthEvidence> evidence
) {

    public RelationImpactResult {
        Objects.requireNonNull(
                evidence,
                "관계 영향 근거는 필수입니다."
        );

        evidence = List.copyOf(evidence);
    }

    public static RelationImpactResult empty() {
        return new RelationImpactResult(
                0.0,
                List.of()
        );
    }
}