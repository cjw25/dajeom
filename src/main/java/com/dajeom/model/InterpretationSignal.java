package com.dajeom.model;

import com.dajeom.type.SajuReportCategory;
import com.dajeom.type.SignalPolarity;
import com.dajeom.type.SignalStrength;

import java.util.List;
import java.util.Objects;

/**
 * 카테고리 보고서 문장을 만들기 위한
 * 해석 신호 하나다.
 *
 * 보고서 생성기는 signalCode를 기준으로
 * 문장 템플릿을 찾고,
 * title, summary, evidence를 사용해
 * 개인화된 해석을 작성한다.
 */
public record InterpretationSignal(

        String signalCode,

        SajuReportCategory category,

        String title,

        String summary,

        SignalPolarity polarity,

        SignalStrength strength,

        double score,

        List<String> evidenceCodes,

        List<String> evidenceDescriptions
) {

    public InterpretationSignal {

        Objects.requireNonNull(
                signalCode,
                "해석 신호 코드는 필수입니다."
        );

        Objects.requireNonNull(
                category,
                "해석 카테고리는 필수입니다."
        );

        Objects.requireNonNull(
                title,
                "해석 신호 제목은 필수입니다."
        );

        Objects.requireNonNull(
                summary,
                "해석 신호 요약은 필수입니다."
        );

        Objects.requireNonNull(
                polarity,
                "해석 신호 방향은 필수입니다."
        );

        Objects.requireNonNull(
                strength,
                "해석 신호 강도는 필수입니다."
        );

        Objects.requireNonNull(
                evidenceCodes,
                "해석 근거 코드는 필수입니다."
        );

        Objects.requireNonNull(
                evidenceDescriptions,
                "해석 근거 설명은 필수입니다."
        );

        score =
                normalizeScore(
                        score
                );

        evidenceCodes =
                List.copyOf(
                        evidenceCodes
                );

        evidenceDescriptions =
                List.copyOf(
                        evidenceDescriptions
                );
    }

    public static InterpretationSignal of(

            String signalCode,

            SajuReportCategory category,

            String title,

            String summary,

            SignalPolarity polarity,

            double score,

            List<String> evidenceCodes,

            List<String> evidenceDescriptions
    ) {

        return new InterpretationSignal(

                signalCode,

                category,

                title,

                summary,

                polarity,

                SignalStrength.fromScore(
                        score
                ),

                score,

                evidenceCodes,

                evidenceDescriptions
        );
    }

    private static double normalizeScore(
            double score
    ) {

        double clamped =
                Math.max(
                        -100.0,
                        Math.min(
                                100.0,
                                score
                        )
                );

        return Math.round(
                clamped * 100.0
        ) / 100.0;
    }
}