package com.dajeom.rule;

import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.type.ClimateHealthStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-10.
 *
 * 조후·병약·통관 전체 파이프라인의
 * 최종 상태를 결정한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 새로운 오행 후보를 만들지 않는다.
 *
 * 2.
 * 후보를 승격하거나 강등하지 않는다.
 *
 * 후보 역할 결정은 이미:
 *
 * 6-8
 * 6-9
 *
 * 에서 완료됐다.
 *
 * 3.
 * 여기서는 6-9 결과를 기준으로
 * 전체 6차의 상태만 분류한다.
 *
 * 4.
 * PRIMARY가 여러 개라고
 * 임의로 하나를 선택하지 않는다.
 *
 * 5.
 * 이 Rule은 용신 결정 Rule이 아니다.
 *
 * 최종 용신·희신·기신은
 * 정밀화 7차에서 결정한다.
 */
@Component
public class ClimateHealthRule {

    public ClimateHealthStatus determineStatus(
            ClimateHealthCrossValidationAnalysis validation
    ) {

        Objects.requireNonNull(
                validation,
                "6-9 교차검증 분석이 필요합니다."
        );

        int primaryCount =
                validation
                        .primaryCandidates()
                        .size();

        int secondaryCount =
                validation
                        .secondaryCandidates()
                        .size();

        int deferredCount =
                validation
                        .deferredCandidates()
                        .size();

        /*
         * ================================================
         * 단일 PRIMARY
         * ================================================
         */
        if (
                primaryCount == 1
        ) {

            return ClimateHealthStatus
                    .SINGLE_PRIMARY;
        }

        /*
         * ================================================
         * 복수 PRIMARY
         * ================================================
         *
         * 6차에서는 억지로 하나를 고르지 않는다.
         *
         * 7차에서:
         *
         * - 용신법 우선도
         * - 조후
         * - 억부
         * - 병약
         * - 통관
         * - 격국
         *
         * 등을 다시 조정한다.
         */
        if (
                primaryCount >= 2
        ) {

            return ClimateHealthStatus
                    .MULTIPLE_PRIMARY;
        }

        /*
         * ================================================
         * PRIMARY는 없지만 SECONDARY 존재
         * ================================================
         */
        if (
                secondaryCount > 0
        ) {

            return ClimateHealthStatus
                    .SECONDARY_ONLY;
        }

        /*
         * ================================================
         * 선택 후보 없이 DEFERRED만 존재
         * ================================================
         */
        if (
                deferredCount > 0
        ) {

            return ClimateHealthStatus
                    .DEFERRED_ONLY;
        }

        /*
         * ================================================
         * 전부 REJECTED
         * ================================================
         */
        return ClimateHealthStatus
                .INACTIVE;
    }

    /**
     * 6차 단계에서 단일 핵심 후보가
     * 존재하는지 확인한다.
     *
     * 이것이 곧 용신 확정을 뜻하지 않는다.
     */
    public boolean hasSinglePrimary(
            ClimateHealthCrossValidationAnalysis validation
    ) {

        Objects.requireNonNull(
                validation,
                "6-9 교차검증 분석이 필요합니다."
        );

        return validation
                .primaryCandidates()
                .size()
                == 1;
    }

    /**
     * 7차에서 후보 간 추가 비교가
     * 반드시 필요한 상태인가.
     */
    public boolean requiresFurtherSelection(
            ClimateHealthCrossValidationAnalysis validation
    ) {

        ClimateHealthStatus status =
                determineStatus(
                        validation
                );

        return status
                .requiresFurtherSelection();
    }
}