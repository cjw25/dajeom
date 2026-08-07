package com.dajeom.analyzer;

import com.dajeom.model.StrengthAnalysis;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * 신강·신약을 기준으로 억부용신 후보 점수를 산출한다.
 */
@Component
public class BalancingUsefulGodAnalyzer {

    public Map<FiveElement, Double> analyze(
            StrengthAnalysis strengthAnalysis
    ) {
        EnumMap<FiveElement, Double> scores =
                createEmptyScores();

        FiveElement dayMasterElement =
                strengthAnalysis.dayMasterElement();

        FiveElement resourceElement =
                findGeneratingElement(
                        dayMasterElement
                );

        FiveElement outputElement =
                findGeneratedElement(
                        dayMasterElement
                );

        FiveElement wealthElement =
                findControlledElement(
                        dayMasterElement
                );

        FiveElement officerElement =
                findControllingElement(
                        dayMasterElement
                );

        StrengthLevel level =
                strengthAnalysis.level();

        if (isWeak(level)) {
            add(scores, resourceElement, 40.0);
            add(scores, dayMasterElement, 35.0);

            add(scores, outputElement, -15.0);
            add(scores, wealthElement, -25.0);
            add(scores, officerElement, -30.0);

        } else if (isStrong(level)) {
            add(scores, outputElement, 35.0);
            add(scores, wealthElement, 30.0);
            add(scores, officerElement, 25.0);

            add(scores, dayMasterElement, -30.0);
            add(scores, resourceElement, -25.0);

        } else {
            add(scores, outputElement, 10.0);
            add(scores, wealthElement, 10.0);
            add(scores, officerElement, 5.0);
            add(scores, resourceElement, 5.0);
        }

        return scores;
    }

    private boolean isWeak(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_WEAK
                || level == StrengthLevel.WEAK
                || level == StrengthLevel.SLIGHTLY_WEAK;
    }

    private boolean isStrong(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_STRONG
                || level == StrengthLevel.STRONG
                || level == StrengthLevel.SLIGHTLY_STRONG;
    }

    private FiveElement findGeneratingElement(
            FiveElement target
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (candidate.generates(target)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "생조 오행을 찾을 수 없습니다."
        );
    }

    private FiveElement findGeneratedElement(
            FiveElement source
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (source.generates(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "설기 오행을 찾을 수 없습니다."
        );
    }

    private FiveElement findControlledElement(
            FiveElement source
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (source.controls(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "재성 오행을 찾을 수 없습니다."
        );
    }

    private FiveElement findControllingElement(
            FiveElement target
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (candidate.controls(target)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "관성 오행을 찾을 수 없습니다."
        );
    }

    private EnumMap<FiveElement, Double>
    createEmptyScores() {
        EnumMap<FiveElement, Double> scores =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element
                : FiveElement.values()) {

            scores.put(element, 0.0);
        }

        return scores;
    }

    private void add(
            Map<FiveElement, Double> scores,
            FiveElement element,
            double score
    ) {
        scores.compute(
                element,
                (key, value) ->
                        value == null
                                ? score
                                : value + score
        );
    }
}