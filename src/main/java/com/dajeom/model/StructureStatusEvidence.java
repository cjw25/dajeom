package com.dajeom.model;

import com.dajeom.type.StructureStatus;

import java.util.Objects;

/**
 * 정밀화 5차-10.
 *
 * 최종 StructureStatus가
 * 어떤 근거로 결정됐는지 보존한다.
 *
 * 주의:
 *
 * 이 Evidence는 기존 5-1~5-9 결과를
 * 다시 계산하지 않는다.
 *
 * PRIMARY 후보에 대해:
 *
 * - 성립도
 * - 강약 적합성
 * - 성격
 * - 파격
 * - 순도
 *
 * 의 핵심 상태만 최종적으로 요약한다.
 */
public record StructureStatusEvidence(

        StructureSelectionCandidate primaryCandidate,

        StructureStatus status,

        boolean meaningfulEstablishment,

        boolean strongEstablishment,

        boolean balanceAcceptable,

        boolean balanceBurdened,

        boolean meaningfulFormationSupport,

        boolean strongFormationSupport,

        boolean meaningfulDamage,

        boolean strongDamage,

        boolean relativelyPure,

        boolean highlyMixed,

        int secondaryCandidateCount,

        int deferredCandidateCount

) {

    public StructureStatusEvidence {

        Objects.requireNonNull(
                primaryCandidate,
                "주격 후보가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "최종 격 상태가 필요합니다."
        );

        if (
                !primaryCandidate.primary()
        ) {

            throw new IllegalArgumentException(
                    "StructureStatusEvidence는 PRIMARY 후보를 기준으로 생성해야 합니다."
            );
        }

        if (
                secondaryCandidateCount < 0
                        || deferredCandidateCount < 0
        ) {

            throw new IllegalArgumentException(
                    "후보 개수는 음수가 될 수 없습니다."
            );
        }

        if (
                strongEstablishment
                        && !meaningfulEstablishment
        ) {

            throw new IllegalArgumentException(
                    "강한 격 성립은 의미 있는 격 성립을 포함해야 합니다."
            );
        }

        if (
                strongFormationSupport
                        && !meaningfulFormationSupport
        ) {

            throw new IllegalArgumentException(
                    "강한 성격 요소는 의미 있는 성격 요소를 포함해야 합니다."
            );
        }

        if (
                strongDamage
                        && !meaningfulDamage
        ) {

            throw new IllegalArgumentException(
                    "강한 파격은 의미 있는 파격을 포함해야 합니다."
            );
        }

        if (
                balanceAcceptable
                        && balanceBurdened
        ) {

            throw new IllegalArgumentException(
                    "강약 적합과 강약 부담은 동시에 true일 수 없습니다."
            );
        }

        if (
                relativelyPure
                        && highlyMixed
        ) {

            throw new IllegalArgumentException(
                    "비교적 순수와 강한 혼잡은 동시에 true일 수 없습니다."
            );
        }

        /*
         * 특수격 PRIMARY는
         * 일반 내격의 성립/성격/파격 지표를
         * 억지로 적용하지 않는다.
         */
        if (
                primaryCandidate.special()
        ) {

            if (
                    !status.specialReview()
            ) {

                throw new IllegalArgumentException(
                        "특수격 PRIMARY의 최종 상태는 SPECIAL_REVIEW여야 합니다."
                );
            }

            if (
                    meaningfulEstablishment
                            || strongEstablishment
                            || balanceAcceptable
                            || balanceBurdened
                            || meaningfulFormationSupport
                            || strongFormationSupport
                            || meaningfulDamage
                            || strongDamage
                            || relativelyPure
                            || highlyMixed
            ) {

                throw new IllegalArgumentException(
                        "특수격 PRIMARY에 일반 내격 상태값을 적용할 수 없습니다."
                );
            }

        } else {

            if (
                    status.specialReview()
            ) {

                throw new IllegalArgumentException(
                        "일반격 PRIMARY를 SPECIAL_REVIEW 상태로 만들 수 없습니다."
                );
            }
        }
    }

    public boolean standardPrimary() {

        return primaryCandidate
                .standard();
    }

    public boolean specialPrimary() {

        return primaryCandidate
                .special();
    }

    public boolean hasSecondaryCandidate() {

        return secondaryCandidateCount > 0;
    }

    public boolean hasDeferredCandidate() {

        return deferredCandidateCount > 0;
    }

    public boolean structurallyClean() {

        return standardPrimary()
                && meaningfulEstablishment
                && !meaningfulDamage
                && !highlyMixed;
    }

    public boolean damagedButSupported() {

        return standardPrimary()
                && meaningfulDamage
                && meaningfulFormationSupport;
    }
}