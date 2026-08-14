package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 13차-6.
 *
 * 하나의 대운에서 발견된
 * 교차분석 근거 Signal.
 *
 * =================================================
 *
 * 예:
 *
 * axis
 * = USEFUL_GOD
 *
 * status
 * = SUPPORTED
 *
 * code
 * = PRIMARY_USEFUL_ELEMENT_ENTERED
 *
 * =================================================
 *
 * 또는:
 *
 * axis
 * = RELATION
 *
 * status
 * = CONFLICTED
 *
 * palacePositions
 * = [DAY]
 *
 * =================================================
 *
 * reasons는 AI Grounding까지 전달할 수 있는
 * deterministic 근거다.
 *
 * 길흉 문장을 직접 생성하지 않는다.
 */
public record DecadeLuckContextSignal(

        DecadeLuckContextAxis axis,

        DecadeLuckContextStatus status,

        String code,

        String summary,

        Set<PalacePosition> palacePositions,

        Set<StructureTenGodType> tenGodTypes,

        List<String> reasons

) {

    public DecadeLuckContextSignal {

        Objects.requireNonNull(
                axis,
                "대운 Context Signal에는 Axis가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "대운 Context Signal에는 Status가 필요합니다."
        );

        if (
                code == null
                        || code.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운 Context Signal에는 Code가 필요합니다."
            );
        }

        code =
                code.trim();

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운 Context Signal에는 요약이 필요합니다."
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
                    "대운 Context Signal에는 최소 하나의 근거가 필요합니다."
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