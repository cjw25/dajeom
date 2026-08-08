package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationEvidenceType;

import java.util.Objects;

/**
 * 하나의 상담 분야를 구성하는
 * 명리학적 근거 데이터.
 *
 * supportWeight와 cautionWeight는
 * 내부 해석 우선순위를 정하기 위한 값이며
 * 확률이나 점수가 아니다.
 *
 * technicalDetail은 사용자 화면에
 * 그대로 출력하기 위한 문장이 아니다.
 */
public record InterpretationEvidence(

        ConsultationCategory category,

        InterpretationEvidenceType type,

        /**
         * 시스템 내부에서 근거를 구분하는 코드.
         *
         * 예:
         *
         * STRENGTH_WEAK
         * ANNUAL_TEN_GOD_GEOP_JAE
         * DAY_CLASH
         */
        String code,

        /**
         * 내부 확인용 기술적인 근거 설명.
         *
         * 최종 사용자에게 그대로 보여주지 않는다.
         */
        String technicalDetail,

        double supportWeight,

        double cautionWeight,

        AnalysisConfidence confidence

) {

    public InterpretationEvidence {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        Objects.requireNonNull(
                type,
                "근거 종류는 필수입니다."
        );

        Objects.requireNonNull(
                code,
                "근거 코드는 필수입니다."
        );

        Objects.requireNonNull(
                technicalDetail,
                "기술 근거 설명은 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        if (code.isBlank()) {

            throw new IllegalArgumentException(
                    "근거 코드는 비어 있을 수 없습니다."
            );
        }

        if (
                supportWeight < 0.0
                        || cautionWeight < 0.0
                        || Double.isNaN(
                        supportWeight
                )
                        || Double.isNaN(
                        cautionWeight
                )
                        || Double.isInfinite(
                        supportWeight
                )
                        || Double.isInfinite(
                        cautionWeight
                )
        ) {

            throw new IllegalArgumentException(
                    "해석 가중치는 0 이상의 유한한 값이어야 합니다."
            );
        }
    }

    public boolean hasSupport() {

        return supportWeight > 0.0;
    }

    public boolean hasCaution() {

        return cautionWeight > 0.0;
    }

    public boolean isNeutral() {

        return supportWeight == 0.0
                && cautionWeight == 0.0;
    }
}