package com.dajeom.analyzer;

import com.dajeom.model.TwelveLifeStageContextAnalysis;
import com.dajeom.model.TwelveLifeStageContextEvidence;
import com.dajeom.model.TwelveLifeStageEvidence;
import com.dajeom.model.TwelveLifeStageEvidenceAnalysis;
import com.dajeom.rule.TwelveLifeStageContextRule;
import com.dajeom.type.TwelveLifeStageContextScope;
import com.dajeom.type.TwelveLifeStagePositionContextType;
import com.dajeom.type.TwelveLifeStageStateGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 11차-4.
 *
 * 11-3 12운성 Evidence에:
 *
 * - 상태 그룹
 * - 위치 Context
 * - 시간 미상 Scope
 *
 * 를 적용한다.
 *
 * =================================================
 *
 * 입력:
 *
 * TwelveLifeStageEvidenceAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * TwelveLifeStageContextAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 길흉 판정
 * - 신강신약 수정
 * - 격국 수정
 * - 용신 수정
 * - 궁위 가족관계 확정
 *
 * 을 하지 않는다.
 */
@Component
public class TwelveLifeStageContextAnalyzer {

    private final TwelveLifeStageContextRule
            twelveLifeStageContextRule;

    public TwelveLifeStageContextAnalyzer(
            TwelveLifeStageContextRule twelveLifeStageContextRule
    ) {

        this.twelveLifeStageContextRule =
                Objects.requireNonNull(
                        twelveLifeStageContextRule,
                        "TwelveLifeStageContextRule이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 11-4 API
     * =================================================
     */
    public TwelveLifeStageContextAnalysis analyze(
            TwelveLifeStageEvidenceAnalysis evidenceAnalysis
    ) {

        Objects.requireNonNull(
                evidenceAnalysis,
                "11-3 TwelveLifeStageEvidenceAnalysis가 필요합니다."
        );

        List<TwelveLifeStageContextEvidence> contexts =
                new ArrayList<>();

        for (
                TwelveLifeStageEvidence evidence
                : evidenceAnalysis.evidences()
        ) {

            TwelveLifeStageStateGroup stateGroup =
                    twelveLifeStageContextRule
                            .stateGroupOf(
                                    evidence.stage()
                            );

            TwelveLifeStagePositionContextType positionContext =
                    twelveLifeStageContextRule
                            .positionContextOf(
                                    evidence.position()
                            );

            contexts.add(
                    new TwelveLifeStageContextEvidence(
                            evidence,
                            stateGroup,
                            positionContext
                    )
            );
        }

        /*
         * =================================================
         * 위치순 정렬
         *
         * enum ordinal에 직접 의존하지 않고
         * 원국 고정 순서를 명시한다.
         * =================================================
         */

        contexts.sort(
                Comparator.comparingInt(
                        context ->
                                positionOrder(
                                        context
                                                .positionContext()
                                )
                )
        );

        TwelveLifeStageContextScope scope =
                twelveLifeStageContextRule
                        .scopeOf(
                                evidenceAnalysis
                                        .completeChart()
                        );

        return new TwelveLifeStageContextAnalysis(
                evidenceAnalysis.dayMaster(),
                evidenceAnalysis,
                contexts,
                scope,
                evidenceAnalysis.knownPillarCount(),
                evidenceAnalysis.completeChart()
        );
    }

    private int positionOrder(
            TwelveLifeStagePositionContextType positionContext
    ) {

        return switch (
                positionContext
                ) {

            case YEAR_BACKGROUND ->
                    0;

            case MONTH_ENVIRONMENT ->
                    1;

            case DAY_CORE ->
                    2;

            case HOUR_EXTENSION ->
                    3;
        };
    }
}