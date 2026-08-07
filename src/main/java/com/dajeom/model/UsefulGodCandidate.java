package com.dajeom.model;

import com.dajeom.type.ConfidenceLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethod;
import com.dajeom.type.UsefulGodType;

import java.util.List;
import java.util.Objects;

/**
 * 특정 오행의 용신·희신·기신 후보 판정 결과다.
 */
public record UsefulGodCandidate(
        FiveElement element,
        UsefulGodType type,
        double score,
        ConfidenceLevel confidenceLevel,
        List<UsefulGodMethod> methods,
        List<String> evidenceCodes,
        List<String> evidenceDescriptions
) {

    public UsefulGodCandidate {
        Objects.requireNonNull(
                element,
                "후보 오행은 필수입니다."
        );

        Objects.requireNonNull(
                type,
                "용신 유형은 필수입니다."
        );

        Objects.requireNonNull(
                confidenceLevel,
                "신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                methods,
                "판정 방식은 필수입니다."
        );

        Objects.requireNonNull(
                evidenceCodes,
                "판정 근거 코드는 필수입니다."
        );

        Objects.requireNonNull(
                evidenceDescriptions,
                "판정 근거 설명은 필수입니다."
        );

        score = normalizeScore(score);
        methods = List.copyOf(methods);
        evidenceCodes = List.copyOf(evidenceCodes);
        evidenceDescriptions =
                List.copyOf(evidenceDescriptions);
    }

    private static double normalizeScore(
            double score
    ) {
        double clamped =
                Math.max(
                        0.0,
                        Math.min(100.0, score)
                );

        return Math.round(clamped * 100.0)
                / 100.0;
    }
}