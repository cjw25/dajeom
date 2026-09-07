package com.dajeom.calculation;

import com.dajeom.domain.DaewoonPeriod;
import com.dajeom.domain.Ganji;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 대운 실제 기간 계산기.
 *
 * 이 클래스는:
 *
 * - 순행/역행을 결정하지 않는다.
 * - 대운 간지를 계산하지 않는다.
 * - 절입거리를 계산하지 않는다.
 * - 대운수를 계산하지 않는다.
 * - 첫 대운 시각을 다시 계산하지 않는다.
 *
 * 이미 앞 단계에서 확정된:
 *
 * 1. 첫 대운 정밀 시작시각
 * 2. 대운 간지 Sequence
 *
 * 를 받아서
 * 실제 10년 단위 기간으로 변환한다.
 */
@Component
public class DaewoonPeriodCalculator {

    private static final int YEARS_PER_PERIOD = 10;

    /**
     * 대운 간지 Sequence 전체를
     * 실제 기간으로 변환한다.
     *
     * 예:
     *
     * firstDaewoonStartDateTime
     * =
     * 1999-02-12T22:49:30.702
     *
     * sequence
     * =
     * 己未, 戊午, 丁巳 ...
     *
     * 결과:
     *
     * 己未
     * [1999-02-12T22:49:30.702,
     *  2009-02-12T22:49:30.702)
     *
     * 戊午
     * [2009-02-12T22:49:30.702,
     *  2019-02-12T22:49:30.702)
     */
    public List<DaewoonPeriod> calculate(

            LocalDateTime firstDaewoonStartDateTime,

            List<Ganji> daewoonSequence

    ) {

        Objects.requireNonNull(
                firstDaewoonStartDateTime,
                "첫 대운 정밀 시작시각은 필수입니다."
        );

        Objects.requireNonNull(
                daewoonSequence,
                "대운 간지 Sequence는 필수입니다."
        );

        if (daewoonSequence.isEmpty()) {

            return List.of();
        }

        List<DaewoonPeriod> result =
                new ArrayList<>(
                        daewoonSequence.size()
                );

        for (
                int index = 0;
                index < daewoonSequence.size();
                index++
        ) {

            Ganji ganji =
                    Objects.requireNonNull(
                            daewoonSequence.get(index),
                            "대운 간지 Sequence에 null이 포함될 수 없습니다."
                    );

            long startYearOffset =
                    (long) index
                            * YEARS_PER_PERIOD;

            long endYearOffset =
                    (long) (index + 1)
                            * YEARS_PER_PERIOD;

            LocalDateTime startDateTime =
                    firstDaewoonStartDateTime
                            .plusYears(
                                    startYearOffset
                            );

            LocalDateTime endDateTime =
                    firstDaewoonStartDateTime
                            .plusYears(
                                    endYearOffset
                            );

            result.add(
                    new DaewoonPeriod(
                            index + 1,
                            ganji,
                            startDateTime,
                            endDateTime
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 이미 생성된 대운 기간 중
     * 특정 시점에 해당하는 대운을 찾는다.
     *
     * 첫 대운 시작 전이면 Optional.empty().
     *
     * 생성된 마지막 대운 이후여도 Optional.empty().
     */
    public Optional<DaewoonPeriod> findAt(

            List<DaewoonPeriod> periods,

            LocalDateTime targetDateTime

    ) {

        Objects.requireNonNull(
                periods,
                "대운 기간 목록은 필수입니다."
        );

        Objects.requireNonNull(
                targetDateTime,
                "대운 조회 기준시각은 필수입니다."
        );

        for (DaewoonPeriod period : periods) {

            DaewoonPeriod normalizedPeriod =
                    Objects.requireNonNull(
                            period,
                            "대운 기간 목록에 null이 포함될 수 없습니다."
                    );

            if (
                    normalizedPeriod.contains(
                            targetDateTime
                    )
            ) {

                return Optional.of(
                        normalizedPeriod
                );
            }
        }

        return Optional.empty();
    }

    /**
     * 기간 생성과 특정 시점 조회를
     * 한 번에 수행하는 편의 메서드.
     */
    public Optional<DaewoonPeriod> calculateAndFindAt(

            LocalDateTime firstDaewoonStartDateTime,

            List<Ganji> daewoonSequence,

            LocalDateTime targetDateTime

    ) {

        List<DaewoonPeriod> periods =
                calculate(
                        firstDaewoonStartDateTime,
                        daewoonSequence
                );

        return findAt(
                periods,
                targetDateTime
        );
    }
}