package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * 원국에서 지나치게 강한 오행을 병으로 보고
 * 이를 조절하는 오행을 약으로 판정한다.
 */
@Component
public class DiseaseRemedyAnalyzer {

    private static final double EXCESS_THRESHOLD =
            35.0;

    public Map<FiveElement, Double> analyze(
            ElementStrength elementStrength
    ) {
        EnumMap<FiveElement, Double> scores =
                createEmptyScores();

        for (FiveElement excessive
                : FiveElement.values()) {

            double percentage =
                    elementStrength.percentageOf(
                            excessive
                    );

            if (percentage < EXCESS_THRESHOLD) {
                continue;
            }

            FiveElement controllingElement =
                    findControllingElement(
                            excessive
                    );

            FiveElement drainingElement =
                    findGeneratedElement(
                            excessive
                    );

            add(
                    scores,
                    controllingElement,
                    30.0
            );

            add(
                    scores,
                    drainingElement,
                    20.0
            );

            add(
                    scores,
                    excessive,
                    -30.0
            );
        }

        return scores;
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
                "과다 오행을 극하는 오행을 찾을 수 없습니다."
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
                "과다 오행을 설기하는 오행을 찾을 수 없습니다."
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