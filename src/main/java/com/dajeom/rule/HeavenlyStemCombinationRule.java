package com.dajeom.rule;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-1.
 *
 * 천간합 관계 탐지 Rule.
 *
 * =================================================
 *
 * 이 Rule은 오직:
 *
 * "두 천간 사이에 五合 관계가 존재하는가"
 *
 * 만 판정한다.
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 실제 합화 성립
 * - 합화 실패
 * - 합거
 * - 합반
 * - 투합
 * - 쟁합
 * - 합으로 인한 원래 오행 기능 상실
 * - 새로운 오행으로 완전 변환
 *
 * 위 내용은 9차에서 처리한다.
 */
@Component
public class HeavenlyStemCombinationRule {

    /**
     * 두 천간 사이의 五合 유형을 찾는다.
     *
     * 순서는 무관하다.
     */
    public Optional<HeavenlyStemCombinationType>
    findCombinationType(

            HeavenlyStem firstStem,

            HeavenlyStem secondStem

    ) {

        Objects.requireNonNull(
                firstStem,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                secondStem,
                "두 번째 천간이 필요합니다."
        );

        if (
                firstStem == secondStem
        ) {

            return Optional.empty();
        }

        for (
                HeavenlyStemCombinationType type
                : HeavenlyStemCombinationType.values()
        ) {

            if (
                    type.matches(
                            firstStem,
                            secondStem
                    )
            ) {

                return Optional.of(
                        type
                );
            }
        }

        return Optional.empty();
    }

    public boolean combines(

            HeavenlyStem firstStem,

            HeavenlyStem secondStem

    ) {

        if (
                firstStem == null
                        || secondStem == null
        ) {

            return false;
        }

        return findCombinationType(
                firstStem,
                secondStem
        )
                .isPresent();
    }

    /**
     * 원국의 위치 간 거리.
     *
     * YEAR  -> 0
     * MONTH -> 1
     * DAY   -> 2
     * HOUR  -> 3
     */
    public int positionDistance(

            VisibleStemPosition firstPosition,

            VisibleStemPosition secondPosition

    ) {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 위치가 필요합니다."
        );

        if (
                firstPosition == secondPosition
        ) {

            return 0;
        }

        return Math.abs(
                firstPosition.ordinal()
                        - secondPosition.ordinal()
        );
    }

    public boolean adjacent(

            VisibleStemPosition firstPosition,

            VisibleStemPosition secondPosition

    ) {

        return positionDistance(
                firstPosition,
                secondPosition
        ) == 1;
    }

    public boolean dayMasterInvolved(

            VisibleStemPosition firstPosition,

            VisibleStemPosition secondPosition

    ) {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 위치가 필요합니다."
        );

        return firstPosition
                == VisibleStemPosition.DAY

                || secondPosition
                == VisibleStemPosition.DAY;
    }
}