package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponseAnalysis;
import com.dajeom.model.NatalConditionalResponsePattern;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalPersonalityFunctionProfile;
import com.dajeom.model.NatalPersonalityPatternAnalysis;
import com.dajeom.model.NatalPersonalitySituationProfile;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국 Functional / CrossFunctional /
 * ConditionalResponse 분석을
 *
 * 기본성향 전용 Pattern Analysis로
 * 통합한다.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalConditionalResponseAnalysis
 *
 * =================================================
 *
 * 이 객체를 통해 이미:
 *
 * NatalCrossFunctionalAnalysis
 *
 * NatalFunctionalAnalysis
 *
 * 까지 동일 체인으로 접근 가능하다.
 *
 * =================================================
 *
 * 출력:
 *
 * NatalPersonalityPatternAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 새로운 성격점수를 만들지 않는다.
 *
 * 2.
 * 십성 Family 세력을
 * 서로 비교해 임의 성격유형을 만들지 않는다.
 *
 * 3.
 * 주격은 추가점수가 아니라
 * 기능 중심성으로만 사용한다.
 *
 * 4.
 * 기존 전통 십성 Pattern과
 * 교차검증 상태를 그대로 보존한다.
 *
 * 5.
 * Pattern이 없는 상황은
 * 빈 SituationProfile로 만든다.
 *
 * 이는:
 *
 * "그 상황에서 아무 행동도 하지 않는다"
 *
 * 가 아니라:
 *
 * "현재 구조화된 명리 근거로
 * 강한 조건부 판단을 만들지 않는다"
 *
 * 는 의미다.
 *
 * =================================================
 *
 * 다음 단계:
 *
 * NatalPersonalityPatternAnalysis
 *
 * ↓
 *
 * Personality Deterministic Judgment
 *
 * ↓
 *
 * PERSONALITY 상담 카테고리
 */
@Component
public class NatalPersonalityPatternAnalysisEngine {

    public NatalPersonalityPatternAnalysis analyze(

            NatalConditionalResponseAnalysis
                    conditionalResponseAnalysis

    ) {

        Objects.requireNonNull(
                conditionalResponseAnalysis,
                "Conditional Response Analysis는 필수입니다."
        );

        NatalCrossFunctionalAnalysis crossFunctionalAnalysis =
                conditionalResponseAnalysis
                        .crossFunctionalAnalysis();

        NatalFunctionalAnalysis natalFunctionalAnalysis =
                crossFunctionalAnalysis
                        .natalFunctionalAnalysis();

        /*
         * =================================================
         * 십성 5대 기능별 Profile
         * =================================================
         */

        List<NatalPersonalityFunctionProfile>
                functionProfiles =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            NatalTenGodFamilyEffect functionEffect =
                    natalFunctionalAnalysis
                            .requireFunction(
                                    functionType
                            );

            /*
             * 이 기능축에 참여하는
             * 모든 Cross Pattern.
             *
             * REFERENCE도 보존한다.
             *
             * 실제 강한 출력 근거인지 여부는
             * Profile helper에서 구분한다.
             */
            List<NatalCrossFunctionalPattern>
                    crossPatterns =
                    crossFunctionalAnalysis
                            .patternsInvolving(
                                    functionType
                            );

            /*
             * 실제 ConditionalResponse로
             * 승격된 MEANINGFUL/STRONG Pattern 중
             * 해당 기능축이 참여하는 것.
             */
            List<NatalConditionalResponsePattern>
                    conditionalPatterns =
                    conditionalResponseAnalysis
                            .patterns()
                            .stream()
                            .filter(
                                    pattern ->
                                            pattern
                                                    .functionalSequence()
                                                    .contains(
                                                            functionType
                                                    )
                            )
                            .toList();

            functionProfiles.add(
                    new NatalPersonalityFunctionProfile(

                            functionType,

                            functionEffect,

                            crossPatterns,

                            conditionalPatterns,

                            natalFunctionalAnalysis
                                    .completeChart()
                    )
            );
        }

        /*
         * =================================================
         * 상황별 Profile
         *
         * 모든 enum 값을 생성한다.
         *
         * 근거가 없는 상황은
         * patterns = empty
         *
         * 로 명확히 보존한다.
         * =================================================
         */

        List<NatalPersonalitySituationProfile>
                situationProfiles =
                new ArrayList<>();

        for (
                NatalResponseSituationType situationType
                : NatalResponseSituationType.values()
        ) {

            situationProfiles.add(
                    new NatalPersonalitySituationProfile(

                            situationType,

                            conditionalResponseAnalysis
                                    .patternsOf(
                                            situationType
                                    ),

                            natalFunctionalAnalysis
                                    .completeChart()
                    )
            );
        }

        /*
         * =================================================
         * 최종 Personality Pattern Aggregate
         * =================================================
         */

        return new NatalPersonalityPatternAnalysis(

                natalFunctionalAnalysis,

                crossFunctionalAnalysis,

                conditionalResponseAnalysis,

                functionProfiles,

                situationProfiles,

                natalFunctionalAnalysis
                        .completeChart()
        );
    }
}