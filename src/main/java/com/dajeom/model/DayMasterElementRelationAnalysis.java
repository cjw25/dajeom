package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 4차-1.
 *
 * 일간 기준 오행 관계 분석 결과.
 *
 * 木火土金水를:
 *
 * - 비겁
 * - 인성
 * - 식상
 * - 재성
 * - 관살
 *
 * 중 하나로 배치한다.
 *
 * 아직 신강/신약을 최종 판정하지 않는다.
 */
public record DayMasterElementRelationAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        List<DayMasterElementRelation> relations,

        Map<FiveElement, DayMasterElementRelation> relationsByElement,

        Map<DayMasterElementRole, FiveElement> elementByRole

) {

    public DayMasterElementRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(
                        relations
                );

        if (
                relations.size() != 5
        ) {

            throw new IllegalArgumentException(
                    "일간 기준 관계는 오행 5개를 모두 포함해야 합니다."
            );
        }

        relationsByElement =
                immutableRelationsByElement(
                        relationsByElement
                );

        elementByRole =
                immutableElementByRole(
                        elementByRole
                );

        /*
         * 오행 5개가 모두 한 번씩 존재하는지.
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            if (
                    !relationsByElement
                            .containsKey(
                                    element
                            )
            ) {

                throw new IllegalArgumentException(
                        "일간 관계에서 오행이 누락되었습니다: "
                                + element
                );
            }
        }

        /*
         * 비겁/인성/식상/재성/관살
         * 5개 역할이 모두 하나씩 있어야 한다.
         */
        for (
                DayMasterElementRole role
                : DayMasterElementRole.values()
        ) {

            if (
                    !elementByRole
                            .containsKey(
                                    role
                            )
            ) {

                throw new IllegalArgumentException(
                        "일간 기준 십성 그룹이 누락되었습니다: "
                                + role
                );
            }
        }

        FiveElement companionElement =
                elementByRole.get(
                        DayMasterElementRole.COMPANION
                );

        if (
                companionElement
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "비겁 오행은 일간 오행과 같아야 합니다."
            );
        }
    }

    private static Map<
            FiveElement,
            DayMasterElementRelation
            >
    immutableRelationsByElement(
            Map<
                    FiveElement,
                    DayMasterElementRelation
                    > source
    ) {

        EnumMap<
                FiveElement,
                DayMasterElementRelation
                > result =
                new EnumMap<>(
                        FiveElement.class
                );

        if (
                source != null
        ) {

            result.putAll(
                    source
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    private static Map<
            DayMasterElementRole,
            FiveElement
            >
    immutableElementByRole(
            Map<
                    DayMasterElementRole,
                    FiveElement
                    > source
    ) {

        EnumMap<
                DayMasterElementRole,
                FiveElement
                > result =
                new EnumMap<>(
                        DayMasterElementRole.class
                );

        if (
                source != null
        ) {

            result.putAll(
                    source
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    public Optional<DayMasterElementRelation>
    relationOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                relationsByElement.get(
                        element
                )
        );
    }

    public Optional<DayMasterElementRelation>
    relationOf(
            DayMasterElementRole role
    ) {

        if (
                role == null
        ) {

            return Optional.empty();
        }

        FiveElement element =
                elementByRole.get(
                        role
                );

        if (
                element == null
        ) {

            return Optional.empty();
        }

        return relationOf(
                element
        );
    }

    public FiveElement companionElement() {

        return elementByRole.get(
                DayMasterElementRole.COMPANION
        );
    }

    public FiveElement resourceElement() {

        return elementByRole.get(
                DayMasterElementRole.RESOURCE
        );
    }

    public FiveElement outputElement() {

        return elementByRole.get(
                DayMasterElementRole.OUTPUT
        );
    }

    public FiveElement wealthElement() {

        return elementByRole.get(
                DayMasterElementRole.WEALTH
        );
    }

    public FiveElement officerElement() {

        return elementByRole.get(
                DayMasterElementRole.OFFICER
        );
    }

    public DayMasterElementRelation companion() {

        return requireRole(
                DayMasterElementRole.COMPANION
        );
    }

    public DayMasterElementRelation resource() {

        return requireRole(
                DayMasterElementRole.RESOURCE
        );
    }

    public DayMasterElementRelation output() {

        return requireRole(
                DayMasterElementRole.OUTPUT
        );
    }

    public DayMasterElementRelation wealth() {

        return requireRole(
                DayMasterElementRole.WEALTH
        );
    }

    public DayMasterElementRelation officer() {

        return requireRole(
                DayMasterElementRole.OFFICER
        );
    }

    /**
     * 역할에 해당하는 오행의
     * 3차 최종 세력.
     *
     * 아직 이 값을 단순 합산해서
     * 신강/신약으로 판정하지 않는다.
     */
    public int powerOf(
            DayMasterElementRole role
    ) {

        return requireRole(
                role
        ).elementPower();
    }

    private DayMasterElementRelation requireRole(
            DayMasterElementRole role
    ) {

        Objects.requireNonNull(
                role,
                "일간 기준 역할이 필요합니다."
        );

        return relationOf(
                role
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "일간 기준 역할을 찾을 수 없습니다: "
                                                + role
                                )
                );
    }
}