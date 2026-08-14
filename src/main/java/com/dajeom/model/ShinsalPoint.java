package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalPointKind;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 17차-1.
 *
 * 신살 Rule이 검사하는
 * 하나의 천간 또는 지지 Point.
 *
 * =================================================
 *
 * NATAL
 *
 * palacePosition 필요.
 *
 * 예:
 *
 * NATAL / DAY / HEAVENLY_STEM / 丁
 *
 * NATAL / MONTH / EARTHLY_BRANCH / 申
 *
 * =================================================
 *
 * DECADE
 *
 * decadeSequenceNumber 필요.
 *
 * =================================================
 *
 * ANNUAL
 *
 * sajuYear 필요.
 *
 * =================================================
 *
 * MONTHLY
 *
 * sajuYear
 * +
 * monthIndex
 *
 * 필요.
 *
 * =================================================
 *
 * 하나의 Point에는:
 *
 * HeavenlyStem
 * 또는
 * EarthlyBranch
 *
 * 둘 중 하나만 존재한다.
 */
public record ShinsalPoint(

        ShinsalLayer layer,

        ShinsalPointKind kind,

        Optional<PalacePosition> palacePosition,

        Optional<Integer> decadeSequenceNumber,

        Optional<Integer> sajuYear,

        Optional<Integer> monthIndex,

        Optional<HeavenlyStem> stem,

        Optional<EarthlyBranch> branch

) {

    public ShinsalPoint {

        Objects.requireNonNull(
                layer,
                "ShinsalPoint에는 Layer가 필요합니다."
        );

        Objects.requireNonNull(
                kind,
                "ShinsalPoint에는 Kind가 필요합니다."
        );

        palacePosition =
                palacePosition == null
                        ? Optional.empty()
                        : palacePosition;

        decadeSequenceNumber =
                decadeSequenceNumber == null
                        ? Optional.empty()
                        : decadeSequenceNumber;

        sajuYear =
                sajuYear == null
                        ? Optional.empty()
                        : sajuYear;

        monthIndex =
                monthIndex == null
                        ? Optional.empty()
                        : monthIndex;

        stem =
                stem == null
                        ? Optional.empty()
                        : stem;

        branch =
                branch == null
                        ? Optional.empty()
                        : branch;

        /*
         * =================================================
         * 천간 / 지지
         * =================================================
         */

        if (
                kind.heavenlyStem()
        ) {

            if (
                    stem.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "HEAVENLY_STEM Point에는 HeavenlyStem이 필요합니다."
                );
            }

            if (
                    branch.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "HEAVENLY_STEM Point에는 EarthlyBranch를 넣을 수 없습니다."
                );
            }
        }

        if (
                kind.earthlyBranch()
        ) {

            if (
                    branch.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "EARTHLY_BRANCH Point에는 EarthlyBranch가 필요합니다."
                );
            }

            if (
                    stem.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "EARTHLY_BRANCH Point에는 HeavenlyStem을 넣을 수 없습니다."
                );
            }
        }

        /*
         * =================================================
         * Layer별 위치정보
         * =================================================
         */

        if (
                layer.natal()
        ) {

            if (
                    palacePosition.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "NATAL ShinsalPoint에는 PalacePosition이 필요합니다."
                );
            }

            if (
                    decadeSequenceNumber.isPresent()
                            || sajuYear.isPresent()
                            || monthIndex.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "NATAL ShinsalPoint에는 운 위치정보를 넣을 수 없습니다."
                );
            }
        }

        if (
                layer.decade()
        ) {

            if (
                    decadeSequenceNumber.isEmpty()
                            || decadeSequenceNumber.orElseThrow() < 1
            ) {

                throw new IllegalArgumentException(
                        "DECADE ShinsalPoint에는 유효한 대운 순번이 필요합니다."
                );
            }

            if (
                    palacePosition.isPresent()
                            || sajuYear.isPresent()
                            || monthIndex.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "DECADE ShinsalPoint의 위치정보가 잘못되었습니다."
                );
            }
        }

        if (
                layer.annual()
        ) {

            if (
                    sajuYear.isEmpty()
                            || sajuYear.orElseThrow() < 1
            ) {

                throw new IllegalArgumentException(
                        "ANNUAL ShinsalPoint에는 명리연도가 필요합니다."
                );
            }

            if (
                    palacePosition.isPresent()
                            || decadeSequenceNumber.isPresent()
                            || monthIndex.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "ANNUAL ShinsalPoint의 위치정보가 잘못되었습니다."
                );
            }
        }

        if (
                layer.monthly()
        ) {

            if (
                    sajuYear.isEmpty()
                            || sajuYear.orElseThrow() < 1
            ) {

                throw new IllegalArgumentException(
                        "MONTHLY ShinsalPoint에는 명리연도가 필요합니다."
                );
            }

            if (
                    monthIndex.isEmpty()
                            || monthIndex.orElseThrow() < 0
                            || monthIndex.orElseThrow() > 11
            ) {

                throw new IllegalArgumentException(
                        "MONTHLY ShinsalPoint의 monthIndex는 0~11이어야 합니다."
                );
            }

            if (
                    palacePosition.isPresent()
                            || decadeSequenceNumber.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "MONTHLY ShinsalPoint의 위치정보가 잘못되었습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Factory - NATAL
     * =================================================
     */

    public static ShinsalPoint natalStem(

            PalacePosition position,

            HeavenlyStem stem

    ) {

        return new ShinsalPoint(
                ShinsalLayer.NATAL,
                ShinsalPointKind.HEAVENLY_STEM,
                Optional.of(
                        Objects.requireNonNull(position)
                ),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(stem)
                ),
                Optional.empty()
        );
    }

    public static ShinsalPoint natalBranch(

            PalacePosition position,

            EarthlyBranch branch

    ) {

        return new ShinsalPoint(
                ShinsalLayer.NATAL,
                ShinsalPointKind.EARTHLY_BRANCH,
                Optional.of(
                        Objects.requireNonNull(position)
                ),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(branch)
                )
        );
    }

    /*
     * =================================================
     * Factory - DECADE
     * =================================================
     */

    public static ShinsalPoint decadeStem(

            int sequenceNumber,

            HeavenlyStem stem

    ) {

        return new ShinsalPoint(
                ShinsalLayer.DECADE,
                ShinsalPointKind.HEAVENLY_STEM,
                Optional.empty(),
                Optional.of(sequenceNumber),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(stem)
                ),
                Optional.empty()
        );
    }

    public static ShinsalPoint decadeBranch(

            int sequenceNumber,

            EarthlyBranch branch

    ) {

        return new ShinsalPoint(
                ShinsalLayer.DECADE,
                ShinsalPointKind.EARTHLY_BRANCH,
                Optional.empty(),
                Optional.of(sequenceNumber),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(branch)
                )
        );
    }

    /*
     * =================================================
     * Factory - ANNUAL
     * =================================================
     */

    public static ShinsalPoint annualStem(

            int sajuYear,

            HeavenlyStem stem

    ) {

        return new ShinsalPoint(
                ShinsalLayer.ANNUAL,
                ShinsalPointKind.HEAVENLY_STEM,
                Optional.empty(),
                Optional.empty(),
                Optional.of(sajuYear),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(stem)
                ),
                Optional.empty()
        );
    }

    public static ShinsalPoint annualBranch(

            int sajuYear,

            EarthlyBranch branch

    ) {

        return new ShinsalPoint(
                ShinsalLayer.ANNUAL,
                ShinsalPointKind.EARTHLY_BRANCH,
                Optional.empty(),
                Optional.empty(),
                Optional.of(sajuYear),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(branch)
                )
        );
    }

    /*
     * =================================================
     * Factory - MONTHLY
     * =================================================
     */

    public static ShinsalPoint monthlyStem(

            int sajuYear,

            int monthIndex,

            HeavenlyStem stem

    ) {

        return new ShinsalPoint(
                ShinsalLayer.MONTHLY,
                ShinsalPointKind.HEAVENLY_STEM,
                Optional.empty(),
                Optional.empty(),
                Optional.of(sajuYear),
                Optional.of(monthIndex),
                Optional.of(
                        Objects.requireNonNull(stem)
                ),
                Optional.empty()
        );
    }

    public static ShinsalPoint monthlyBranch(

            int sajuYear,

            int monthIndex,

            EarthlyBranch branch

    ) {

        return new ShinsalPoint(
                ShinsalLayer.MONTHLY,
                ShinsalPointKind.EARTHLY_BRANCH,
                Optional.empty(),
                Optional.empty(),
                Optional.of(sajuYear),
                Optional.of(monthIndex),
                Optional.empty(),
                Optional.of(
                        Objects.requireNonNull(branch)
                )
        );
    }

    /*
     * =================================================
     * Convenience
     * =================================================
     */

    public HeavenlyStem requireStem() {

        return stem.orElseThrow(
                () ->
                        new IllegalStateException(
                                "현재 ShinsalPoint는 천간 Point가 아닙니다."
                        )
        );
    }

    public EarthlyBranch requireBranch() {

        return branch.orElseThrow(
                () ->
                        new IllegalStateException(
                                "현재 ShinsalPoint는 지지 Point가 아닙니다."
                        )
        );
    }

    public boolean natal() {

        return layer.natal();
    }

    public boolean luck() {

        return !layer.natal();
    }

    /**
     * 중복 검증과 로그에 사용할 안정적인 위치 식별자.
     */
    public String locationKey() {

        if (
                layer.natal()
        ) {

            return "NATAL:"
                    + palacePosition.orElseThrow().name()
                    + ":"
                    + kind.name();
        }

        if (
                layer.decade()
        ) {

            return "DECADE:"
                    + decadeSequenceNumber.orElseThrow()
                    + ":"
                    + kind.name();
        }

        if (
                layer.annual()
        ) {

            return "ANNUAL:"
                    + sajuYear.orElseThrow()
                    + ":"
                    + kind.name();
        }

        return "MONTHLY:"
                + sajuYear.orElseThrow()
                + ":"
                + monthIndex.orElseThrow()
                + ":"
                + kind.name();
    }
}