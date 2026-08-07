package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * 서로 충돌하는 강한 오행 사이를 연결하는 통관 오행을 찾는다.
 */
@Component
public class MediationUsefulGodAnalyzer {

    private static final double STRONG_THRESHOLD =
            25.0;

    public Map<FiveElement, Double> analyze(
            ElementStrength elementStrength
    ) {
        EnumMap<FiveElement, Double> scores =
                createEmptyScores();

        for (FiveElement controller
                : FiveElement.values()) {

            for (FiveElement controlled
                    : FiveElement.values()) {

                if (!controller.controls(controlled)) {
                    continue;
                }

                double controllerScore =
                        elementStrength.percentageOf(
                                controller
                        );

                double controlledScore =
                        elementStrength.percentageOf(
                                controlled
                        );

                if (controllerScore
                        < STRONG_THRESHOLD
                        || controlledScore
                        < STRONG_THRESHOLD) {

                    continue;
                }

                FiveElement mediator =
                        findMediator(
                                controller,
                                controlled
                        );

                if (mediator != null) {
                    scores.compute(
                            mediator,
                            (key, value) ->
                                    value == null
                                            ? 25.0
                                            : value + 25.0
                    );
                }
            }
        }

        return scores;
    }

    /**
     * 극하는 오행이 생하고,
     * 그 결과가 피극 오행을 생하는 연결 오행을 찾는다.
     */
    private FiveElement findMediator(
            FiveElement controller,
            FiveElement controlled
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (controller.generates(candidate)
                    && candidate.generates(controlled)) {

                return candidate;
            }
        }

        return null;
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
}