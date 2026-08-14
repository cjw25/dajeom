package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-4.
 *
 * 하나의 삼합 / 반합 / 부분조합 Evidence.
 *
 * =================================================
 *
 * COMPLETE
 *
 * first
 * second
 * third
 *
 * 세 지지를 모두 사용한다.
 *
 * =================================================
 *
 * HALF_WITH_CARDINAL
 * PARTIAL_WITHOUT_CARDINAL
 *
 * first
 * second
 *
 * 두 지지만 사용하며
 * thirdPosition / thirdBranch는 null이다.
 *
 * =================================================
 *
 * coveredByCompleteCombination
 *
 * Pair Evidence가 동일 위치의 완전 삼합 안에
 * 이미 포함되어 있는가.
 *
 * 예:
 *
 * 年申
 * 月子
 * 日辰
 *
 * 이면:
 *
 * 申子辰 COMPLETE
 *
 * 와 동시에:
 *
 * 申子 HALF
 * 子辰 HALF
 * 申辰 PARTIAL
 *
 * 관계 자체는 모두 존재한다.
 *
 * 하지만 세 Pair 모두 완전 삼합 안에
 * 포함되어 있으므로:
 *
 * coveredByCompleteCombination = true
 *
 * 로 저장한다.
 *
 * 이렇게 해야 Evidence는 잃지 않으면서도
 * 이후 단계에서 완전 삼합과 Pair를
 * 중복 점수화하지 않을 수 있다.
 *
 * =================================================
 *
 * 실제 合化·局 성립은 아직 판단하지 않는다.
 */
public record EarthlyBranchThreeCombinationEvidence(

        EarthlyBranchThreeCombinationType combinationType,

        EarthlyBranchThreeCombinationForm form,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        EarthlyBranchPosition thirdPosition,

        EarthlyBranch thirdBranch,

        boolean coveredByCompleteCombination

) {

    public EarthlyBranchThreeCombinationEvidence {

        Objects.requireNonNull(
                combinationType,
                "삼합 유형이 필요합니다."
        );

        Objects.requireNonNull(
                form,
                "삼합 구성 형태가 필요합니다."
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
                    "삼합 Evidence는 원국 위치 순서대로 저장해야 합니다."
            );
        }

        if (
                form.complete()
        ) {

            Objects.requireNonNull(
                    thirdPosition,
                    "완전 삼합에는 세 번째 지지 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    thirdBranch,
                    "완전 삼합에는 세 번째 지지가 필요합니다."
            );

            if (
                    secondPosition.ordinal()
                            >= thirdPosition.ordinal()
            ) {

                throw new IllegalArgumentException(
                        "완전 삼합의 세 위치는 원국 순서대로 저장해야 합니다."
                );
            }

            if (
                    !combinationType.matchesComplete(
                            firstBranch,
                            secondBranch,
                            thirdBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "세 지지가 지정된 완전 삼합국과 일치하지 않습니다."
                );
            }

            if (
                    coveredByCompleteCombination
            ) {

                throw new IllegalArgumentException(
                        "완전 삼합 Evidence 자체에는 coveredByCompleteCombination을 지정할 수 없습니다."
                );
            }

        } else {

            if (
                    thirdPosition != null
                            || thirdBranch != null
            ) {

                throw new IllegalArgumentException(
                        "Pair 형태의 삼합 Evidence에는 세 번째 지지를 지정할 수 없습니다."
                );
            }

            if (
                    !combinationType.matchesPair(
                            firstBranch,
                            secondBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "두 지지가 지정된 삼합국의 부분 구성과 일치하지 않습니다."
                );
            }

            boolean cardinalIncluded =
                    combinationType
                            .pairContainsCardinal(
                                    firstBranch,
                                    secondBranch
                            );

            if (
                    form.halfWithCardinal()
                            != cardinalIncluded
            ) {

                throw new IllegalArgumentException(
                        "왕지 포함 여부와 삼합 Pair 형태가 일치하지 않습니다."
                );
            }

            if (
                    form.partialWithoutCardinal()

                            && !combinationType
                            .birthStoragePair(
                                    firstBranch,
                                    secondBranch
                            )
            ) {

                throw new IllegalArgumentException(
                        "왕지 없는 부분조합은 생지+고지 Pair여야 합니다."
                );
            }
        }
    }

    public int memberCount() {

        return form.complete()
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

    public FiveElement nominalTransformationElement() {

        return combinationType
                .nominalTransformationElement();
    }

    public FiveElement transformationTarget() {

        return nominalTransformationElement();
    }

    public boolean completeCombination() {

        return form.complete();
    }

    public boolean halfCombination() {

        return form.halfWithCardinal();
    }

    public boolean partialWithoutCardinal() {

        return form.partialWithoutCardinal();
    }

    public boolean cardinalBranchIncluded() {

        return branches()
                .contains(
                        combinationType
                                .cardinalBranch()
                );
    }

    public boolean dayBranchInvolved() {

        return positions()
                .contains(
                        EarthlyBranchPosition.DAY
                );
    }

    public boolean monthBranchInvolved() {

        return positions()
                .contains(
                        EarthlyBranchPosition.MONTH
                );
    }

    public boolean hourBranchInvolved() {

        return positions()
                .contains(
                        EarthlyBranchPosition.HOUR
                );
    }

    public boolean yearBranchInvolved() {

        return positions()
                .contains(
                        EarthlyBranchPosition.YEAR
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

    /**
     * Evidence가 차지하는 원국 위치 범위.
     *
     * 예:
     *
     * YEAR + MONTH
     * -> 1
     *
     * YEAR + DAY
     * -> 2
     *
     * YEAR + MONTH + DAY
     * -> 2
     */
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

    /**
     * 구성 지지들이 원국에서 연속된 위치에 있는가.
     *
     * Pair:
     *
     * YEAR-MONTH
     * MONTH-DAY
     * DAY-HOUR
     *
     * 는 compact.
     *
     * 완전 삼합:
     *
     * YEAR-MONTH-DAY
     * MONTH-DAY-HOUR
     *
     * 는 compact.
     */
    public boolean compact() {

        return positionSpan()
                == memberCount() - 1;
    }

    /**
     * Pair Evidence이며
     * 완전 삼합 내부에 포함된 경우.
     */
    public boolean subordinateToCompleteCombination() {

        return !form.complete()
                && coveredByCompleteCombination;
    }

    /**
     * 완전 삼합에 포함되지 않고
     * 독립적으로 존재하는 Pair Evidence.
     */
    public boolean standalonePartialRelation() {

        return form.pairForm()
                && !coveredByCompleteCombination;
    }
}