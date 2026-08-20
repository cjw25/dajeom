package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementFactType;
import com.dajeom.type.PersonalityStatementSelectionReason;
import com.dajeom.type.PersonalityStatementSelectionRole;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 Personality Statement Fact가
 * 최종 설명에서 어떤 역할을 맡는지 나타낸다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Fact 자체를 삭제하지 않는다.
 *
 * Selection Role만 결정한다.
 *
 * =================================================
 *
 * 예:
 *
 * 강한 Synthesis
 * =
 * PRIMARY
 *
 * 그 Synthesis에 포함된
 * 강한 Function
 * =
 * SUPPORTING
 *
 * 흡수된 개별 Cross Pattern
 * =
 * INTERNAL_REFERENCE
 *
 * =================================================
 *
 * coveredByDirectSynthesis
 *
 * Function Fact가
 * 직접 사용자 출력 가능한 Synthesis에
 * 이미 참여하고 있는지를 나타낸다.
 *
 * true라고 Function Evidence가
 * 사라지는 것은 아니다.
 */
public record NatalPersonalityStatementSelection(

        NatalPersonalityStatementFact fact,

        PersonalityStatementSelectionRole role,

        List<PersonalityStatementSelectionReason> reasons,

        boolean coveredByDirectSynthesis,

        boolean completeChart

) {

    public NatalPersonalityStatementSelection {

        Objects.requireNonNull(
                fact,
                "Personality Statement Fact는 필수입니다."
        );

        Objects.requireNonNull(
                role,
                "Personality Statement Selection Role은 필수입니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Personality Statement Selection에는 "
                            + "최소 하나의 선택 이유가 필요합니다."
            );
        }

        if (
                fact.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Statement Fact와 Selection의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 선택 이유 중복 금지
         * =================================================
         */

        Set<PersonalityStatementSelectionReason> reasonSet =
                new LinkedHashSet<>();

        for (
                PersonalityStatementSelectionReason reason
                : reasons
        ) {

            Objects.requireNonNull(
                    reason,
                    "Selection Reason에는 null을 사용할 수 없습니다."
            );

            if (
                    !reasonSet.add(
                            reason
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Selection Reason이 중복되었습니다: "
                                + reason
                );
            }
        }

        /*
         * =================================================
         * 직접 출력 Role 검증
         * =================================================
         */

        if (
                role.directOutput()

                        && !fact
                        .userFacingCandidate()
        ) {

            throw new IllegalArgumentException(
                    "직접 사용자 출력이 불가능한 Fact를 "
                            + "PRIMARY/SUPPORTING으로 선택할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Synthesis에 실제 흡수된 Fact는
         * 직접 출력 Role 금지
         * =================================================
         */

        if (
                fact.absorbedByDirectSynthesis()

                        && role.directOutput()
        ) {

            throw new IllegalArgumentException(
                    "상위 Synthesis에 흡수된 Fact를 "
                            + "직접 출력 Role로 선택할 수 없습니다."
            );
        }

        /*
         * =================================================
         * coveredByDirectSynthesis는
         * FUNCTION Fact에만 사용한다.
         *
         * Cross Pattern의 실제 absorption과는
         * 의미가 다르다.
         * =================================================
         */

        if (
                coveredByDirectSynthesis

                        && fact.factType()
                        != PersonalityStatementFactType
                        .FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "coveredByDirectSynthesis는 "
                            + "FUNCTION Fact에만 사용할 수 있습니다."
            );
        }
    }

    public boolean primary() {

        return role
                == PersonalityStatementSelectionRole
                .PRIMARY;
    }

    public boolean supporting() {

        return role
                == PersonalityStatementSelectionRole
                .SUPPORTING;
    }

    public boolean backgroundContext() {

        return role
                == PersonalityStatementSelectionRole
                .BACKGROUND_CONTEXT;
    }

    public boolean internalReference() {

        return role
                == PersonalityStatementSelectionRole
                .INTERNAL_REFERENCE;
    }

    /**
     * 실제 사용자 설명에 사용할 Selection.
     */
    public boolean directOutput() {

        return role
                .directOutput();
    }

    public boolean qualificationRequired() {

        return fact
                .qualificationRequired();
    }

    public List<NatalTenGodFunctionType>
    involvedFunctions() {

        return fact
                .involvedFunctions();
    }
}