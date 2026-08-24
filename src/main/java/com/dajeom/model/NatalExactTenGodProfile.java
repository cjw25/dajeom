package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodPowerLevel;

import java.util.Objects;

/**
 * 정확한 10십성 하나의 원국 상태를 보존하는 Profile.
 *
 * =================================================
 *
 * 기존 5개 Family:
 *
 * 비겁
 * 식상
 * 재성
 * 관살
 * 인성
 *
 * 으로 먼저 합치지 않는다.
 *
 * 다음 정확한 10십성을
 * 각각 독립적으로 유지한다.
 *
 * - 비견
 * - 겁재
 * - 식신
 * - 상관
 * - 편재
 * - 정재
 * - 편관
 * - 정관
 * - 편인
 * - 정인
 *
 * =================================================
 *
 * sourceSummary
 *
 * 새로운 십성 세력을 계산하지 않는다.
 *
 * 기존 정밀화 10-1
 * TenGodDistributionAnalysis에서 이미 확정된
 * TenGodPowerSummary를 그대로 참조한다.
 *
 * =================================================
 *
 * primaryStructure
 *
 * 5차 격국에서
 * 해당 정확한 십성이 실제 주격 중심인 경우만 true.
 *
 * 격국을 세력에 다시 더하지 않는다.
 */
public record NatalExactTenGodProfile(

        StructureTenGodType tenGodType,

        TenGodPowerSummary sourceSummary,

        boolean primaryStructure,

        boolean completeChart

) {

    public NatalExactTenGodProfile {

        Objects.requireNonNull(
                tenGodType,
                "정확한 십성 종류는 필수입니다."
        );

        Objects.requireNonNull(
                sourceSummary,
                "정확한 십성 Profile에는 10-1 TenGodPowerSummary가 필요합니다."
        );

        if (
                sourceSummary.tenGodType()
                        != tenGodType
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Profile과 Source Summary의 십성 종류가 일치하지 않습니다."
            );
        }

        if (
                primaryStructure
                        && !sourceSummary.present()
        ) {

            throw new IllegalArgumentException(
                    "주격 중심 십성은 원국에 실제로 존재해야 합니다: "
                            + tenGodType
            );
        }
    }

    public String koreanName() {

        return tenGodType
                .koreanName();
    }

    public TenGodPowerLevel powerLevel() {

        return sourceSummary
                .powerLevel();
    }

    public int occurrenceCount() {

        return sourceSummary
                .occurrenceCount();
    }

    public int allocatedPower() {

        return sourceSummary
                .allocatedPower();
    }

    public int shareBasisPoints() {

        return sourceSummary
                .shareBasisPoints();
    }

    public double sharePercent() {

        return sourceSummary
                .sharePercent();
    }

    public boolean present() {

        return sourceSummary
                .present();
    }

    public boolean visible() {

        return sourceSummary
                .visible();
    }

    public boolean hiddenOnly() {

        return sourceSummary
                .hiddenOnly();
    }

    public boolean monthPresent() {

        return sourceSummary
                .monthPresent();
    }

    public boolean monthBranchPresent() {

        return sourceSummary
                .monthBranchPresent();
    }

    public boolean dominant() {

        return sourceSummary
                .dominant();
    }

    public boolean strongSide() {

        return sourceSummary
                .strongSide();
    }

    public boolean meaningful() {

        return sourceSummary
                .meaningful();
    }
}