package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 기본성향의 최종 구조화 Finding 하나.
 *
 * =================================================
 *
 * 이 객체는:
 *
 * "무슨 성향 근거가 존재하는가"
 *
 * 를 보존한다.
 *
 * =================================================
 *
 * 아직:
 *
 * - 사용자용 문장
 * - 감성적 표현
 * - 사건 예측
 *
 * 은 만들지 않는다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * REFERENCE Finding도 삭제하지 않는다.
 *
 * 예:
 *
 * groundingLevel = REFERENCE
 * directAssertionAllowed = false
 *
 * 로 내부에 그대로 남는다.
 *
 * =================================================
 *
 * 따라서:
 *
 * 약한 근거가 이후 다른 분석축과
 * 교차검증될 때 다시 사용할 수 있다.
 */
public record NatalPersonalityFinding(

        PersonalityFindingTheme theme,

        PersonalityFindingOriginType originType,

        List<NatalTenGodFunctionType>
        involvedFunctions,

        Optional<NatalResponseSituationType>
        situationType,

        Optional<NatalCrossFunctionalPatternType>
        crossPatternType,

        PersonalityJudgmentGroundingLevel
        groundingLevel,

        List<NatalPersonalityJudgmentEvidence>
        evidences,

        boolean directAssertionAllowed,

        boolean qualificationRequired,

        boolean completeChart

) {

    public NatalPersonalityFinding {

        Objects.requireNonNull(
                theme,
                "Personality Finding Theme은 필수입니다."
        );

        Objects.requireNonNull(
                originType,
                "Personality Finding Origin은 필수입니다."
        );

        involvedFunctions =
                involvedFunctions == null
                        ? List.of()
                        : List.copyOf(
                        involvedFunctions
                );

        situationType =
                situationType == null
                        ? Optional.empty()
                        : situationType;

        crossPatternType =
                crossPatternType == null
                        ? Optional.empty()
                        : crossPatternType;

        Objects.requireNonNull(
                groundingLevel,
                "Personality Finding Grounding Level은 필수입니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * Origin별 구조 검증
         * =================================================
         */

        switch (
                originType
        ) {

            case CORE_SELF -> {

                if (
                        !involvedFunctions.isEmpty()
                                || situationType.isPresent()
                                || crossPatternType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "CORE_SELF Finding에는 "
                                    + "기능축/상황/CrossPattern을 "
                                    + "지정하지 않습니다."
                    );
                }
            }

            case FUNCTION -> {

                if (
                        involvedFunctions.size() != 1
                                || situationType.isPresent()
                                || crossPatternType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "FUNCTION Finding에는 "
                                    + "정확히 하나의 기능축만 필요합니다."
                    );
                }
            }

            case CROSS_PATTERN -> {

                if (
                        involvedFunctions.isEmpty()
                                || crossPatternType.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "CROSS_PATTERN Finding에는 "
                                    + "기능축과 CrossPattern이 필요합니다."
                    );
                }
            }

            case INTERPERSONAL_CONTEXT -> {

                if (
                        !involvedFunctions.isEmpty()
                                || situationType.isPresent()
                                || crossPatternType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "INTERPERSONAL_CONTEXT Finding에는 "
                                    + "기능축/상황/CrossPattern을 "
                                    + "지정하지 않습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * Direct Assertion 검증
         * =================================================
         */

        if (
                directAssertionAllowed

                        && !groundingLevel
                        .directAssertionAllowed()
        ) {

            throw new IllegalArgumentException(
                    "REFERENCE 또는 CONTEXTUAL Finding을 "
                            + "직접 단정용으로 사용할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Strong Grounding 검증
         * =================================================
         */

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED

                        && evidences
                        .stream()
                        .noneMatch(
                                NatalPersonalityJudgmentEvidence
                                        ::strong
                        )
        ) {

            throw new IllegalArgumentException(
                    "STRONGLY_GROUNDED Finding에는 "
                            + "최소 하나의 Strong Evidence가 필요합니다."
            );
        }
    }

    /**
     * 사용자에게 직접 표현할 후보인가.
     */
    public boolean userFacingCandidate() {

        return directAssertionAllowed;
    }

    /**
     * 내부에는 남지만
     * 단독으로 사용자에게 강하게
     * 말하지 않는 Finding.
     */
    public boolean internalReferenceOnly() {

        return !directAssertionAllowed;
    }

    public boolean stronglyGrounded() {

        return groundingLevel
                == PersonalityJudgmentGroundingLevel
                .STRONGLY_GROUNDED;
    }
}