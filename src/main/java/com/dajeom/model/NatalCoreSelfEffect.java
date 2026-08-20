package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 원국 Functional Analysis의
 * 자기 기반축.
 *
 * =================================================
 *
 * 사용 Source:
 *
 * 4차
 * DayMasterStrengthAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 월령을 다시 더하지 않는다.
 * - 통근을 다시 더하지 않는다.
 * - 득령·득지·득세를 다시 점수화하지 않는다.
 * - 오행 세력을 다시 더하지 않는다.
 *
 * 이미 4차에서 확정된:
 *
 * - 지원력
 * - 압력
 * - 구조 보정
 * - 최종 신강·신약
 *
 * 을 그대로 사용한다.
 *
 * 즉 중복가산 방지용
 * Functional Evidence다.
 */
public record NatalCoreSelfEffect(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        DayMasterStrengthLevel strengthLevel,

        int strengthBasisPoints,

        int supportPower,

        int pressurePower,

        boolean directlyGainsCommand,

        boolean seasonallySupported,

        boolean grounded,

        boolean gainsForce,

        boolean gainsEstablishedForce,

        boolean extreme,

        boolean followStructureReviewRequired,

        boolean completeChart

) {

    public NatalCoreSelfEffect {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "신강·신약 단계는 필수입니다."
        );

        if (
                strengthBasisPoints < 0
                        || strengthBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 내부 강도 지수는 "
                            + "0~10000bp 범위여야 합니다."
            );
        }

        if (
                supportPower < 0
                        || pressurePower < 0
        ) {

            throw new IllegalArgumentException(
                    "지원력과 압력은 음수일 수 없습니다."
            );
        }
    }

    public boolean strongSide() {

        return strengthLevel
                .strongSide();
    }

    public boolean weakSide() {

        return strengthLevel
                .weakSide();
    }

    public boolean balancedZone() {

        return strengthLevel
                .balancedZone();
    }
}