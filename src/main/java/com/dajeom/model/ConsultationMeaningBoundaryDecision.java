package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * 하나의 Meaning Claim을 생성하기 직전
 * Java가 최종 확정한 표현 경계 결정.
 *
 * =================================================
 * requested
 * =================================================
 *
 * 상위 deterministic 계산 또는
 * ExactTenGod / FormalPattern 계산에서 요청한:
 *
 * - strongExpressionAllowed
 * - qualificationRequired
 *
 * =================================================
 * effective
 * =================================================
 *
 * Origin 정책까지 적용한 뒤
 * 실제 Claim에 들어갈 최종 Expression Mode.
 *
 * =================================================
 * 예
 * =================================================
 *
 * NATAL_CONTEXT
 * requestedStrong = true
 *
 * ->
 *
 * effectiveExpressionMode = DIRECT
 * 또는 QUALIFIED
 *
 * 즉 강한 표현 권한을 제거한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 객체는 새로운 명리 Meaning을 만들지 않는다.
 *
 * 이미 확정된 Meaning의:
 *
 * - 출처
 * - 구체성
 * - 표현 강도
 * - 제한 표현 필요 여부
 *
 * 만 보존한다.
 */
public record ConsultationMeaningBoundaryDecision(

        ConsultationMeaningOriginContract originContract,

        ConsultationMeaningSpecificity specificity,

        boolean requestedStrongExpressionAllowed,

        boolean requestedQualificationRequired,

        ConsultationMeaningExpressionMode effectiveExpressionMode

) {

    public ConsultationMeaningBoundaryDecision {

        Objects.requireNonNull(
                originContract,
                "Meaning Boundary Decision originContract는 필수입니다."
        );

        Objects.requireNonNull(
                specificity,
                "Meaning Boundary Decision specificity는 필수입니다."
        );

        Objects.requireNonNull(
                effectiveExpressionMode,
                "Meaning Boundary Decision effectiveExpressionMode는 필수입니다."
        );

        /*
         * =================================================
         * Origin ↔ Specificity
         * =================================================
         */

        originContract.requireSpecificity(
                specificity
        );

        /*
         * =================================================
         * 최종 Expression Boundary 자체 검증
         * =================================================
         *
         * 여기서 NATAL_CONTEXT strong 금지 같은
         * 공식 제약도 다시 검증된다.
         */

        new ConsultationMeaningExpressionBoundaryContract(
                originContract.origin(),
                specificity,
                effectiveExpressionMode
        );
    }

    public ConsultationMeaningOrigin origin() {

        return originContract
                .origin();
    }

    public ConsultationMeaningAuthority authority() {

        return originContract
                .authority();
    }

    public boolean strongExpressionAllowed() {

        return effectiveExpressionMode
                .strongExpressionAllowed();
    }

    public boolean qualificationRequired() {

        return effectiveExpressionMode
                .qualificationRequired();
    }

    /**
     * 상위 계산에서는 strong을 요청했지만
     * Origin 정책 때문에 최종적으로 제거되었는지.
     *
     * 대표적으로 NATAL_CONTEXT에서 사용한다.
     */
    public boolean strongExpressionDowngraded() {

        return requestedStrongExpressionAllowed
                &&
                !strongExpressionAllowed();
    }

    /**
     * qualification은 현재 임의 제거하지 않는다.
     *
     * 따라서 이 값이 true가 된다면
     * 향후 정책 변경으로 제한표현이 추가된 것이다.
     */
    public boolean qualificationAdded() {

        return !requestedQualificationRequired
                &&
                qualificationRequired();
    }

    public ConsultationMeaningExpressionBoundaryContract
    expressionBoundaryContract() {

        return new ConsultationMeaningExpressionBoundaryContract(
                origin(),
                specificity,
                effectiveExpressionMode
        );
    }
}