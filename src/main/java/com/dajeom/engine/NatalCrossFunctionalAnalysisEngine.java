package com.dajeom.engine;

import com.dajeom.model.NatalCoreSelfEffect;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.model.TenGodCrossValidatedPattern;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 Functional 기능축과
 * 기존 10-9 십성 교차검증 Pattern을 연결한다.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalFunctionalAnalysis
 *
 * =================================================
 *
 * 사용 Source:
 *
 * TenGodCrossValidatedPattern
 *
 * =================================================
 *
 * 출력:
 *
 * NatalCrossFunctionalAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 새로운 심리 Pattern을 만들지 않는다.
 *
 * 2.
 * Family Power를 서로:
 *
 * - 빼기
 * - 나누기
 * - 평균
 * - 새로운 threshold 비교
 *
 * 하지 않는다.
 *
 * 3.
 * 기존 10-9 Pattern의 최종 상태:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * 를 임의로 변경하지 않는다.
 *
 * 4.
 * 10-8 REFERENCE Pattern도 삭제하지 않는다.
 *
 * 다만 structurallyEffective()로
 * 실제 행동 Pattern의 강한 Evidence와 구분한다.
 *
 * 5.
 * summary/reasons 문자열을
 * 파싱하여 Pattern을 추정하지 않는다.
 *
 * sourcePattern.patternType()
 *
 * 라는 구조화 필드만 사용한다.
 *
 * =================================================
 *
 * 다음 단계:
 *
 * NatalCrossFunctionalAnalysis
 *
 * ↓
 *
 * NatalConditionalResponseAnalysis
 *
 * ↓
 *
 * Personality
 */
@Component
public class NatalCrossFunctionalAnalysisEngine {

    public NatalCrossFunctionalAnalysis analyze(

            NatalFunctionalAnalysis natalFunctionalAnalysis

    ) {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "원국 Functional Analysis는 필수입니다."
        );

        List<NatalCrossFunctionalPattern> patterns =
                new ArrayList<>();

        /*
         * =================================================
         * 기존 10-9 Pattern 하나씩
         * Functional Pattern으로 변환
         * =================================================
         */
        for (
                TenGodCrossValidatedPattern sourcePattern
                : natalFunctionalAnalysis.crossValidatedPatterns()
        ) {

            NatalCrossFunctionalPatternType patternType =
                    NatalCrossFunctionalPatternType.from(
                            sourcePattern.patternType()
                    );

            /*
             * =================================================
             * 주 기능축
             * =================================================
             */
            NatalTenGodFamilyEffect primary =
                    natalFunctionalAnalysis.requireFunction(
                            patternType.primaryFunction()
                    );

            /*
             * =================================================
             * 보조 기능축
             *
             * BETWEEN_FUNCTIONS일 때만 존재
             * =================================================
             */
            Optional<NatalTenGodFamilyEffect> secondary =
                    createSecondaryFunction(
                            natalFunctionalAnalysis,
                            patternType
                    );

            /*
             * =================================================
             * CoreSelf
             *
             * 재다신약처럼
             * FUNCTION_TO_CORE_SELF일 때만 연결
             * =================================================
             */
            Optional<NatalCoreSelfEffect> coreSelf =
                    patternType.scope()
                            == NatalCrossFunctionalScope.FUNCTION_TO_CORE_SELF

                            ? Optional.of(
                            natalFunctionalAnalysis.coreSelf()
                    )

                            : Optional.empty();

            /*
             * =================================================
             * Pattern 생성
             * =================================================
             */
            patterns.add(
                    new NatalCrossFunctionalPattern(

                            patternType,

                            sourcePattern,

                            primary,

                            secondary,

                            coreSelf,

                            natalFunctionalAnalysis.completeChart()
                    )
            );
        }

        /*
         * =================================================
         * 최종 Aggregate
         * =================================================
         */
        return new NatalCrossFunctionalAnalysis(

                natalFunctionalAnalysis,

                patterns,

                natalFunctionalAnalysis.completeChart()
        );
    }

    /**
     * BETWEEN_FUNCTIONS Pattern의
     * 두 번째 기능축을 연결한다.
     *
     * 다른 Scope에서는
     * 보조 기능축을 만들지 않는다.
     */
    private Optional<NatalTenGodFamilyEffect>
    createSecondaryFunction(

            NatalFunctionalAnalysis natalFunctionalAnalysis,

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType.scope()
                        != NatalCrossFunctionalScope.BETWEEN_FUNCTIONS
        ) {

            return Optional.empty();
        }

        NatalTenGodFunctionType secondaryType =
                patternType
                        .secondaryFunction()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "기능 간 Pattern에 "
                                                + "보조 기능축 정의가 없습니다: "
                                                + patternType
                                )
                        );

        return Optional.of(
                natalFunctionalAnalysis.requireFunction(
                        secondaryType
                )
        );
    }
}