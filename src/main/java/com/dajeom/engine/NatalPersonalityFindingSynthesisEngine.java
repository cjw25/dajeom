package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityFinding;
import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityFindingSynthesis;
import com.dajeom.model.NatalPersonalityFindingSynthesisAnalysis;
import com.dajeom.model.NatalPersonalityFunctionProfile;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Personality Finding끼리
 * 실제 구조적으로 연결되는 조합을 찾아
 * Synthesis로 만든다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 새로운 명리 Pattern을 추정하지 않는다.
 *
 * PersonalityFindingSynthesisType에
 * 명시된 조합만 사용한다.
 *
 * =================================================
 *
 * 2.
 * Finding 개수로 강도를 정하지 않는다.
 *
 * =================================================
 *
 * 3.
 * Source 중 하나가 REFERENCE이면
 * Synthesis도 REFERENCE.
 *
 * =================================================
 *
 * 4.
 * Source가 모두 직접판정 가능해야
 * Synthesis도 GROUNDED 이상이 된다.
 *
 * =================================================
 *
 * 5.
 * STRONGLY_GROUNDED는:
 *
 * - Source가 모두 직접판정 가능
 * - 최소 하나의 강한 Source 존재
 * - 주격 중심 기능이 참여
 *
 * 를 함께 확인한다.
 *
 * =================================================
 *
 * 6.
 * 출생시간이 없는 경우
 * STRONGLY_GROUNDED는 GROUNDED로 제한한다.
 *
 * =================================================
 *
 * 7.
 * QUALIFIED / WEAKENED / CONFLICTED
 * Source는 버리지 않는다.
 *
 * qualificationRequired로 유지한다.
 *
 * =================================================
 *
 * 8.
 * 이 단계에서도
 * 사용자 성격문장을 만들지 않는다.
 */
@Component
public class NatalPersonalityFindingSynthesisEngine {

    public NatalPersonalityFindingSynthesisAnalysis analyze(

            NatalPersonalityFindingAnalysis
                    findingAnalysis

    ) {

        Objects.requireNonNull(
                findingAnalysis,
                "Personality Finding Analysis는 필수입니다."
        );

        List<NatalPersonalityFindingSynthesis> syntheses =
                new ArrayList<>();

        /*
         * =================================================
         * 정의된 모든 Synthesis Type을 확인한다.
         * =================================================
         */

        for (
                PersonalityFindingSynthesisType synthesisType
                : PersonalityFindingSynthesisType.values()
        ) {

            Optional<List<NatalPersonalityFinding>>
                    sourceFindings =
                    findRequiredFindings(
                            findingAnalysis,
                            synthesisType
                    );

            /*
             * 필요한 Finding 자체가 하나라도 없으면
             * 이 Synthesis는 원국에 존재하지 않는다.
             */
            if (
                    sourceFindings.isEmpty()
            ) {

                continue;
            }

            List<NatalPersonalityFinding> sources =
                    sourceFindings.get();

            List<NatalTenGodFunctionType> involvedFunctions =
                    collectInvolvedFunctions(
                            sources
                    );

            boolean structurallyCentered =
                    isStructurallyCentered(
                            findingAnalysis,
                            involvedFunctions
                    );

            PersonalityJudgmentGroundingLevel groundingLevel =
                    determineGrounding(
                            sources,
                            structurallyCentered,
                            findingAnalysis.completeChart()
                    );

            boolean qualificationRequired =
                    sources
                            .stream()
                            .anyMatch(
                                    NatalPersonalityFinding
                                            ::qualificationRequired
                            );

            syntheses.add(
                    new NatalPersonalityFindingSynthesis(

                            synthesisType,

                            sources,

                            involvedFunctions,

                            groundingLevel,

                            groundingLevel
                                    .directAssertionAllowed(),

                            qualificationRequired,

                            structurallyCentered,

                            findingAnalysis.completeChart()
                    )
            );
        }

        return new NatalPersonalityFindingSynthesisAnalysis(

                findingAnalysis,

                syntheses,

                findingAnalysis.completeChart()
        );
    }

