package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisKind;
import com.dajeom.type.PersonalityFindingSynthesisType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Personality Finding Synthesis 호환 Aggregate.
 *
 * =================================================
 * 현재 공식 정책
 * =================================================
 *
 * 프로젝트가 둘 이상의 독립 Finding을 다시 조합해
 * 새로운 사용자 Meaning을 만드는 2차 Synthesis는 허용하지 않는다.
 *
 * 따라서 현재 공식 Pipeline에서
 * syntheses는 반드시 비어 있어야 한다.
 *
 * 이 record와 조회 API는
 * 기존 코드 호환을 위해 유지한다.
 */
public record NatalPersonalityFindingSynthesisAnalysis(

        NatalPersonalityFindingAnalysis findingAnalysis,

        List<NatalPersonalityFindingSynthesis> syntheses,

        boolean completeChart

) {

    public NatalPersonalityFindingSynthesisAnalysis {

        Objects.requireNonNull(
                findingAnalysis,
                "Personality Finding Analysis는 필수입니다."
        );

        syntheses =
                syntheses == null
                        ? List.of()
                        : List.copyOf(
                        syntheses
                );

        if (
                findingAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Finding Analysis와 Synthesis Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 공식 실행선에서 2차 Synthesis 하드 차단
         * =================================================
         */

        if (
                !syntheses.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "현재 공식 Personality Pipeline에서는 "
                            + "독립 Finding의 2차 Synthesis를 생성할 수 없습니다."
            );
        }
    }

    /**
     * 현재 공식 Pipeline에서
     * Synthesis 생성이 비활성 상태인지.
     */
    public boolean synthesisDisabled() {

        return true;
    }

    /**
     * Legacy API 호환.
     *
     * 현재 공식 실행에서는
     * 항상 빈 Optional.
     */
    public Optional<NatalPersonalityFindingSynthesis> synthesisOf(

            PersonalityFindingSynthesisType synthesisType

    ) {

        return Optional.empty();
    }

    public List<NatalPersonalityFindingSynthesis>
    directlyAssertableSyntheses() {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    stronglyGroundedSyntheses() {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    internalSyntheses() {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    synthesesInvolving(

            NatalTenGodFunctionType functionType

    ) {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    synthesesOfKind(

            PersonalityFindingSynthesisKind kind

    ) {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    structurallyCenteredSyntheses() {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    qualifiedSyntheses() {

        return List.of();
    }

    public List<NatalPersonalityFindingSynthesis>
    referenceSyntheses() {

        return List.of();
    }
}