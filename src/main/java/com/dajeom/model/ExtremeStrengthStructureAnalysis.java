package com.dajeom.model;

import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FollowWeakDirection;

import java.util.Objects;

/**
 * 정밀화 4차-7.
 *
 * 일반적인 신강·신약 판정 전에
 * 극단 구조 여부를 검토한 결과.
 *
 * 주의:
 *
 * followStrongCandidate
 * followWeakCandidate
 *
 * 는 실제 종격 확정값이 아니다.
 *
 * 정밀화 5차 격국에서:
 *
 * - 월령
 * - 투간
 * - 격의 성립
 * - 파격
 * - 구조 순도
 *
 * 등을 추가로 검토해야 한다.
 */
public record ExtremeStrengthStructureAnalysis(

        ExtremeStrengthStructureType primaryType,

        boolean extremeStrongCandidate,

        boolean extremeWeakCandidate,

        boolean followStrongCandidate,

        boolean followWeakCandidate,

        FollowWeakDirection followWeakDirection,

        ExtremeStrengthStructureEvidence evidence

) {

    public ExtremeStrengthStructureAnalysis {

        Objects.requireNonNull(
                primaryType,
                "극단 구조 분류가 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                evidence,
                "극단 구조 Evidence가 필요합니다."
        );

        if (
                followStrongCandidate
                        && !extremeStrongCandidate
        ) {

            throw new IllegalArgumentException(
                    "종강 후보는 극강 후보 조건을 먼저 만족해야 합니다."
            );
        }

        if (
                followWeakCandidate
                        && !extremeWeakCandidate
        ) {

            throw new IllegalArgumentException(
                    "종약 후보는 극약 후보 조건을 먼저 만족해야 합니다."
            );
        }

        if (
                followStrongCandidate
                        && followWeakCandidate
        ) {

            throw new IllegalArgumentException(
                    "종강 후보와 종약 후보가 동시에 성립할 수 없습니다."
            );
        }

        if (
                extremeStrongCandidate
                        && extremeWeakCandidate
        ) {

            throw new IllegalArgumentException(
                    "극강 후보와 극약 후보가 동시에 성립할 수 없습니다."
            );
        }

        if (
                !followWeakCandidate
                        && !followWeakDirection.none()
        ) {

            throw new IllegalArgumentException(
                    "종약 후보가 아니면 종약 방향은 NONE이어야 합니다."
            );
        }

        if (
                followWeakCandidate
                        && followWeakDirection.none()
        ) {

            throw new IllegalArgumentException(
                    "종약 후보이면 종약 방향이 필요합니다."
            );
        }

        validatePrimaryType(
                primaryType,
                extremeStrongCandidate,
                extremeWeakCandidate,
                followStrongCandidate,
                followWeakCandidate
        );
    }

    private static void validatePrimaryType(

            ExtremeStrengthStructureType primaryType,

            boolean extremeStrongCandidate,

            boolean extremeWeakCandidate,

            boolean followStrongCandidate,

            boolean followWeakCandidate

    ) {

        switch (
                primaryType
        ) {

            case NORMAL -> {

                if (
                        extremeStrongCandidate
                                || extremeWeakCandidate
                                || followStrongCandidate
                                || followWeakCandidate
                ) {

                    throw new IllegalArgumentException(
                            "극단 후보가 존재하는데 primaryType이 NORMAL입니다."
                    );
                }
            }

            case EXTREME_STRONG_CANDIDATE -> {

                if (
                        !extremeStrongCandidate
                                || followStrongCandidate
                ) {

                    throw new IllegalArgumentException(
                            "극강 후보 primaryType과 상태가 일치하지 않습니다."
                    );
                }
            }

            case EXTREME_WEAK_CANDIDATE -> {

                if (
                        !extremeWeakCandidate
                                || followWeakCandidate
                ) {

                    throw new IllegalArgumentException(
                            "극약 후보 primaryType과 상태가 일치하지 않습니다."
                    );
                }
            }

            case FOLLOW_STRONG_CANDIDATE -> {

                if (
                        !followStrongCandidate
                ) {

                    throw new IllegalArgumentException(
                            "종강 후보 primaryType과 상태가 일치하지 않습니다."
                    );
                }
            }

            case FOLLOW_WEAK_CANDIDATE -> {

                if (
                        !followWeakCandidate
                ) {

                    throw new IllegalArgumentException(
                            "종약 후보 primaryType과 상태가 일치하지 않습니다."
                    );
                }
            }
        }
    }

    public boolean normalStructure() {

        return primaryType.normal();
    }

    public boolean requiresExtremeReview() {

        return !normalStructure();
    }

    /**
     * 이후 5차 격국에서
     * 종격 가능성을 추가 검토해야 하는지.
     */
    public boolean requiresFollowStructureReview() {

        return followStrongCandidate
                || followWeakCandidate;
    }
}