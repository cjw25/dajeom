package com.dajeom.model;

import com.dajeom.type.MonthlyLuckContextAxis;
import com.dajeom.type.MonthlyLuckContextStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 15차-7.
 *
 * 월운 교차분석의 최소 Evidence 단위.
 *
 * =================================================
 *
 * 예:
 *
 * axis
 * = TEN_GOD
 *
 * status
 * = SUPPORTED
 *
 * code
 * = MONTHLY_WEALTH_SUPPORT
 *
 * =================================================
 *
 * 또는:
 *
 * axis
 * = THREE_LAYER_RELATION
 *
 * code
 * = NATAL_ANNUAL_MONTHLY_RELATION
 *
 * =================================================
 *
 * reasons는 이후:
 *
 * 18차 생활영역
 * 19차 고민분석
 * 20차 AI Grounding
 *
 * 에서 그대로 사용할 수 있는
 * deterministic 근거다.
 */
public record MonthlyLuckContextSignal(

        MonthlyLuckContextAxis axis,

        MonthlyLuckContextStatus status,

        String code,

        String summary,

        Set<PalacePosition> palacePositions,

        Set<StructureTenGodType> tenGodTypes,

        List<String> reasons

) {

    public MonthlyLuckContextSignal {

        Objects.requireNonNull(
                axis,
                "월운 Context Signal에는 Axis가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "월운 Context Signal에는 Status가 필요합니다."
        );

        if (
                code == null
                        || code.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운 Context Signal에는 Code가 필요합니다."
            );
        }

        code =
                code.trim();

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운 Context Signal에는 요약이 필요합니다."
            );
        }

        summary =
                summary.trim();

        palacePositions =
                palacePositions == null
                        ? Set.of()
                        : Set.copyOf(
                        palacePositions
                );

        tenGodTypes =
                tenGodTypes == null
                        ? Set.of()
                        : Set.copyOf(
                        tenGodTypes
                );

        reasons =
                reasons == null
                        ? List.of()
                        : reasons
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                reason ->
                                        !reason.isBlank()
                        )
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "월운 Context Signal에는 최소 하나의 근거가 필요합니다."
            );
        }
    }

    public boolean involves(
            PalacePosition position
    ) {

        return position != null
                && palacePositions.contains(
                position
        );
    }

    public boolean involves(
            StructureTenGodType tenGodType
    ) {

        return tenGodType != null
                && tenGodTypes.contains(
                tenGodType
        );
    }

    public boolean supported() {

        return status.supported();
    }

    public boolean qualified() {

        return status.qualified();
    }

    public boolean weakened() {

        return status.weakened();
    }

    public boolean conflicted() {

        return status.conflicted();
    }
}