package com.dajeom.analyzer;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.ClimateElementCandidateRule;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-3.
 *
 * 6-2 조후 필요성을
 * 실제 五行 후보로 변환한다.
 *
 * 처리:
 *
 * ClimateAdjustmentNeedAnalysis
 *
 * ↓
 *
 * 木 火 土 金 水 각각 평가
 *
 * ↓
 *
 * 직접 조후 보정 여부
 *
 * ↓
 *
 * 다른 축과의 충돌 여부
 *
 * ↓
 *
 * 현재 원국 보유량
 *
 * ↓
 *
 * 간접 연결 필요 여부
 *
 * ↓
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_NEEDED
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * PRIMARY는 용신 확정이 아니다.
 *
 * 2.
 * 寒燥/熱濕처럼 조후 요구가 경쟁하면
 *    필요도가 같을 경우 PRIMARY를
 *    강제로 만들지 않는다.
 *
 * 3.
 * 이미 오행이 매우 많은 경우
 *    "더 필요하다"는 단순 판단을 하지 않는다.
 *
 * 4.
 * 土는 6-3에서 한난조습 직접 후보로
 *    일반화하지 않는다.
 *
 * 5.
 * 병약/통관/격국/신강신약과의
 *    최종 충돌 조정은 뒤 단계에서 수행한다.
 */
@Component
public class ClimateElementCandidateAnalyzer {

    private final ClimateAdjustmentNeedAnalyzer
            climateAdjustmentNeedAnalyzer;

    private final ClimateElementCandidateRule
            climateElementCandidateRule;

    public ClimateElementCandidateAnalyzer(

            ClimateAdjustmentNeedAnalyzer
                    climateAdjustmentNeedAnalyzer,

            ClimateElementCandidateRule
                    climateElementCandidateRule

    ) {

        this.climateAdjustmentNeedAnalyzer =
                Objects.requireNonNull(
                        climateAdjustmentNeedAnalyzer,
                        "ClimateAdjustmentNeedAnalyzer가 필요합니다."
                );

        this.climateElementCandidateRule =
                Objects.requireNonNull(
                        climateElementCandidateRule,
                        "ClimateElementCandidateRule이 필요합니다."
                );
    }

    /**
     * 5차 StructureAnalysis부터
     * 6-1 → 6-2 → 6-3을 연속 실행한다.
     */
    public ClimateElementCandidateAnalysis analyze(
            StructureAnalysis structure
    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        ClimateAdjustmentNeedAnalysis need =
                climateAdjustmentNeedAnalyzer
                        .analyze(
                                structure
                        );

        return analyze(
                need
        );
    }

    /**
     * 6-2 결과를 이미 가지고 있다면
     * 그대로 재사용한다.
     *
     * 6차 전체 Engine에서는
     * 이 오버로드를 사용한다.
     */
    public ClimateElementCandidateAnalysis analyze(
            ClimateAdjustmentNeedAnalysis need
    ) {

        Objects.requireNonNull(
                need,
                "조후 필요성 분석이 필요합니다."
        );

        List<ClimateElementCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            ClimateElementCandidateRule
                    .ClimateElementDecision decision =
                    climateElementCandidateRule
                            .evaluate(
                                    element,
                                    need
                            );

            candidates.add(
                    new ClimateElementCandidate(
                            decision.element(),
                            decision.role(),
                            decision.targets(),
                            decision.addressedNeedLevel(),
                            decision.presenceLevel(),
                            decision.existingShareBasisPoints(),
                            decision.directTemperatureCorrection(),
                            decision.directMoistureCorrection(),
                            decision.temperatureConflict(),
                            decision.moistureConflict(),
                            decision.bridgeCandidate()
                    )
            );
        }

        /*
         * ================================================
         * 정렬
         * ================================================
         *
         * 1.
         * 후보 역할
         *
         * 2.
         * 대응하는 조후 필요도
         *
         * 3.
         * 직접 보정 축의 수
         *
         * 4.
         * 현재 오행 비중이 적은 후보
         *
         * 순.
         *
         * 이것은 용신 점수가 아니라
         * 결과 표시 및 후속 처리 순서다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        ClimateElementCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ClimateElementCandidate candidate
                                                ) ->
                                                        candidate
                                                                .addressedNeedLevel()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ClimateElementCandidate
                                                        ::targetCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                ClimateElementCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new ClimateElementCandidateAnalysis(
                need.dayMaster(),
                need.monthBranch(),
                need,
                candidates,
                need.completeChart()
        );
    }
}