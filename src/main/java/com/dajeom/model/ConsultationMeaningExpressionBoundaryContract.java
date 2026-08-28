package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * Meaning Claim의:
 *
 * - Origin
 * - Specificity
 * - Expression Mode
 *
 * 를 하나의 공식 사용자 표현 계약으로 묶는다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존에는:
 *
 * strongExpressionAllowed
 * qualificationRequired
 *
 * 가 각각 boolean으로 존재했다.
 *
 * 따라서 향후 코드가 이 둘을
 * 서로 독립된 값처럼 잘못 취급할 수 있었다.
 *
 * 현재는:
 *
 * DIRECT
 * QUALIFIED
 * STRONG_DIRECT
 * STRONG_QUALIFIED
 *
 * 중 정확히 하나의 Mode로 고정한다.
 *
 * =================================================
 * Origin 경계
 * =================================================
 *
 * NATAL_CONTEXT는:
 *
 * - CoreSelf 배경
 * - 기능축 배경
 * - interpersonal context
 *
 * 와 같은 보조 원국 Context다.
 *
 * 따라서 독립적인 강한 확정판정 권한을
 * 가질 수 없다.
 *
 * NATAL_CONTEXT + STRONG_*
 *
 * 조합은 Java에서 즉시 금지한다.
 *
 * =================================================
 * Exact TenGod
 * =================================================
 *
 * EXACT_TEN_GOD는
 * 정확한 10십성 공식 계산에서
 * 실제 PRIMARY / 구조적 강함이 확인된 경우
 * strongExpressionAllowed=true가 올 수 있다.
 *
 * Supporting / weak exact ten god는
 * 상위 ExactTenGod Meaning Engine에서
 * strongExpressionAllowed=false로 내려오므로
 * 이 Contract가 임의로 강도를 새로 계산하지 않는다.
 *
 * =================================================
 * STRONG_QUALIFIED
 * =================================================
 *
 * strong=true
 * qualification=true
 *
 * 는 모순이 아니다.
 *
 * 근거 자체는 강하지만
 * 완전성 제한이나 조건이 존재할 수 있으므로
 * 공식적으로 허용한다.
 */
public record ConsultationMeaningExpressionBoundaryContract(

        ConsultationMeaningOrigin origin,

        ConsultationMeaningSpecificity specificity,

        ConsultationMeaningExpressionMode expressionMode

) {

    public ConsultationMeaningExpressionBoundaryContract {

        Objects.requireNonNull(
                origin,
                "Expression Boundary Contract origin은 필수입니다."
        );

        Objects.requireNonNull(
                specificity,
                "Expression Boundary Contract specificity는 필수입니다."
        );

        Objects.requireNonNull(
                expressionMode,
                "Expression Boundary Contract expressionMode는 필수입니다."
        );

        /*
         * =================================================
         * Origin ↔ Specificity
         * =================================================
         */

        ConsultationMeaningSpecificityContract
                .forOrigin(
                        origin
                )
                .requireAllowed(
                        specificity
                );

        /*
         * =================================================
         * NATAL_CONTEXT 강한 확정 권한 금지
         * =================================================
         *
         * NATAL_CONTEXT는
         * 독립 사용자 Meaning의 상위 Authority가 아니다.
         */

        if (
                origin
                        == ConsultationMeaningOrigin.NATAL_CONTEXT

                        &&
                        expressionMode.strong()
        ) {

            throw new IllegalArgumentException(
                    "NATAL_CONTEXT는 강한 확정 표현 권한을 가질 수 없습니다. "
                            + "specificity="
                            + specificity
                            + ", expressionMode="
                            + expressionMode
            );
        }
    }

    /**
     * 기존 boolean 경계에서
     * 공식 Expression Boundary Contract 생성.
     */
    public static ConsultationMeaningExpressionBoundaryContract of(

            ConsultationMeaningOrigin origin,

            ConsultationMeaningSpecificity specificity,

            boolean strongExpressionAllowed,

            boolean qualificationRequired

    ) {

        return new ConsultationMeaningExpressionBoundaryContract(

                origin,

                specificity,

                ConsultationMeaningExpressionMode.of(

                        strongExpressionAllowed,

                        qualificationRequired
                )
        );
    }

    /**
     * 공식 Meaning Claim에서
     * Expression Boundary Contract 재구성.
     */
    public static ConsultationMeaningExpressionBoundaryContract from(

            ConsultationMeaningClaim claim

    ) {

        Objects.requireNonNull(
                claim,
                "Expression Boundary Contract 생성에는 Meaning Claim이 필요합니다."
        );

        return of(

                claim.origin(),

                claim.specificity(),

                claim.strongExpressionAllowed(),

                claim.qualificationRequired()
        );
    }

    public boolean strongExpressionAllowed() {

        return expressionMode
                .strongExpressionAllowed();
    }

    public boolean qualificationRequired() {

        return expressionMode
                .qualificationRequired();
    }

    public boolean strong() {

        return expressionMode
                .strong();
    }

    public boolean qualified() {

        return expressionMode
                .qualified();
    }
}