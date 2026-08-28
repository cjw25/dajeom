package com.dajeom.engine;

import com.dajeom.constant.NatalPersonalityFormalPatternMeaningPolicyRegistry;
import com.dajeom.model.NatalPersonalityFinding;
import com.dajeom.model.NatalPersonalityFormalPatternMeaning;
import com.dajeom.model.NatalPersonalityFormalPatternMeaningPolicy;
import com.dajeom.model.NatalPersonalityStatementFact;
import com.dajeom.model.NatalPersonalityStatementSelection;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityStatementFactType;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Personality Statement Selection에서
 * 실제 전통 Formal Pattern만 사용자 Meaning으로 변환한다.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 다음 정보만으로 Pattern Meaning을 만들지 않는다.
 *
 * - Finding Theme 문자열
 * - involvedFunctions
 * - 5개 기능축 동시 존재
 * - 두 기능축의 co-existence
 *
 * =================================================
 * 반드시 필요한 공식 Provenance
 * =================================================
 *
 * Selection
 * ->
 * CROSS_PATTERN Statement Fact
 * ->
 * 단일 Source Finding
 * ->
 * CROSS_PATTERN Finding Origin
 * ->
 * 실제 NatalCrossFunctionalPatternType
 * ->
 * 실제 TenGodCorePatternType
 * ->
 * 등록된 Formal Pattern Meaning Policy
 *
 * =================================================
 * Reference / Contextual
 * =================================================
 *
 * directOutput이 아닌 Selection은
 * 사용자 Meaning으로 만들지 않는다.
 *
 * 내부 Evidence는 기존 Pipeline에 그대로 남는다.
 */
@Component
public class NatalPersonalityFormalPatternMeaningEngine {

    public Optional<NatalPersonalityFormalPatternMeaning> analyze(

            NatalPersonalityStatementSelection selection

    ) {

        if (
                selection == null
                        ||
                        !selection.directOutput()
        ) {

            return Optional.empty();
        }

        NatalPersonalityStatementFact fact =
                Objects.requireNonNull(
                        selection.fact(),
                        "Formal Pattern Meaning 분석에는 Statement Fact가 필요합니다."
                );

        /*
         * =================================================
         * CROSS_PATTERN만 대상
         * =================================================
         */

        if (
                fact.factType()
                        != PersonalityStatementFactType.CROSS_PATTERN
        ) {

            return Optional.empty();
        }

        /*
         * =================================================
         * Source Finding
         * =================================================
         */

        if (
                fact.sourceFindings().size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "CROSS_PATTERN Statement Fact의 Source Finding 수가 1이 아닙니다."
            );
        }

        NatalPersonalityFinding finding =
                fact.sourceFindings()
                        .get(0);

        if (
                finding.originType()
                        != PersonalityFindingOriginType.CROSS_PATTERN
        ) {

            throw new IllegalStateException(
                    "CROSS_PATTERN Statement Fact의 Source Finding Origin이 CROSS_PATTERN이 아닙니다."
            );
        }

        /*
         * =================================================
         * 내부 Reference는 사용자 출력 금지
         * =================================================
         */

        if (
                !finding.userFacingCandidate()
        ) {

            return Optional.empty();
        }

        /*
         * =================================================
         * 실제 Pattern Type
         * =================================================
         */

        NatalCrossFunctionalPatternType crossPatternType =
                finding
                        .crossPatternType()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "CROSS_PATTERN Finding에 실제 NatalCrossFunctionalPatternType이 없습니다."
                                        )
                        );

        /*
         * =================================================
         * 전통 Pattern Registry
         * =================================================
         *
         * 여기 등록되지 않은 Pattern은
         * 사용자 Formal Pattern Meaning으로 올라갈 수 없다.
         */

        NatalPersonalityFormalPatternMeaningPolicy policy =
                NatalPersonalityFormalPatternMeaningPolicyRegistry
                        .require(
                                crossPatternType
                                        .sourcePatternType()
                        );

        return Optional.of(
                new NatalPersonalityFormalPatternMeaning(
                        fact,
                        finding,
                        crossPatternType,
                        policy,
                        fact.strongUserFacingCandidate(),
                        fact.qualificationRequired()
                )
        );
    }
}