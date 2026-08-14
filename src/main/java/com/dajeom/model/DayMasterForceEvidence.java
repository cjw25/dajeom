package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterForcePosition;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.Objects;

/**
 * 정밀화 4차-4.
 *
 * 일간 주변의 하나의 세력 형성 근거.
 *
 * source가 천간이면:
 *
 * - branch = null
 * - hiddenStemRole = null
 * - visibleStemSource = true
 *
 * source가 지장간이면:
 *
 * - branch 존재
 * - hiddenStemRole 존재
 * - visibleStemSource = false
 *
 * rawWeight는 위치를 비교하기 위한
 * 최종 신강 점수가 아니다.
 *
 * 천간의 경우 3-1 raw base weight,
 * 지장간의 경우 3-2 hidden raw weight를
 * 그대로 보존한다.
 */
public record DayMasterForceEvidence(

        DayMasterForcePosition position,

        DayMasterElementRole role,

        FiveElement element,

        HeavenlyStem stem,

        boolean visibleStemSource,

        EarthlyBranch branch,

        HiddenStemRole hiddenStemRole,

        int rawWeight,

        boolean exposed

) {

    public DayMasterForceEvidence {

        Objects.requireNonNull(
                position,
                "세력 위치가 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 역할이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        if (
                rawWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "세력 원시 가중치는 0보다 커야 합니다."
            );
        }

        if (
                visibleStemSource
        ) {

            if (
                    !position.stemPosition()
            ) {

                throw new IllegalArgumentException(
                        "표면 천간 Evidence의 위치가 천간 위치가 아닙니다."
                );
            }

            if (
                    branch != null
                            || hiddenStemRole != null
            ) {

                throw new IllegalArgumentException(
                        "표면 천간 Evidence에는 지지/지장간 역할을 지정할 수 없습니다."
                );
            }

            /*
             * 천간은 이미 겉으로 드러난 source이므로
             * exposed는 반드시 true다.
             */
            if (
                    !exposed
            ) {

                throw new IllegalArgumentException(
                        "표면 천간 Evidence는 exposed=true여야 합니다."
                );
            }

        } else {

            if (
                    !position.branchPosition()
            ) {

                throw new IllegalArgumentException(
                        "지장간 Evidence의 위치가 지지 위치가 아닙니다."
                );
            }

            Objects.requireNonNull(
                    branch,
                    "지장간 Evidence에는 지지가 필요합니다."
            );

            Objects.requireNonNull(
                    hiddenStemRole,
                    "지장간 Evidence에는 역할이 필요합니다."
            );
        }
    }

    /**
     * 비겁 또는 인성.
     */
    public boolean supportive() {

        return role.supportive();
    }

    /**
     * 식상·재성·관살.
     *
     * 신강/신약 관점에서
     * 일간 지원 세력의 반대편이다.
     */
    public boolean opposing() {

        return !role.supportive();
    }

    public boolean companion() {

        return role.companion();
    }

    public boolean resource() {

        return role.resource();
    }

    public boolean output() {

        return role.output();
    }

    public boolean wealth() {

        return role.wealth();
    }

    public boolean officer() {

        return role.officer();
    }

    public boolean hiddenStemSource() {

        return !visibleStemSource;
    }

    public boolean mainQi() {

        return hiddenStemRole
                != null
                && hiddenStemRole.isMain();
    }

    public boolean middleQi() {

        return hiddenStemRole
                != null
                && hiddenStemRole.isMiddle();
    }

    public boolean residualQi() {

        return hiddenStemRole
                != null
                && hiddenStemRole.isResidual();
    }

    /**
     * 구조적으로 비교적 명확한
     * 지원/압력 source.
     *
     * - 천간에 직접 드러난 경우
     * - 지지의 정기인 경우
     *
     * 를 강한 구조적 source로 취급한다.
     *
     * 이것은 최종 가중치가 아니다.
     */
    public boolean structurallyStrongSource() {

        return visibleStemSource
                || mainQi();
    }

    /**
     * 지장간이 실제 천간으로
     * 투출까지 한 source인지.
     */
    public boolean exposedHiddenStem() {

        return hiddenStemSource()
                && exposed;
    }

    public boolean centralPosition() {

        return position.centralPosition();
    }
}