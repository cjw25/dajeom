package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponseAnalysis;
import com.dajeom.model.NatalConditionalResponsePattern;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.type.NatalConditionalResponsePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 기존 CrossFunctional Pattern을
 * 상황별 기능 작동 Pattern으로 변환한다.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalCrossFunctionalAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * NatalConditionalResponseAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 10-8 REFERENCE Pattern은
 * Conditional Response로 승격하지 않는다.
 *
 * 이유:
 *
 * 필요한 십성은 존재하지만
 * 실제 Pattern으로 다루기에는
 * 세력이 아직 약하기 때문이다.
 *
 * 원본 CrossFunctionalAnalysis에는
 * REFERENCE가 그대로 남아 있다.
 *
 * 2.
 * MEANINGFUL / STRONG Pattern만
 * 조건부 작동 Pattern으로 변환한다.
 *
 * 3.
 * 10-9 상태:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * 를 그대로 유지한다.
 *
 * 4.
 * 새로운 점수 없음.
 *
 * 새로운 threshold 없음.
 *
 * Family Power 비교 없음.
 *
 * 5.
 * "참는다"
 * "폭발한다"
 * "고집이 세다"
 * "말이 세다"
 *
 * 같은 심리 문장을 만들지 않는다.
 *
 * 6.
 * 이 Engine의 기능순서는
 * 명리 구조상의 작용 방향이다.
 *
 * 심리학적 시간순서를
 * 임의로 만들어낸 것이 아니다.
 *
 * =================================================
 *
 * 다음 단계:
 *
 * NatalConditionalResponseAnalysis
 *
 * +
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * Personality Pattern Analysis
 *
 * ↓
 *
 * 기본성향
 */
@Component
public class NatalConditionalResponseAnalysisEngine {

    public NatalConditionalResponseAnalysis analyze(

            NatalCrossFunctionalAnalysis crossFunctionalAnalysis

    ) {

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "CrossFunctional Analysis는 필수입니다."
        );

        List<NatalConditionalResponsePattern> result =
                new ArrayList<>();

        /*
         * =================================================
         * 실제 구조적으로 성립한:
         *
         * MEANINGFUL
         * STRONG
         *
         * Pattern만 사용한다.
         *
         * REFERENCE는
         * CrossFunctionalAnalysis에 그대로 남긴다.
         * =================================================
         */
        for (
                NatalCrossFunctionalPattern sourcePattern
                : crossFunctionalAnalysis.effectivePatterns()
        ) {

            NatalConditionalResponsePatternType patternType =
                    NatalConditionalResponsePatternType.from(
                            sourcePattern.patternType()
                    );

            /*
             * =================================================
             * Pattern의 중심성도 보존한다.
             *
             * 격국을 점수에 더하는 것이 아니라:
             *
             * 이 작동에 참여한 기능이
             * 원국 주격의 중심인지
             *
             * 만 보존한다.
             * =================================================
             */
            boolean primaryStructuralCenter =
                    sourcePattern
                            .primaryFunctionEffect()
                            .primaryStructure();

            boolean secondaryStructuralCenter =
                    sourcePattern
                            .secondaryFunctionEffect()
                            .map(
                                    NatalTenGodFamilyEffect
                                            ::primaryStructure
                            )
                            .orElse(
                                    false
                            );

            result.add(
                    new NatalConditionalResponsePattern(

                            patternType,

                            sourcePattern,

                            patternType.functionalSequence(),

                            patternType.coreSelfIncluded(),

                            primaryStructuralCenter,

                            secondaryStructuralCenter,

                            crossFunctionalAnalysis.completeChart()
                    )
            );
        }

        return new NatalConditionalResponseAnalysis(

                crossFunctionalAnalysis,

                result,

                crossFunctionalAnalysis.completeChart()
        );
    }
}