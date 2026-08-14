package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.Objects;

/**
 * 하나의 지지 내부에 존재하는 지장간.
 *
 * 예:
 *
 * 申
 *
 * 庚  정기  60
 * 壬  중기  25
 * 戊  여기  15
 *
 * weight는 다점 엔진 내부에서 사용하는
 * 상대적인 세력 가중치다.
 *
 * 확률이나 운세 발생확률을 의미하지 않는다.
 */
public record HiddenStem(

        HeavenlyStem stem,

        HiddenStemRole role,

        int weight

) {

    public HiddenStem {

        Objects.requireNonNull(
                stem,
                "지장간 천간이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "지장간 역할이 필요합니다."
        );

        if (
                weight <= 0
                        || weight > 100
        ) {

            throw new IllegalArgumentException(
                    "지장간 가중치는 1부터 100 사이여야 합니다: "
                            + weight
            );
        }
    }

    public boolean main() {

        return role.isMain();
    }

    public boolean middle() {

        return role.isMiddle();
    }

    public boolean residual() {

        return role.isResidual();
    }
}