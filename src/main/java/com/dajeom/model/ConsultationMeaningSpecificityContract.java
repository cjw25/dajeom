package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Meaning Origin마다 허용되는 Specificity를 고정하는 Contract.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 정확한 10십성이나 원국 Context가
 * 별도 deterministic 계산 없이:
 *
 * - 생활영역 결과
 * - 특정 연도 결과
 * - 현실 사건
 *
 * 으로 올라가는 것을 Claim 생성 시점에서 차단한다.
 *
 * =================================================
 * 공식 경계
 * =================================================
 *
 * EXACT_TEN_GOD
 * -> STRUCTURE / TENDENCY
 *
 * FORMAL_PATTERN
 * -> STRUCTURE / TENDENCY
 *
 * DETERMINISTIC_RULE
 * -> STRUCTURE / TENDENCY / DOMAIN
 *
 * TIMELINE_CONTRACT
 * -> TIMING
 *
 * NATAL_CONTEXT
 * -> STRUCTURE / TENDENCY
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이것은 Specificity의 점수나 서열이 아니다.
 * Origin별 허용 집합을 명시적으로 고정한 계약이다.
 */
public record ConsultationMeaningSpecificityContract(

        ConsultationMeaningOrigin origin,

        Set<ConsultationMeaningSpecificity> allowedSpecificities

) {

    public ConsultationMeaningSpecificityContract {

        Objects.requireNonNull(
                origin,
                "Meaning Specificity Contract origin은 필수입니다."
        );

        Objects.requireNonNull(
                allowedSpecificities,
                "Meaning Specificity Contract allowedSpecificities는 필수입니다."
        );

        if (
                allowedSpecificities.isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "Meaning Specificity Contract에는 최소 하나의 허용 Specificity가 필요합니다."
            );
        }

        allowedSpecificities = Set.copyOf(
                allowedSpecificities
        );
    }

    /**
     * Origin의 공식 Specificity Contract.
     */
    public static ConsultationMeaningSpecificityContract forOrigin(
            ConsultationMeaningOrigin origin
    ) {

        Objects.requireNonNull(
                origin,
                "Meaning Origin은 필수입니다."
        );

        return switch (origin) {

            case EXACT_TEN_GOD ->
                    new ConsultationMeaningSpecificityContract(
                            origin,
                            EnumSet.of(
                                    ConsultationMeaningSpecificity.STRUCTURE,
                                    ConsultationMeaningSpecificity.TENDENCY
                            )
                    );

            case FORMAL_PATTERN ->
                    new ConsultationMeaningSpecificityContract(
                            origin,
                            EnumSet.of(
                                    ConsultationMeaningSpecificity.STRUCTURE,
                                    ConsultationMeaningSpecificity.TENDENCY
                            )
                    );

            case DETERMINISTIC_RULE ->
                    new ConsultationMeaningSpecificityContract(
                            origin,
                            EnumSet.of(
                                    ConsultationMeaningSpecificity.STRUCTURE,
                                    ConsultationMeaningSpecificity.TENDENCY,
                                    ConsultationMeaningSpecificity.DOMAIN
                            )
                    );

            case TIMELINE_CONTRACT ->
                    new ConsultationMeaningSpecificityContract(
                            origin,
                            EnumSet.of(
                                    ConsultationMeaningSpecificity.TIMING
                            )
                    );

            case NATAL_CONTEXT ->
                    new ConsultationMeaningSpecificityContract(
                            origin,
                            EnumSet.of(
                                    ConsultationMeaningSpecificity.STRUCTURE,
                                    ConsultationMeaningSpecificity.TENDENCY
                            )
                    );
        };
    }

    public boolean allows(
            ConsultationMeaningSpecificity specificity
    ) {

        return specificity != null
                && allowedSpecificities.contains(
                specificity
        );
    }

    public void requireAllowed(
            ConsultationMeaningSpecificity specificity
    ) {

        Objects.requireNonNull(
                specificity,
                "Meaning Specificity는 필수입니다."
        );

        if (
                !allows(
                        specificity
                )
        ) {
            throw new IllegalArgumentException(
                    "Meaning Origin이 허용하지 않는 Specificity입니다. origin="
                            + origin
                            + ", specificity="
                            + specificity
                            + ", allowed="
                            + allowedSpecificities
            );
        }
    }

    /**
     * Timeline Origin인지.
     *
     * 단순 helper이며 새로운 판단을 만들지 않는다.
     */
    public boolean timingOnly() {

        return allowedSpecificities.size() == 1
                && allowedSpecificities.contains(
                ConsultationMeaningSpecificity.TIMING
        );
    }
}