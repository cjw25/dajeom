package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchPunishmentForm;
import com.dajeom.type.EarthlyBranchPunishmentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-7.
 *
 * 원국에서 실제 발견된 하나의
 * 지지 형(刑) Evidence.
 *
 * =================================================
 *
 * COMPLETE_THREE
 *
 * 세 위치 / 세 지지를 사용.
 *
 * =================================================
 *
 * PARTIAL_THREE
 *
 * 삼형 계열의 두 구성원.
 *
 * =================================================
 *
 * MUTUAL_TWO
 *
 * 子卯.
 *
 * =================================================
 *
 * SELF
 *
 * 辰辰
 * 午午
 * 酉酉
 * 亥亥
 *
 * 서로 다른 두 궁위에서 동일 지지가
 * 실제로 반복되어야 생성한다.
 *
 * =================================================
 *
 * coveredByCompletePunishment
 *
 * PARTIAL_THREE Evidence가
 * 동일 위치의 완전 삼형 내부에 포함되었는지.
 *
 * 완전 삼형이 존재해도 내부 Pair Evidence는
 * 삭제하지 않는다.
 *
 * 대신 이후 단계에서 중복 해석하지 않도록
 * 종속 상태를 표시한다.
 */
public record EarthlyBranchPunishmentEvidence(

        EarthlyBranchPunishmentType punishmentType,

        EarthlyBranchPunishmentForm form,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        EarthlyBranchPosition thirdPosition,

        EarthlyBranch thirdBranch,

        boolean coveredByCompletePunishment

) {

    public EarthlyBranchPunishmentEvidence {

        Objects.requireNonNull(
                punishmentType,
                "지지 형 유형이 필요합니다."
        );

        Objects.requireNonNull(
                form,
                "지지 형 구성 형태가 필요합니다."
        );

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                firstBranch,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondBranch,
                "두 번째 지지가 필요합니다."
        );

        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "지지 형 Evidence는 원국 위치 순서대로 저장해야 합니다."
            );
        }

        /*
         * =================================================
         * 완전 삼형
         * =================================================
         */
        if (
                form.completeThree()
        ) {

            Objects.requireNonNull(
                    thirdPosition,
                    "완전 삼형에는 세 번째 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    thirdBranch,
                    "완전 삼형에는 세 번째 지지가 필요합니다."
            );

            if (
                    !punishmentType.threePunishment()
            ) {

                throw new IllegalArgumentException(
                        "COMPLETE_THREE는 삼형 유형에서만 사용할 수 있습니다."
                );
            }

            if (
                    secondPosition.ordinal()
                            >= thirdPosition.ordinal()
            ) {

                throw new IllegalArgumentException(
                        "완전 삼형의 세 위치는 원국 순서대로 저장해야 합니다."
                );
            }

            if (
                    !punishmentType
                            .matchesCompleteThree(
                                    firstBranch,
                                    secondBranch,
                                    thirdBranch
                            )
            ) {

                throw new IllegalArgumentException(
                        "세 지지가 지정된 완전 삼형 유형과 일치하지 않습니다."
                );
            }

            if (
                    coveredByCompletePunishment
            ) {

                throw new IllegalArgumentException(
                        "완전 삼형 Evidence 자체는 coveredByCompletePunishment가 될 수 없습니다."
                );
            }

        } else {

            /*
             * Pair 형에서는
             * 세 번째 구성원을 사용하지 않는다.
             */
            if (
                    thirdPosition != null
                            || thirdBranch != null
            ) {

                throw new IllegalArgumentException(
                        "Pair 형 Evidence에는 세 번째 지지를 지정할 수 없습니다."
                );
            }

            /*
             * =================================================
             * 부분 삼형
             * =================================================
             */
            if (
                    form.partialThree()
            ) {

                if (
                        !punishmentType.threePunishment()
                                || !punishmentType
                                .matchesPartialThree(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    throw new IllegalArgumentException(
                            "PARTIAL_THREE와 삼형 지지 Pair가 일치하지 않습니다."
                    );
                }

                /*
                 * =================================================
                 * 子卯 상형
                 * =================================================
                 */
            } else if (
                    form.mutualTwo()
            ) {

                if (
                        !punishmentType.mutualPunishment()
                                || !punishmentType
                                .matchesMutual(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    throw new IllegalArgumentException(
                            "MUTUAL_TWO는 子卯 상형 관계여야 합니다."
                    );
                }

                if (
                        coveredByCompletePunishment
                ) {

                    throw new IllegalArgumentException(
                            "子卯 상형은 완전 삼형의 종속 Pair가 아닙니다."
                    );
                }

                /*
                 * =================================================
                 * 자형
                 * =================================================
                 */
            } else if (
                    form.selfPunishment()
            ) {

                if (
                        !punishmentType.selfPunishment()
                                || !punishmentType
                                .matchesSelf(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    throw new IllegalArgumentException(
                            "SELF 형과 자형 지지가 일치하지 않습니다."
                    );
                }

                if (
                        firstBranch
                                != secondBranch
                ) {

                    throw new IllegalArgumentException(
                            "자형은 서로 다른 궁위의 동일 지지끼리만 성립합니다."
                    );
                }

                if (
                        coveredByCompletePunishment
                ) {

                    throw new IllegalArgumentException(
                            "자형은 완전 삼형의 종속 Pair가 아닙니다."
                    );
                }
            }
        }
    }

    public int memberCount() {

        return form.completeThree()
                ? 3
                : 2;
    }

    public List<EarthlyBranchPosition> positions() {

        List<EarthlyBranchPosition> positions =
                new ArrayList<>();

        positions.add(
                firstPosition
        );

        positions.add(
                secondPosition
        );

        if (
                thirdPosition != null
        ) {

            positions.add(
                    thirdPosition
            );
        }

        return List.copyOf(
                positions
        );
    }

    public List<EarthlyBranch> branches() {

        List<EarthlyBranch> branches =
                new ArrayList<>();

        branches.add(
                firstBranch
        );

        branches.add(
                secondBranch
        );

        if (
                thirdBranch != null
        ) {

            branches.add(
                    thirdBranch
            );
        }

        return List.copyOf(
                branches
        );
    }

    public boolean completeThreePunishment() {

        return form.completeThree();
    }

    public boolean partialThreePunishment() {

        return form.partialThree();
    }

    public boolean mutualPunishment() {

        return form.mutualTwo();
    }

    public boolean selfPunishment() {

        return form.selfPunishment();
    }

    public boolean involves(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return positions()
                .contains(
                        position
                );
    }

    public boolean involves(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return false;
        }

        return branches()
                .contains(
                        branch
                );
    }

    public boolean dayBranchInvolved() {

        return involves(
                EarthlyBranchPosition.DAY
        );
    }

    public boolean monthBranchInvolved() {

        return involves(
                EarthlyBranchPosition.MONTH
        );
    }

    public boolean yearBranchInvolved() {

        return involves(
                EarthlyBranchPosition.YEAR
        );
    }

    public boolean hourBranchInvolved() {

        return involves(
                EarthlyBranchPosition.HOUR
        );
    }

    public int positionSpan() {

        int minimum =
                positions()
                        .stream()
                        .mapToInt(
                                Enum::ordinal
                        )
                        .min()
                        .orElseThrow();

        int maximum =
                positions()
                        .stream()
                        .mapToInt(
                                Enum::ordinal
                        )
                        .max()
                        .orElseThrow();

        return maximum - minimum;
    }

    public boolean compact() {

        return positionSpan()
                == memberCount() - 1;
    }

    /**
     * 완전 삼형 내부에 포함된
     * PARTIAL_THREE Evidence인가.
     */
    public boolean subordinateToCompletePunishment() {

        return form.partialThree()
                && coveredByCompletePunishment;
    }

    /**
     * 완전 삼형에 포함되지 않은
     * 독립적인 부분 삼형인가.
     */
    public boolean standalonePartialThree() {

        return form.partialThree()
                && !coveredByCompletePunishment;
    }

    /**
     * PARTIAL_THREE일 때
     * 형의 방향상 먼저 작용하는 지지.
     *
     * 子卯 상형과 자형은 단방향으로 축약하지 않으므로
     * null을 반환한다.
     */
    public EarthlyBranch punisherBranch() {

        if (
                !form.partialThree()
        ) {

            return null;
        }

        if (
                punishmentType.punishes(
                        firstBranch,
                        secondBranch
                )
        ) {

            return firstBranch;
        }

        if (
                punishmentType.punishes(
                        secondBranch,
                        firstBranch
                )
        ) {

            return secondBranch;
        }

        return null;
    }

    public EarthlyBranch punishedBranch() {

        EarthlyBranch punisher =
                punisherBranch();

        if (
                punisher == null
        ) {

            return null;
        }

        return punisher == firstBranch
                ? secondBranch
                : firstBranch;
    }

    public EarthlyBranchPosition punisherPosition() {

        EarthlyBranch punisher =
                punisherBranch();

        if (
                punisher == null
        ) {

            return null;
        }

        return firstBranch == punisher
                ? firstPosition
                : secondPosition;
    }

    public EarthlyBranchPosition punishedPosition() {

        EarthlyBranch punished =
                punishedBranch();

        if (
                punished == null
        ) {

            return null;
        }

        return firstBranch == punished
                ? firstPosition
                : secondPosition;
    }
}