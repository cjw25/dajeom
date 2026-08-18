package com.dajeom.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 22차.
 *
 * 하나의 ConsultationGroundingEvidence를
 * 상담문에서 어느 정도 먼저 다룰지 판단하기 위한
 * deterministic provenance 메타데이터.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이것은:
 *
 * - 사주 점수
 * - 길흉 점수
 * - 성공 확률
 * - 적중 확률
 * - confidence 확률
 *
 * 이 아니다.
 *
 * 실제 LifeDomainRuleMatch가 사용한
 * Source provenance만 이용한다.
 *
 * =================================================
 * 보존하는 정보
 * =================================================
 *
 * narrativePriority
 *     상담문 서술 순서.
 *     1이 가장 먼저 설명할 후보.
 *
 * nonAuxiliarySourceCount
 *     현재 Evidence 자체가 사용한
 *     독립 비보조 SourceKey 수.
 *
 * nonAuxiliarySourceTypeCount
 *     현재 Evidence가 사용한
 *     비보조 Source 종류 수.
 *
 * sameDirectionRuleCount
 *     같은 Slice 안에서
 *     같은 Domain + 같은 Status를 가진
 *     서로 다른 Rule 수.
 *
 * sameDirectionSourceCount
 *     위 같은 방향 Rule들이 함께 사용한
 *     독립 비보조 SourceKey 총수.
 *
 * auxiliarySourceCount
 *     현재 Evidence가 참고한 AUXILIARY Source 수.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * 이 숫자들을 더해서 하나의 종합점수를 만들지 않는다.
 *
 * SUPPORTIVE와 PRESSURE를 비교해서
 * 어느 쪽이 더 좋은지 결정하지 않는다.
 *
 * 반대 방향 Evidence를 삭제하지 않는다.
 */
public record ConsultationGroundingProminence(

        int narrativePriority,

        int nonAuxiliarySourceCount,

        int nonAuxiliarySourceTypeCount,

        int sameDirectionRuleCount,

        int sameDirectionSourceCount,

        int auxiliarySourceCount

) {

    public ConsultationGroundingProminence {

        if (
                narrativePriority < 0
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence narrativePriority는 0 이상이어야 합니다."
            );
        }

        if (
                nonAuxiliarySourceCount < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence에는 최소 하나의 비보조 Source가 필요합니다."
            );
        }

        if (
                nonAuxiliarySourceTypeCount < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence에는 최소 하나의 비보조 Source Type이 필요합니다."
            );
        }

        if (
                nonAuxiliarySourceTypeCount
                        >
                        nonAuxiliarySourceCount
        ) {

            throw new IllegalArgumentException(
                    "비보조 Source Type 수는 비보조 Source 수를 초과할 수 없습니다."
            );
        }

        if (
                sameDirectionRuleCount < 1
        ) {

            throw new IllegalArgumentException(
                    "같은 방향 Rule 수는 최소 1이어야 합니다."
            );
        }

        if (
                sameDirectionSourceCount
                        <
                        nonAuxiliarySourceCount
        ) {

            throw new IllegalArgumentException(
                    "같은 방향 Source 수는 현재 Evidence의 비보조 Source 수보다 작을 수 없습니다."
            );
        }

        if (
                auxiliarySourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "AUXILIARY Source 수는 0 이상이어야 합니다."
            );
        }
    }

    /**
     * 아직 전체 Slice 안에서 순위가 계산되지 않은
     * Evidence에 사용하는 기본 prominence.
     *
     * narrativePriority == 0
     *
     * 은 미정 상태를 의미한다.
     */
    public static ConsultationGroundingProminence unranked(

            List<ConsultationGroundingSource> sources

    ) {

        Objects.requireNonNull(
                sources,
                "Grounding Prominence 계산에는 Source가 필요합니다."
        );

        Set<String> nonAuxiliaryKeys =
                new LinkedHashSet<>();

        Set<String> nonAuxiliaryTypes =
                new LinkedHashSet<>();

        int auxiliaryCount =
                0;

        for (
                ConsultationGroundingSource source
                : sources
        ) {

            Objects.requireNonNull(
                    source,
                    "Grounding Source는 null일 수 없습니다."
            );

            if (
                    source.auxiliary()
            ) {

                auxiliaryCount++;

                continue;
            }

            nonAuxiliaryKeys.add(
                    source.sourceKey()
            );

            nonAuxiliaryTypes.add(
                    String.valueOf(
                            source.sourceType()
                    )
            );
        }

        if (
                nonAuxiliaryKeys.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "AUXILIARY Source만으로 Grounding Prominence를 만들 수 없습니다."
            );
        }

        return new ConsultationGroundingProminence(
                0,
                nonAuxiliaryKeys.size(),
                nonAuxiliaryTypes.size(),
                1,
                nonAuxiliaryKeys.size(),
                auxiliaryCount
        );
    }

    public boolean ranked() {

        return narrativePriority > 0;
    }

    public boolean multipleDirectSources() {

        return nonAuxiliarySourceCount > 1;
    }

    public boolean diverseDirectSourceTypes() {

        return nonAuxiliarySourceTypeCount > 1;
    }

    public boolean repeatedAcrossRules() {

        return sameDirectionRuleCount > 1;
    }

    public boolean expandedSameDirectionEvidence() {

        return sameDirectionSourceCount
                >
                nonAuxiliarySourceCount;
    }

    public boolean hasAuxiliarySource() {

        return auxiliarySourceCount > 0;
    }
}