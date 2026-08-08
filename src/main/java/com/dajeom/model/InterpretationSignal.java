package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 상담 분야에 대한
 * 통합 해석 신호.
 *
 * 아직 사용자 상담문은 아니다.
 *
 * 여러 명리 근거를 하나의 분야로 묶어서
 * 다음 단계의 ConsultationEngine이
 * 사용할 수 있도록 만든 데이터다.
 */
public record InterpretationSignal(

        ConsultationCategory category,

        double supportScore,

        double cautionScore,

        InterpretationPolarity polarity,

        InterpretationIntensity intensity,

        List<InterpretationEvidence> evidences

) {

    public InterpretationSignal {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        Objects.requireNonNull(
                polarity,
                "해석 방향은 필수입니다."
        );

        Objects.requireNonNull(
                intensity,
                "해석 강도는 필수입니다."
        );

        Objects.requireNonNull(
                evidences,
                "해석 근거 목록은 필수입니다."
        );

        if (
                supportScore < 0.0
                        || cautionScore < 0.0
        ) {

            throw new IllegalArgumentException(
                    "해석 점수는 음수일 수 없습니다."
            );
        }

        evidences =
                List.copyOf(
                        evidences
                );

        boolean invalidCategory =
                evidences
                        .stream()
                        .anyMatch(
                                evidence ->
                                        evidence.category()
                                                != category
                        );

        if (invalidCategory) {

            throw new IllegalArgumentException(
                    "다른 상담 분야의 근거가 포함되어 있습니다."
            );
        }
    }

    public boolean hasEvidence() {

        return !evidences.isEmpty();
    }

    public boolean isSupportive() {

        return polarity
                == InterpretationPolarity.SUPPORTIVE;
    }

    public boolean isCaution() {

        return polarity
                == InterpretationPolarity.CAUTION;
    }

    public boolean isMixed() {

        return polarity
                == InterpretationPolarity.MIXED;
    }
}