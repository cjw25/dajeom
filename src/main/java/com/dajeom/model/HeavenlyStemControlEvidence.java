package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemClashType;
import com.dajeom.type.HeavenlyStemControlRelationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.Objects;

/**
 * 정밀화 8차-2.
 *
 * 원국에서 실제 발견된 하나의
 * 천간 충·극 관계 Evidence.
 *
 * =================================================
 *
 * controller
 *
 * 실제로 극하는 쪽.
 *
 * =================================================
 *
 * controlled
 *
 * 실제로 극을 받는 쪽.
 *
 * =================================================
 *
 * 예:
 *
 * 年干 庚
 * 日干 甲
 *
 * Metal controls Wood
 *
 * 따라서:
 *
 * controllerStem
 * = 庚
 *
 * controlledStem
 * = 甲
 *
 * relationType
 * = CLASH
 *
 * clashType
 * = JIA_GENG
 *
 * =================================================
 *
 * 일반 극제 예:
 *
 * 甲 -> 戊
 *
 * Wood controls Earth
 *
 * 천간충은 아니므로:
 *
 * relationType
 * = CONTROL_ONLY
 *
 * clashType
 * = null
 *
 * =================================================
 *
 * 이 단계에서는:
 *
 * - 이 충이 좋은가
 * - 나쁜가
 * - 용신을 손상하는가
 * - 실제 사건으로 나타나는가
 *
 * 를 결정하지 않는다.
 */
public record HeavenlyStemControlEvidence(

        HeavenlyStemControlRelationType relationType,

        HeavenlyStemClashType clashType,

        VisibleStemPosition controllerPosition,

        HeavenlyStem controllerStem,

        VisibleStemPosition controlledPosition,

        HeavenlyStem controlledStem,

        int positionDistance,

        boolean adjacent,

        boolean dayMasterController,

        boolean dayMasterControlled

) {

    public HeavenlyStemControlEvidence {

        Objects.requireNonNull(
                relationType,
                "천간 충·극 관계 유형이 필요합니다."
        );

        Objects.requireNonNull(
                controllerPosition,
                "극하는 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                controllerStem,
                "극하는 천간이 필요합니다."
        );

        Objects.requireNonNull(
                controlledPosition,
                "극받는 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                controlledStem,
                "극받는 천간이 필요합니다."
        );

        if (
                controllerPosition
                        == controlledPosition
        ) {

            throw new IllegalArgumentException(
                    "동일 위치의 천간끼리는 극제 Evidence를 만들 수 없습니다."
            );
        }

        if (
                controllerStem == controlledStem
        ) {

            throw new IllegalArgumentException(
                    "동일 천간끼리는 극제 관계를 만들 수 없습니다."
            );
        }

        if (
                relationType.clash()
        ) {

            if (
                    clashType == null
            ) {

                throw new IllegalArgumentException(
                        "천간충 Evidence에는 ClashType이 필요합니다."
                );
            }

            if (
                    !clashType.matches(
                            controllerStem,
                            controlledStem
                    )
            ) {

                throw new IllegalArgumentException(
                        "천간충 유형과 실제 천간 Pair가 일치하지 않습니다."
                );
            }

            if (
                    clashType.controllerStem()
                            != controllerStem

                            || clashType.controlledStem()
                            != controlledStem
            ) {

                throw new IllegalArgumentException(
                        "천간충의 극제 방향이 ClashType과 일치하지 않습니다."
                );
            }

        } else {

            if (
                    clashType != null
            ) {

                throw new IllegalArgumentException(
                        "일반 극제 Evidence에는 ClashType을 지정할 수 없습니다."
                );
            }
        }

        int expectedDistance =
                Math.abs(
                        controllerPosition.ordinal()
                                - controlledPosition.ordinal()
                );

        if (
                positionDistance
                        != expectedDistance
        ) {

            throw new IllegalArgumentException(
                    "천간 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "천간 인접 상태가 실제 위치 거리와 일치하지 않습니다."
            );
        }

        boolean expectedDayMasterController =
                controllerPosition
                        == VisibleStemPosition.DAY;

        boolean expectedDayMasterControlled =
                controlledPosition
                        == VisibleStemPosition.DAY;

        if (
                dayMasterController
                        != expectedDayMasterController
        ) {

            throw new IllegalArgumentException(
                    "일간 극제 주체 상태가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                dayMasterControlled
                        != expectedDayMasterControlled
        ) {

            throw new IllegalArgumentException(
                    "일간 피극 상태가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                dayMasterController
                        && dayMasterControlled
        ) {

            throw new IllegalArgumentException(
                    "하나의 극제 Evidence에서 일간이 동시에 극하는 쪽과 극받는 쪽일 수 없습니다."
            );
        }
    }

    public boolean clash() {

        return relationType.clash();
    }

    public boolean controlOnly() {

        return relationType.controlOnly();
    }

    public boolean dayMasterInvolved() {

        return dayMasterController
                || dayMasterControlled;
    }

    /**
     * 일간이 직접 극을 받는 관계.
     */
    public boolean dayMasterUnderControl() {

        return dayMasterControlled;
    }

    /**
     * 일간이 다른 천간을 직접 극하는 관계.
     */
    public boolean dayMasterExertsControl() {

        return dayMasterController;
    }

    public boolean adjacentToDayMaster() {

        return adjacent
                && dayMasterInvolved();
    }

    public boolean involves(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return controllerStem == stem
                || controlledStem == stem;
    }

    public boolean involves(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return controllerPosition == position
                || controlledPosition == position;
    }

    public boolean involves(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return controllerElement() == element
                || controlledElement() == element;
    }

    public FiveElement controllerElement() {

        return elementOf(
                controllerStem
        );
    }

    public FiveElement controlledElement() {

        return elementOf(
                controlledStem
        );
    }

    /**
     * 이후 8-10에서 UsefulGodAnalysis와 연결할 때:
     *
     * controllerElement가
     * controlledElement를 극한다는 방향성을
     * 그대로 사용할 수 있다.
     */
    public boolean controlsElement(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return controlledElement()
                == element;
    }

    private static FiveElement elementOf(
            HeavenlyStem stem
    ) {

        return switch (
                stem
                ) {

            case JIA,
                 YI ->
                    FiveElement.WOOD;

            case BING,
                 DING ->
                    FiveElement.FIRE;

            case WU,
                 JI ->
                    FiveElement.EARTH;

            case GENG,
                 XIN ->
                    FiveElement.METAL;

            case REN,
                 GUI ->
                    FiveElement.WATER;
        };
    }
}