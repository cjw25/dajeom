package com.dajeom.model;

import com.dajeom.type.TenGodCrossValidationFactorType;
import com.dajeom.type.TenGodCrossValidationReasonType;
import com.dajeom.type.TenGodCrossValidationStatus;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-9.
 *
 * 하나의 교차검증 축에 대한 Evidence.
 *
 * 예:
 *
 * factorType = DAY_MASTER_STRENGTH
 * status     = QUALIFIED
 *
 * 또는
 *
 * factorType = TRANSFORMATION
 * status     = WEAKENED
 */
public record TenGodCrossValidationEvidence(

        TenGodCrossValidationFactorType factorType,

        TenGodCrossValidationStatus status,

        List<TenGodCrossValidationReasonType> reasons

) {

    public TenGodCrossValidationEvidence {

        Objects.requireNonNull(
                factorType,
                "교차검증 Factor 종류가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "교차검증 Factor 상태가 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "교차검증 Evidence에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    public boolean supported() {

        return status
                .supported();
    }

    public boolean qualified() {

        return status
                .qualified();
    }

    public boolean weakened() {

        return status
                .weakened();
    }

    public boolean conflicted() {

        return status
                .conflicted();
    }

    public boolean hasReason(
            TenGodCrossValidationReasonType reason
    ) {

        return reason != null
                && reasons.contains(
                reason
        );
    }
}