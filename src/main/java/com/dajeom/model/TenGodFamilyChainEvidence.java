package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.TenGodFamilyChainReasonType;
import com.dajeom.type.TenGodFamilyChainType;
import com.dajeom.type.TenGodFamilyInteractionLevel;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-7.
 *
 * 복수의 생 관계가 연속으로 이어진
 * Family Chain Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * OUTPUT
 *   ↓
 * WEALTH
 *   ↓
 * OFFICER
 *   ↓
 * RESOURCE
 *
 * =================================================
 *
 * chainLevel은 연쇄 중 가장 약한 관계를
 * 기준으로 결정한다.
 *
 * 즉:
 *
 * STRONG
 * STRONG
 * MEANINGFUL
 *
 * 이라면 전체 Chain은
 *
 * MEANINGFUL
 *
 * 이다.
 *
 * 임의의 평균 점수를 사용하지 않는다.
 */
public record TenGodFamilyChainEvidence(

        TenGodFamilyChainType chainType,

        TenGodFamilyInteractionLevel chainLevel,

        List<TenGodFamilyInteractionEvidence> interactions,

        List<TenGodFamilyChainReasonType> reasons

) {

    public TenGodFamilyChainEvidence {

        Objects.requireNonNull(
                chainType,
                "십성 Family 연쇄 종류가 필요합니다."
        );

        Objects.requireNonNull(
                chainLevel,
                "십성 Family 연쇄 수준이 필요합니다."
        );

        interactions =
                interactions == null
                        ? List.of()
                        : List.copyOf(
                        interactions
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                interactions.size()
                        != chainType.interactionCount()
        ) {

            throw new IllegalArgumentException(
                    "연쇄 종류와 Interaction 개수가 일치하지 않습니다."
            );
        }

        for (
                int index = 0;
                index < interactions.size();
                index++
        ) {

            TenGodFamilyInteractionEvidence interaction =
                    interactions.get(index);

            DayMasterElementRole expectedSource =
                    chainType.roles()
                            .get(index);

            DayMasterElementRole expectedTarget =
                    chainType.roles()
                            .get(index + 1);

            if (
                    !interaction.generation()
                            || interaction.sourceRole()
                            != expectedSource
                            || interaction.targetRole()
                            != expectedTarget
            ) {

                throw new IllegalArgumentException(
                        "Family Chain의 생 관계 순서가 chainType과 일치하지 않습니다."
                );
            }
        }

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Family Chain에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public boolean meaningful() {

        return chainLevel
                .effective();
    }

    public boolean strong() {

        return chainLevel
                .strong();
    }

    public int interactionCount() {

        return interactions.size();
    }

    public List<DayMasterElementRole> roles() {

        return chainType
                .roles();
    }

    public boolean includes(
            DayMasterElementRole role
    ) {

        return chainType
                .includes(
                        role
                );
    }

    public boolean allInteractionsMeaningful() {

        return interactions
                .stream()
                .allMatch(
                        TenGodFamilyInteractionEvidence
                                ::meaningful
                );
    }

    public boolean allInteractionsStrong() {

        return interactions
                .stream()
                .allMatch(
                        TenGodFamilyInteractionEvidence
                                ::strong
                );
    }

    public boolean hasVisibleFamily() {

        return interactions
                .stream()
                .anyMatch(
                        interaction ->
                                interaction.sourceVisible()
                                        || interaction.targetVisible()
                );
    }

    public boolean hasMonthAnchor() {

        return interactions
                .stream()
                .anyMatch(
                        interaction ->
                                interaction.sourceMonthPresent()
                                        || interaction.targetMonthPresent()
                );
    }

    public boolean hasReason(
            TenGodFamilyChainReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}