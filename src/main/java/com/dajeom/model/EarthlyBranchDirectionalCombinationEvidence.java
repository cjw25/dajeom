package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-5.
 *
 * 원국에서 발견된 하나의
 * 방합 / 부분 방합 Evidence.
 *
 * =================================================
 *
 * COMPLETE
 *
 * 세 지지를 모두 사용한다.
 *
 * 예:
 *
 * 寅卯辰
 *
 * =================================================
 *
 * PARTIAL_WITH_CARDINAL
 * PARTIAL_WITHOUT_CARDINAL
 *
 * 두 지지만 사용한다.
 *
 * thirdPosition
 * thirdBranch
 *
 * 는 null이다.
 *
 * =================================================
 *
 * coveredByCompleteCombination
 *
 * Pair Evidence가 동일 方의
 * 완전 방합 내부에 이미 포함되어 있는가.
 *
 * 예:
 *
 * YEAR 寅
 * MONTH 卯
 * DAY 辰
 *
 * 이면:
 *
 * 寅卯辰 COMPLETE
 *
 * 뿐 아니라:
 *
 * 寅卯
 * 卯辰
 * 寅辰
 *
 * Pair 관계도 구조적으로 존재한다.
 *
 * Evidence는 삭제하지 않되
 * 세 Pair에는:
 *
 * coveredByCompleteCombination = true
 *
 * 를 지정한다.
 *
 * 이후 9차에서 완전 方을 평가하면서
 * Pair를 다시 중복 계산하지 않을 수 있다.
 */
public record EarthlyBranchDirectionalCombinationEvidence(

        EarthlyBranchDirectionalCombinationType combinationType,

        EarthlyBranchDirectionalCombinationForm form,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        EarthlyBranchPosition thirdPosition,

        EarthlyBranch thirdBranch,

        boolean coveredByCompleteCombination

) {

    public EarthlyBranchDirectionalCombinationEvidence {

        Objects.requireNonNull(
                combinationType,
                "방합 유형이 필요합니다."
        );

        Objects.requireNonNull(
                form,
                "방합 구성 형태가 필요합니다."
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
                    "방합 Evidence는 원국 위치 순서대로 저장해야 합니다."
            );
        }

        if (
                form.complete()
        ) {

            Objects.requireNonNull(
                    thirdPosition,
                    "완전 방합에는 세 번째 지지 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    thirdBranch,
                    "완전 방합에는 세 번째 지지가 필요합니다."
            );

            if (
                    secondPosition.ordinal()
                            >= thirdPosition.ordinal()
            ) {

                throw new IllegalArgumentException(
                        "완전 방합의 세 위치는 원국 순서대로 저장해야 합니다."
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
                        "세 지지가 지정된 방합과 일치하지 않습니다."
                );
            }

            if (
                    coveredByCompleteCombination
            ) {

                throw new IllegalArgumentException(
                        "완전 방합 Evidence 자체에는 coveredByCompleteCombination을 지정할 수 없습니다."
                );
            }

        } else {

            if (
                    thirdPosition != null
                            || thirdBranch != null
            ) {

                throw new IllegalArgumentException(
                        "부분 방합 Evidence에는 세 번째 지지를 지정할 수 없습니다."
                );
            }

            if (
                    !combinationType.matchesPair(
                            firstBranch,
                            secondBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "두 지지가 지정된 방합의 구성원과 일치하지 않습니다."
                );
            }

            boolean cardinalIncluded =
                    combinationType
                            .pairContainsCardinal(
                                    firstBranch,
                                    secondBranch
                            );

            if (
                    form.partialWithCardinal()
                            != cardinalIncluded
            ) {

                throw new IllegalArgumentException(
                        "왕지 포함 여부와 부분 방합 형태가 일치하지 않습니다."
                );
            }

            if (
                    form.partialWithoutCardinal()

                            && !combinationType
                            .openingClosingPair(
                                    firstBranch,
                                    secondBranch
                            )
            ) {

                throw new IllegalArgumentException(
                        "왕지 없는 부분 방합은 계절 시작지+마무리지 Pair여야 합니다."
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

    public boolean partialWithCardinal() {

        return form.partialWithCardinal();
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

    /**
     * Evidence가 차지하는 원국 위치 범위.
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
     * 구성 지지가 원국에서 연속 위치에 있는가.
     *
     * Pair:
     *
     * YEAR-MONTH
     * MONTH-DAY
     * DAY-HOUR
     *
     * 완전 방합:
     *
     * YEAR-MONTH-DAY
     * MONTH-DAY-HOUR
     */
    public boolean compact() {

        return positionSpan()
                == memberCount() - 1;
    }

    public boolean subordinateToCompleteCombination() {

        return form.partial()
                && coveredByCompleteCombination;
    }

    public boolean standalonePartialRelation() {

        return form.partial()
                && !coveredByCompleteCombination;
    }
}