    /**
     * =================================================
     * Synthesis에 필요한 Finding 조회
     * =================================================
     *
     * 문자열 parsing 없이
     * PersonalityFindingTheme만 사용한다.
     */
    private Optional<List<NatalPersonalityFinding>>
    findRequiredFindings(

            NatalPersonalityFindingAnalysis findingAnalysis,

            PersonalityFindingSynthesisType synthesisType

    ) {

        List<NatalPersonalityFinding> result =
                new ArrayList<>();

        for (
                PersonalityFindingTheme theme
                : synthesisType.requiredThemes()
        ) {

            Optional<NatalPersonalityFinding> finding =
                    findingAnalysis
                            .findingOf(
                                    theme
                            );

            if (
                    finding.isEmpty()
            ) {

                return Optional.empty();
            }

            result.add(
                    finding.get()
            );
        }

        return Optional.of(
                List.copyOf(
                        result
                )
        );
    }

    /**
     * =================================================
     * 참여 기능축 수집
     * =================================================
     *
     * 순서는 Source Finding 등장순서를 유지한다.
     */
    private List<NatalTenGodFunctionType>
    collectInvolvedFunctions(

            List<NatalPersonalityFinding> sourceFindings

    ) {

        Set<NatalTenGodFunctionType> functions =
                new LinkedHashSet<>();

        for (
                NatalPersonalityFinding finding
                : sourceFindings
        ) {

            functions.addAll(
                    finding.involvedFunctions()
            );
        }

        return List.copyOf(
                functions
        );
    }

    /**
     * =================================================
     * 주격 중심 기능 참여 여부
     * =================================================
     *
     * 격국을 추가 점수로 사용하지 않는다.
     *
     * "이 Synthesis가 원국의 중심 기능과
     * 연결되어 있는가"
     *
     * 만 보존한다.
     */
    private boolean isStructurallyCentered(

            NatalPersonalityFindingAnalysis findingAnalysis,

            List<NatalTenGodFunctionType> involvedFunctions

    ) {

        for (
                NatalTenGodFunctionType functionType
                : involvedFunctions
        ) {

            NatalPersonalityFunctionProfile profile =
                    findingAnalysis
                            .deterministicAnalysis()
                            .patternAnalysis()
                            .requireFunction(
                                    functionType
                            );

            if (
                    profile.structuralCenter()
            ) {

                return true;
            }
        }

        return false;
    }

    /**
     * =================================================
     * Synthesis Grounding 판정
     * =================================================
     *
     * 다수결이나 점수를 쓰지 않는다.
     */
    private PersonalityJudgmentGroundingLevel
    determineGrounding(

            List<NatalPersonalityFinding> sourceFindings,

            boolean structurallyCentered,

            boolean completeChart

    ) {

        /*
         * =================================================
         * 필요한 Source 중 하나라도
         * REFERENCE면
         *
         * 결합구조 자체를
         * 강하게 성립했다고 볼 수 없다.
         *
         * 단:
         *
         * Finding과 Synthesis 모두
         * 내부에는 그대로 남는다.
         * =================================================
         */

        boolean hasReference =
                sourceFindings
                        .stream()
                        .anyMatch(
                                finding ->
                                        finding.groundingLevel()
                                                == PersonalityJudgmentGroundingLevel
                                                .REFERENCE
                        );

        if (
                hasReference
        ) {

            return PersonalityJudgmentGroundingLevel
                    .REFERENCE;
        }

        /*
         * =================================================
         * Source 중 하나라도
         * 직접판정 불가라면
         *
         * Synthesis도 CONTEXTUAL.
         * =================================================
         */

        boolean allDirect =
                sourceFindings
                        .stream()
                        .allMatch(
                                NatalPersonalityFinding
                                        ::directAssertionAllowed
                        );

        if (
                !allDirect
        ) {

            return PersonalityJudgmentGroundingLevel
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * 최소 하나의 Source가
         * STRONGLY_GROUNDED이고
         *
         * +
         *
         * 원국 구조 중심 기능까지 참여하면
         *
         * 복합구조 자체도
         * STRONGLY_GROUNDED 후보.
         *
         * 단 불완전 원국에서는 GROUNDED 제한.
         * =================================================
         */

        boolean hasStrongSource =
                sourceFindings
                        .stream()
                        .anyMatch(
                                NatalPersonalityFinding
                                        ::stronglyGrounded
                        );

        if (
                hasStrongSource
                        && structurallyCentered
        ) {

            return completeChart

                    ? PersonalityJudgmentGroundingLevel
                    .STRONGLY_GROUNDED

                    : PersonalityJudgmentGroundingLevel
                    .GROUNDED;
        }

        /*
         * 모든 Source가 직접판정 가능하므로
         * 복합 구조도 GROUNDED.
         */
        return PersonalityJudgmentGroundingLevel
                .GROUNDED;
    }
}