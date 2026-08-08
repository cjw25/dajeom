package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;
import com.dajeom.type.UsefulGodMethod;

import java.util.List;
import java.util.Objects;

/**
 * 용신 / 희신 / 기신 후보 분석 결과.
 *
 * 현재 근거:
 *
 * 1. 억부
 * 2. 조후
 * 3. 통관
 * 4. 원국 합화 가능성
 * 5. 격국
 *
 * 아직 최종 용신 확정 단계는 아니다.
 */
public record UsefulGodAnalysis(

        UsefulGodMethod method,

        StrengthLevel strengthLevel,

        /**
         * 억부 관점의 용신 후보.
         */
        List<FiveElement> yongshinCandidates,

        List<FiveElement> huishinCandidates,

        List<FiveElement> gishinCandidates,

        /**
         * 조후 관점의 보완 후보.
         */
        List<FiveElement> temperatureCandidates,

        /**
         * 통관 관점에서 필요한 후보.
         */
        List<FiveElement> mediationCandidates,

        /**
         * 억부와 조후 후보가 일치하는지.
         */
        boolean methodAgreement,

        /**
         * 억부와 통관 후보가 일치하는지.
         */
        boolean mediationAgreement,

        AnalysisConfidence confidence,

        /**
         * 합화 가능성 때문에
         * 기존 오행 구조 재검토가 필요한지.
         */
        boolean relationAdjustmentRequired,

        boolean temperatureAdjustmentRequired,

        /**
         * 통관이 용신 판단에
         * 반영되어야 하는지.
         */
        boolean mediationAdjustmentRequired,

        boolean finalized,

        String ruleVersion

) {

    public UsefulGodAnalysis {

        Objects.requireNonNull(
                method,
                "용신 분석 방법은 필수입니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "강약 단계는 필수입니다."
        );

        Objects.requireNonNull(
                yongshinCandidates,
                "용신 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                huishinCandidates,
                "희신 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                gishinCandidates,
                "기신 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                temperatureCandidates,
                "조후 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                mediationCandidates,
                "통관 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        yongshinCandidates =
                List.copyOf(
                        yongshinCandidates
                );

        huishinCandidates =
                List.copyOf(
                        huishinCandidates
                );

        gishinCandidates =
                List.copyOf(
                        gishinCandidates
                );

        temperatureCandidates =
                List.copyOf(
                        temperatureCandidates
                );

        mediationCandidates =
                List.copyOf(
                        mediationCandidates
                );
    }

    public boolean hasYongshinCandidate() {

        return !yongshinCandidates.isEmpty();
    }

    public boolean hasTemperatureCandidate() {

        return !temperatureCandidates.isEmpty();
    }

    public boolean hasMediationCandidate() {

        return !mediationCandidates.isEmpty();
    }
}